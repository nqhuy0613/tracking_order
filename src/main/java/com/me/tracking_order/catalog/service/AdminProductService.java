package com.me.tracking_order.catalog.service;

import com.me.tracking_order.catalog.dto.admin.request.AdminUpdateProductVariantRequest;
import com.me.tracking_order.catalog.dto.admin.request.AdminVariantFilterRequest;
import com.me.tracking_order.catalog.dto.admin.response.AdminGetAllVariantResponse;
import com.me.tracking_order.catalog.dto.admin.response.AdminProductSummaryResponse;
import com.me.tracking_order.catalog.dto.admin.response.AdminUpdateProductVariantResponse;
import com.me.tracking_order.catalog.entity.Category;
import com.me.tracking_order.catalog.entity.Inventory;
import com.me.tracking_order.catalog.entity.Product;
import com.me.tracking_order.catalog.entity.ProductVariant;
import com.me.tracking_order.catalog.mapper.AdminProductVariantMapper;
import com.me.tracking_order.catalog.mapper.UpdateProductVariantMapper;
import com.me.tracking_order.catalog.repository.CategoryRepository;
import com.me.tracking_order.catalog.repository.InventoryRepository;
import com.me.tracking_order.catalog.repository.ProductRepository;
import com.me.tracking_order.catalog.repository.ProductVariantRepository;
import com.me.tracking_order.catalog.specification.ProductVariantSpecification;
import com.me.tracking_order.common.exception.BusinessException;
import com.me.tracking_order.common.exception.ErrorCode;
import com.me.tracking_order.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;
    private final UpdateProductVariantMapper updateProductVariantMapper;
    private final AdminProductVariantMapper adminProductVariantMapper;

    public AdminProductSummaryResponse getAdminProductSummary() {
        BigDecimal totalPrice = productVariantRepository.getTotalPrice();

        long productVariantCount = productRepository.countByIsDeletedFalse();

        long lowStockVariantCount = productVariantRepository.getLowStockVariantcCount(5);

        return AdminProductSummaryResponse.builder()
                .totalPrice(totalPrice)
                .productVariantCount(productVariantCount)
                .lowStockVariantCount(lowStockVariantCount)
                .build();
    }

    @Transactional
    public AdminUpdateProductVariantResponse updateProductVariant(
            AdminUpdateProductVariantRequest request,
            String id
    ) {
        ProductVariant productVariant = productVariantRepository
                .findActiveById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.PRODUCT_VARIANT_NOT_FOUND
                        )
                );

        String normalizedSku = request.getSku().trim();

        if (productVariantRepository.existsBySkuAndIdNot(
                normalizedSku,
                id
        )) {
            throw new BusinessException(
                    ErrorCode.PRODUCT_VARIANT_SKU_ALREADY_EXISTS
            );
        }

        Category category = categoryRepository
                .findByNameAndIsDeletedFalse(
                        request.getCategoryName().trim()
                )
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.CATEGORY_NOT_FOUND
                        )
                );

        productVariant.setSku(normalizedSku);
        productVariant.setUnitPrice(request.getUnitPrice());
        productVariant.setImage(request.getImage());
        productVariant.setName(request.getName().trim());

        Product product = productVariant.getProduct();
        product.setCategory(category);
        product.setDescription(request.getDescription());

        Inventory inventory = productVariant.getInventory();

        if (inventory == null) {
            inventory = new Inventory();
            inventory.setProductVariant(productVariant);
            inventory.setQuantityInStock(request.getQuantityInStock());

            productVariant.setInventory(inventory);

            inventoryRepository.save(inventory);
        } else {
            inventory.setDeleted(false);
            inventory.setQuantityInStock(
                    request.getQuantityInStock()
            );
        }

        return updateProductVariantMapper.toResponse(
                productVariant
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminGetAllVariantResponse> getAllProductVariants(
            AdminVariantFilterRequest request,
            Integer pageNumber,
            Integer pageSize
    ) {
        Specification<ProductVariant> specification =
                Specification.where(
                        ProductVariantSpecification.notDeleted()
                );

        if (request != null) {
            if (request.getMinPrice() != null
                    && request.getMaxPrice() != null
                    && request.getMinPrice()
                    .compareTo(request.getMaxPrice()) > 0) {
                throw new BusinessException(
                        ErrorCode.INVALID_PRICE_RANGE
                );
            }

            if (request.getName() != null
                    && !request.getName().isBlank()) {
                specification = specification.and(
                        ProductVariantSpecification.nameContains(
                                request.getName()
                        )
                );
            }

            if (request.getSku() != null
                    && !request.getSku().isBlank()) {
                specification = specification.and(
                        ProductVariantSpecification.skuContains(
                                request.getSku()
                        )
                );
            }

            if (request.getStockStatus() != null) {
                specification = specification.and(
                        ProductVariantSpecification.hasStockStatus(
                                request.getStockStatus()
                        )
                );
            }

            if (request.getMinPrice() != null) {
                specification = specification.and(
                        ProductVariantSpecification.minPrice(
                                request.getMinPrice()
                        )
                );
            }

            if (request.getMaxPrice() != null) {
                specification = specification.and(
                        ProductVariantSpecification.maxPrice(
                                request.getMaxPrice()
                        )
                );
            }
        }

        Pageable pageable = PageRequest.of(
                pageNumber - 1,
                pageSize,
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"
                )
        );

        Page<ProductVariant> page =
                productVariantRepository.findAll(
                        specification,
                        pageable
                );

        return PageResponse.from(
                page,
                adminProductVariantMapper::toResponse
        );
    }
}
