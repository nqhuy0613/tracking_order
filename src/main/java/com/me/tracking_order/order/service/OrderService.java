package com.me.tracking_order.order.service;

import com.me.tracking_order.cart.entity.Cart;
import com.me.tracking_order.cart.repository.CartRepository;
import com.me.tracking_order.common.exception.BusinessException;
import com.me.tracking_order.common.exception.ErrorCode;
import com.me.tracking_order.order.dto.customer.request.*;
import com.me.tracking_order.order.dto.customer.response.*;
import com.me.tracking_order.cart.entity.CartItem;
import com.me.tracking_order.catalog.entity.Inventory;
import com.me.tracking_order.catalog.entity.ProductVariant;
import com.me.tracking_order.discount.entity.UserDiscount;
import com.me.tracking_order.order.entity.Order;
import com.me.tracking_order.order.entity.OrderItem;
import com.me.tracking_order.order.enums.MyOrderStatus;
import com.me.tracking_order.order.enums.OrderSource;
import com.me.tracking_order.order.mapper.MyOrderMapper;
import com.me.tracking_order.order.mapper.OrderDetailsMapper;
import com.me.tracking_order.cart.repository.CartItemRepository;
import com.me.tracking_order.catalog.repository.ProductVariantRepository;
import com.me.tracking_order.discount.repository.UserDiscountRepository;
import com.me.tracking_order.order.repository.OrderItemRepository;
import com.me.tracking_order.order.repository.OrderRepository;
import com.me.tracking_order.payment.entity.Payment;
import com.me.tracking_order.payment.enums.PaymentMethodStatus;
import com.me.tracking_order.payment.enums.PaymentStatus;
import com.me.tracking_order.payment.repository.PaymentRepository;
import com.me.tracking_order.review.dto.response.ReviewResponse;
import com.me.tracking_order.review.entity.Review;
import com.me.tracking_order.review.mapper.ReviewMapper;
import com.me.tracking_order.review.repository.ReviewRepository;
import com.me.tracking_order.shipment.entity.Shipment;
import com.me.tracking_order.shipment.enums.ShipmentStatus;
import com.me.tracking_order.user.entity.User;
import com.me.tracking_order.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final CartRepository cartRepository;
    private final ReviewRepository reviewRepository;
    private final OrderDetailsMapper orderDetailsMapper;
    private final ReviewMapper reviewMapper;
    private final PaymentRepository paymentRepository;
    private final MyOrderMapper myOrderMapper;

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
    public OrderDetailsResponse getOrderDetails(String username, String orderId){
        Order order = orderRepository.findActiveOwnedOrder(
                username,
                orderId
        ).orElseThrow(
                () -> new BusinessException(ErrorCode.ORDER_NOT_FOUND)
        );
        return orderDetailsMapper.toResponse(order);
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

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String username, MyOrderStatus status) {
        // lay tat ca cac Orders chua xoa,
        List<Order> orders = orderRepository
                .findAllByUser_UsernameAndUser_IsDeletedFalseAndIsDeletedFalseOrderByCreatedAtDesc(username);

        // quyet dinh status cua cac order va loc
        Map<Order, MyOrderStatus> orderStatusMap = orders.stream()
                .collect(Collectors.toMap(
                        order -> order,
                        order -> getOrderStatus(order)
                ));

        List<Order> filteredOrders = orders;

        if(status != null) {
            filteredOrders = orders.stream()
                    .filter(order -> orderStatusMap.get(order) == status)
                    .collect(Collectors.toList());
        }

        if (filteredOrders.isEmpty()) {
            return List.of();
        }

        List<String> orderIds = filteredOrders.stream()
                .map(order -> order.getId())
                .collect(Collectors.toList());



        // query count orderItem bang group by va id trong list da loc
        List<Payment> payments = paymentRepository.findActiveWithMethodByOrderIds(orderIds);

        Map<String, Payment> latestPaymentByOrderId =
                new HashMap<>();

        for (Payment payment : payments) {
            latestPaymentByOrderId.putIfAbsent(
                    payment.getOrder().getId(),
                    payment
            );
        }

        List<OrderItem> orderItems = orderItemRepository.findByIsDeletedFalseAndOrder_IdIn(orderIds);

        Map<String, Integer> orderItemsByOrderId =
                orderItems.stream()
                        .collect(
                                Collectors.groupingBy(
                                        orderItem -> orderItem.getOrder().getId(),
                                        Collectors.summingInt(orderItem->1)
                                )
                        );

        // tra ve response
        return filteredOrders.stream()
                .map(order -> myOrderMapper.toResponse(
                        order,
                        latestPaymentByOrderId.get(order.getId()),
                        orderStatusMap.get(order),
                        orderItemsByOrderId.getOrDefault(order.getId(), 0)
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderStatistics getOrderStatistics(String username){
        return orderRepository.getOrderStatistics(
                username,
                ShipmentStatus.SHIPPING,
                ShipmentStatus.DELIVERED
        );
    }

    @Transactional
    public List<ReorderItemResponse> reorder(String username, String orderId){
        Order order = orderRepository.getActiveOwnedDeliveredOrder(
                username,
                orderId,
                ShipmentStatus.DELIVERED
        ).orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        Cart cart = cartRepository.findActiveOwnedCartWithoutItems(username).
                orElseThrow(() ->  new BusinessException(ErrorCode.CART_NOT_FOUND));

        List<OrderItem> orderItems = orderItemRepository.findActiveByOrderId(orderId);


        // kiem tra lai so luong
        int activeOrderItemCount =
                orderItemRepository.countByIsDeletedFalseAndOrder_Id(orderId);
        if (activeOrderItemCount == 0
                || orderItems.size() != activeOrderItemCount) {
            throw new BusinessException(ErrorCode.PRODUCT_UNAVAILABLE);
        }

        List<String> productVariantIds = orderItems.stream()
                .map(x-> x.getProductVariant().getId())
                .distinct()
                .toList();


        List<CartItem> cartItems = cartItemRepository
                .findAllByCart_IdAndProductVariant_IdIn(cart.getId(), productVariantIds);


        Map<String, CartItem> cartItemMap = cartItems.stream()
                .collect(Collectors.toMap(
                        cartItem -> cartItem.getProductVariant().getId(),
                        cartItem -> cartItem));

        Map<String, Integer> finalQuantityMap = new HashMap<>();

        for(OrderItem orderItem : orderItems){
            ProductVariant productVariant = orderItem.getProductVariant();
            Inventory inventory = productVariant.getInventory();

            CartItem cartItem = cartItemMap.get(productVariant.getId());

            int finalQuantity;

            if (cartItem == null || cartItem.isDeleted()) {
                finalQuantity = orderItem.getQuantity();
            } else {
                finalQuantity =
                        cartItem.getQuantity() + orderItem.getQuantity();
            }

            if (finalQuantity > inventory.getQuantityInStock()) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
            }

            finalQuantityMap.put(productVariant.getId(), finalQuantity);
        }

        List<CartItem> saveCartItems = new ArrayList<>();

        for(OrderItem orderItem : orderItems){
            ProductVariant productVariant = orderItem.getProductVariant();

            CartItem cartItem = cartItemMap.get(productVariant.getId());

            if(cartItem == null) {
                CartItem savedCartItem = new CartItem();
                savedCartItem.setProductVariant(productVariant);
                savedCartItem.setQuantity(finalQuantityMap.get(productVariant.getId()));
                savedCartItem.setCart(cart);
                savedCartItem.setUnitPrice(productVariant.getUnitPrice());
                saveCartItems.add(savedCartItem);
            }
            else {
                cartItem.setDeleted(false);
                cartItem.setQuantity(finalQuantityMap.get(productVariant.getId()));
                cartItem.setUnitPrice(productVariant.getUnitPrice());
                saveCartItems.add(cartItem);
            }
        }

        return cartItemRepository.saveAll(saveCartItems)
                .stream()
                .map(cartItem -> new ReorderItemResponse(cartItem.getId(), cartItem.getQuantity()))
                .toList();
    }


    @Transactional
    public List<ReviewResponse> review(String username, String orderId, CreateReviewRequest request){
        Order order = orderRepository.getActiveOwnedDeliveredOrder(
                username,
                orderId,
                ShipmentStatus.DELIVERED
        ).orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        // kiem tra request khong rong và khong trung orderItemId
        Set<String> requestIds = request.getReviews()
                .stream()
                .map(ReviewRequest::getOrderItemId)
                .collect(Collectors.toSet());

        if(requestIds.size() == 0 || requestIds.size() != request.getReviews().size()){
            throw new BusinessException(ErrorCode.DUPLICATE_ORDER_ITEM);
        }

        // tim cac orderItem chua xoa trong order
        List<OrderItem> orderItems = orderItemRepository.findByIsDeletedFalseAndOrder_Id(orderId);

        // kiem tra các orderItemId trong request deu thuoc các item chua xoa trong order
        Map<String, OrderItem> orderItemMap = orderItems.stream()
                .collect(Collectors.toMap(
                        orderItem -> orderItem.getId(),
                        orderItem -> orderItem
                ));

        if(!orderItemMap.keySet().containsAll(requestIds)){
            throw new BusinessException(ErrorCode.INVALID_ORDER_ITEM_SELECTION);
        }

        //kiem tra cac orderItem da co review truoc chua(unique)
        List<Review> reviews = reviewRepository.findAllByOrderItem_IdIn(requestIds);

        if(reviews.size() != 0){
            throw new BusinessException(ErrorCode.ORDER_ITEM_ALREADY_REVIEWED);
        }

        //tao cac review
        List<Review> savedReviews = new ArrayList<>();
        for(ReviewRequest reviewRequest : request.getReviews()){
            Review review = new Review();
            review.setOrderItem(orderItemMap.get(reviewRequest.getOrderItemId()));
            review.setDescription(reviewRequest.getDescription());
            review.setRating(reviewRequest.getRating());
            savedReviews.add(review);
        }

        // save all va tra ve response
        return reviewRepository.saveAll(savedReviews).stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    private MyOrderStatus getOrderStatus(Order order){

        Shipment shipment = order.getShipment();

        if (shipment == null || shipment.isDeleted()) {
            return order.getPaymentStatus() == PaymentStatus.PAID
                    ? MyOrderStatus.PROCESSING
                    : MyOrderStatus.AWAITING_PAYMENT;
        }

        return switch (shipment.getStatus()) {
            case DELIVERED -> MyOrderStatus.COMPLETED;
            case FAILED -> MyOrderStatus.CANCELLED;
            case SHIPPING -> MyOrderStatus.SHIPPING;

            case PENDING,
                 CONFIRMED,
                 PICKING,
                 RETURNING,
                 REATTEMPT -> MyOrderStatus.PROCESSING;
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
