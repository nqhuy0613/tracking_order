package com.me.tracking_order.order.service;

import com.me.tracking_order.common.exception.BusinessException;
import com.me.tracking_order.common.exception.ErrorCode;
import com.me.tracking_order.order.dto.customer.request.BuyNowItemRequest;
import com.me.tracking_order.order.dto.customer.request.CreateOrderRequest;
import com.me.tracking_order.order.dto.customer.request.OrderSummaryRequest;
import com.me.tracking_order.order.dto.customer.response.CreateOrderResponse;
import com.me.tracking_order.order.dto.customer.response.OrderResponse;
import com.me.tracking_order.order.dto.customer.response.OrderSummaryResponse;
import com.me.tracking_order.cart.entity.CartItem;
import com.me.tracking_order.catalog.entity.Inventory;
import com.me.tracking_order.catalog.entity.ProductVariant;
import com.me.tracking_order.discount.entity.UserDiscount;
import com.me.tracking_order.order.entity.Order;
import com.me.tracking_order.order.entity.OrderItem;
import com.me.tracking_order.order.enums.OrderSource;
import com.me.tracking_order.order.mapper.OrderMapper;
import com.me.tracking_order.cart.repository.CartItemRepository;
import com.me.tracking_order.catalog.repository.ProductVariantRepository;
import com.me.tracking_order.discount.repository.UserDiscountRepository;
import com.me.tracking_order.order.repository.OrderItemRepository;
import com.me.tracking_order.order.repository.OrderRepository;
import com.me.tracking_order.user.entity.User;
import com.me.tracking_order.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.me.tracking_order.order.enums.OrderSource.CART;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartItemRepository cartItemRepository;
    private final UserDiscountRepository userDiscountRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final OrderMapper orderMapper;

    private record OrderPricing(
            BigDecimal subTotal,
            BigDecimal discountFee,
            BigDecimal shippingFee,
            BigDecimal totalAmount
    ){}

    private record PreparedOrder(
            List<CheckoutLine> checkoutLines,
            UserDiscount userDiscount,
            OrderPricing pricing
    ){}

    private record CheckoutLine(
            ProductVariant productVariant,
            int quantity,
            BigDecimal unitPrice,
            CartItem sourceCartItem
    ) {
    }

    @Transactional(readOnly = true)
    public OrderSummaryResponse getOrderSummary(String username, OrderSummaryRequest request) {

        PreparedOrder preparedOrder = preparedOrder(username, request.getCartItemIds(), null, CART, request.getUserDiscountId());

        OrderPricing pricing = preparedOrder.pricing();

        return OrderSummaryResponse.builder()
                .discountFee(pricing.discountFee)
                .subTotal(pricing.subTotal)
                .shippingFee(pricing.shippingFee)
                .totalAmount(pricing.totalAmount)
                .build();
    }


    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request, String username) {
        PreparedOrder preparedOrder = preparedOrder(username, request.getCartItemIds(), request.getBuyNowItem(), request.getSource(), request.getUserDiscountId());

        OrderPricing pricing = preparedOrder.pricing();

        User user = userRepository.findActiveByUsername(username).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );

        Order order = new Order();
        order.setUser(user);
        order.setSubtotalAmount(pricing.subTotal);
        order.setDiscountAmount(pricing.discountFee);
        order.setShippingFee(pricing.shippingFee);
        order.setTotalAmount(pricing.totalAmount);

        order.setUserDiscount(preparedOrder.userDiscount);

        Order savedOrder = orderRepository.save(order);

        List<CheckoutLine> checkoutLines = preparedOrder.checkoutLines;

        deductInventory(checkoutLines);

        List<OrderItem> orderItems = checkoutLines.stream()
                .map(cartItem ->
                        createOrderItem(savedOrder, cartItem)
                )
                .toList();

        orderItemRepository.saveAll(orderItems);

        checkoutLines.stream()
                .map(CheckoutLine::sourceCartItem)
                .filter(Objects::nonNull)
                .forEach(cartItem ->
                        cartItem.setDeleted(true));


        return CreateOrderResponse.builder()
                .orderId(savedOrder.getId())
                .subTotal(pricing.subTotal)
                .discountFee(pricing.discountFee)
                .shippingFee(pricing.shippingFee)
                .totalAmount(pricing.totalAmount)
                .paymentStatus(savedOrder.getPaymentStatus())
                .build();

    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderDetails(String username, String orderId){
        Order order = orderRepository.findActiveOwnedOrder(
                username,
                orderId
        ).orElseThrow(
                () -> new BusinessException(ErrorCode.ORDER_NOT_FOUND)
        );
        return orderMapper.toResponse(order);
    }

    private List<CheckoutLine> resolveCheckoutLines(
            String username,
            List<String> cartItemIds,
            BuyNowItemRequest buyNowItem,
            OrderSource source
    ) {
        return switch (source) {
            case CART -> resolveCartLines(
                    username,
                    cartItemIds,
                    buyNowItem
            );

            case BUY_NOW -> List.of(
                    resolveBuyNowLine(
                            buyNowItem,
                            cartItemIds
                    )
            );
        };
    }



    private List<CheckoutLine> resolveCartLines(String username, List<String> requestedCartItemIds, BuyNowItemRequest buyNowItemRequest){
        Set<String> cartItemIds = new HashSet<>(requestedCartItemIds);


        // cart item trùng
        if(requestedCartItemIds.size() != cartItemIds.size()) {
            throw new BusinessException(ErrorCode.INVALID_CART_ITEM_SELECTION);
        }


        List<CartItem> cartItems= cartItemRepository.findActiveOwnedCartItems(cartItemIds, username)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CART_ITEM_SELECTION));

        // có cartitem thuộc user khác
        if(cartItems.size() != requestedCartItemIds.size()) {
            throw new BusinessException(ErrorCode.INVALID_CART_ITEM_SELECTION);
        }

        return cartItems.stream()
                .map(x -> new CheckoutLine(
                        x.getProductVariant(),
                        x.getQuantity(),
                        x.getProductVariant().getUnitPrice(),
                        x
                ))
                .toList();
    }

    private CheckoutLine resolveBuyNowLine(
            BuyNowItemRequest buyNowItem,
            List<String> cartItemIds
    ) {
        if (buyNowItem == null
                || cartItemIds != null && !cartItemIds.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.INVALID_ORDER_ITEM_SELECTION
            );
        }

        ProductVariant variant = productVariantRepository
                .findActiveWithInventoryById(
                        buyNowItem.getProductVariantId()
                )
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PRODUCT_UNAVAILABLE
                ));

        return new CheckoutLine(
                variant,
                buyNowItem.getQuantity(),
                variant.getUnitPrice(),
                null
        );
    }

    private void validateCheckoutLine(List<CheckoutLine> checkoutLines) {
        for (CheckoutLine checkoutLine : checkoutLines) {
            ProductVariant variant = checkoutLine.productVariant();
            Inventory inventory = variant.getInventory();

            if (variant.isDeleted()
                    || inventory == null
                    || inventory.isDeleted()) {
                throw new BusinessException(
                        ErrorCode.PRODUCT_UNAVAILABLE
                );
            }

            if (checkoutLine.quantity()
                    > inventory.getQuantityInStock()) {
                throw new BusinessException(
                        ErrorCode.INSUFFICIENT_STOCK
                );
            }
        }
    }

    private BigDecimal calculateSubTotal(List<CheckoutLine> checkoutLines) {
        return checkoutLines.stream()
                .map(checkoutLine -> checkoutLine.unitPrice().multiply(BigDecimal.valueOf(checkoutLine.quantity())))
                // tinh tong
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                // lam tron 2 chu so tp
                .setScale(2, RoundingMode.HALF_UP);
    }

    private UserDiscount getUserDiscount(String discountId, String username) {
        if(discountId != null && !discountId.isBlank()) {
            return userDiscountRepository.findActivedOwnedUserDiscount(
                    username,
                    discountId,
                    LocalDateTime.now()
            ).orElseThrow(
                    ()-> new BusinessException(ErrorCode.DISCOUNT_NOT_AVAILABLE)
            );
        }

        else return null;
    }

    private BigDecimal calculateDiscountFee(UserDiscount userDiscount, BigDecimal subtotal) {
        if(userDiscount == null) return BigDecimal.ZERO;

        BigDecimal discountFee = BigDecimal.ZERO;

        if (subtotal.compareTo(userDiscount.getDiscount().getMinOrderAmount()) >= 0) {
            BigDecimal percentageDiscount = subtotal.multiply(userDiscount.getDiscount().getDiscountPercentage()).divide(
                    BigDecimal.valueOf(100),
                    2,
                    RoundingMode.HALF_UP
            );
            discountFee = percentageDiscount.min(userDiscount.getDiscount().getMaxDiscountAmount());
        }
        return discountFee;
    }

    private PreparedOrder preparedOrder(
           String username,
           List<String> cartItemIds,
           BuyNowItemRequest buyNowItem,
           OrderSource source,
           String userDiscountId
    ){
        List<CheckoutLine> checkoutLines = resolveCheckoutLines(username, cartItemIds, buyNowItem, source);

        validateCheckoutLine(checkoutLines);

        BigDecimal subTotal = calculateSubTotal(checkoutLines);

        UserDiscount userDiscount = getUserDiscount(userDiscountId, username);

        BigDecimal discountFee = calculateDiscountFee(userDiscount, subTotal);

        BigDecimal shippingFee = BigDecimal.valueOf(30000);

        BigDecimal totalAmount = subTotal
                .add(shippingFee)
                .subtract(discountFee)
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        OrderPricing orderPricing = new OrderPricing(
                subTotal,
                discountFee,
                shippingFee,
                totalAmount
        );

        return new PreparedOrder(
                checkoutLines,
                userDiscount,
                orderPricing
        );
    }

    private void deductInventory(
            List<CheckoutLine> checkoutLines
    ) {
        for (CheckoutLine checkoutLine : checkoutLines) {
            Inventory inventory = checkoutLine
                    .productVariant()
                    .getInventory();

            inventory.setQuantityInStock(
                    inventory.getQuantityInStock()
                            - checkoutLine.quantity()
            );
        }
    }

    private OrderItem createOrderItem(Order order, CheckoutLine checkoutLine) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProductVariant(
                checkoutLine.productVariant()
        );
        orderItem.setQuantity(
                checkoutLine.quantity()
        );
        orderItem.setUnitPrice(
                checkoutLine.unitPrice()
        );

        return orderItem;
    }

}
