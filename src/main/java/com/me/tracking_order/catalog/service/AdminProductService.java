package com.me.tracking_order.catalog.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.me.tracking_order.catalog.dto.admin.request.AdminCreateProductRequest;
import com.me.tracking_order.catalog.dto.admin.request.AdminCreateProductVariantRequest;
import com.me.tracking_order.catalog.dto.admin.request.AdminUpdateProductVariantRequest;
import com.me.tracking_order.catalog.dto.admin.request.AdminVariantFilterRequest;
import com.me.tracking_order.catalog.dto.admin.response.*;
import com.me.tracking_order.catalog.entity.Category;
import com.me.tracking_order.catalog.entity.Inventory;
import com.me.tracking_order.catalog.entity.Product;
import com.me.tracking_order.catalog.entity.ProductVariant;
import com.me.tracking_order.catalog.mapper.*;
import com.me.tracking_order.catalog.repository.CategoryRepository;
import com.me.tracking_order.catalog.repository.InventoryRepository;
import com.me.tracking_order.catalog.repository.ProductRepository;
import com.me.tracking_order.catalog.repository.ProductVariantRepository;
import com.me.tracking_order.catalog.specification.ProductVariantSpecification;
import com.me.tracking_order.common.exception.BusinessException;
import com.me.tracking_order.common.exception.ErrorCode;
import com.me.tracking_order.common.response.PageResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Service
@RequiredArgsConstructor
public class AdminProductService {

    private static final int EXPORT_BATCH_SIZE = 500;
    private final int LOW_STOCK = 5;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;
    private final UpdateProductVariantMapper updateProductVariantMapper;
    private final AdminProductVariantMapper adminProductVariantMapper;
    private final ProductVariantCsvRowMapper productVariantCsvRowMapper;
    private final EntityManager entityManager;
    private final AdminCreatedProductVariantMapper adminCreatedProductVariantMapper;
    private final AdminCreateProductMapper adminCreateProductMapper;

    @Transactional(readOnly = true)
    public AdminProductSummaryResponse getAdminProductSummary() {

        return productVariantRepository.getAdminProductSummary(LOW_STOCK);
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

        String normalizedSku = normalizeSku(productVariant.getSku());

        if (productVariantRepository.existsBySkuAndIdNot(
                normalizedSku,
                id
        )) {
            throw new BusinessException(
                    ErrorCode.PRODUCT_VARIANT_SKU_ALREADY_EXISTS
            );
        }

        Category category = categoryRepository
                .findByIdAndIsDeletedFalse(
                        request.getCategoryId().trim()
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

        // li do chuyen inventory len sau validate, truoc update
        Inventory inventory = inventoryRepository.findByVariantIdForUpdate(id);

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

            inventoryRepository.save(inventory);
        }

        productVariant.setInventory(inventory);
        productVariantRepository.save(productVariant);

        return updateProductVariantMapper.toResponse(
                productVariant
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminGetAllVariantResponse>     getAllProductVariants(
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

    @Transactional(readOnly = true)
    public void exportProductVariants(OutputStream outputStream) {
        // 1. tao writer, tao sheet
        try (ExcelWriter excelWriter = EasyExcel
                // ghi vao response, cau truc dto
                .write(outputStream, ProductVariantCsvRow.class)
                // file csv
                .excelType(ExcelTypeEnum.CSV)
                .charset(StandardCharsets.UTF_8)
                .withBom(true)
                .autoCloseStream(false)
                .build()
        ) {

            WriteSheet writeSheet = EasyExcel
                    .writerSheet("Product variants")
                    .build();

            int pageNumber = 0;

            while(true) {
                // 2. tim batch hien tai
                Pageable pageable = PageRequest.of(
                        pageNumber,
                        EXPORT_BATCH_SIZE,
                        Sort.by(Sort.Direction.DESC, "createdAt","id"));

                List<ProductVariant> batch = productVariantRepository.findAll(
                        ProductVariantSpecification.notDeleted(),
                        pageable).getContent();
                // 3. chuyen sang dang du lieu ghi vao file

                List<ProductVariantCsvRow> rows = batch.stream()
                        .map(productVariantCsvRowMapper::toRow)
                        .toList();
                // 4. ghi batch vao stream
                excelWriter.write(rows, writeSheet);
                // 5. xoa cac entity trong persistence context hien tai
                entityManager.clear();
                // 6. kiem tra de quyet dinh co tiep tuc vong lap khong
                if(batch.size() < EXPORT_BATCH_SIZE) {
                    break;
                }
                pageNumber++;
            }
        }
    }

    @Transactional
    public AdminCreateProductResponse createProduct(AdminCreateProductRequest request) {
        // validate category, validate sku
        Category category = categoryRepository.findByIdAndIsDeletedFalse(request.getCategoryId().trim())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        List<String> skus = request.getVariants()
                .stream()
                .map(x -> x.getSku())
                .map(this::normalizeSku)
                .toList();
        HashSet<String> skusSet = new HashSet<>(skus);

        if(skus.size() != skusSet.size()) {
            throw new BusinessException(ErrorCode.PRODUCT_VARIANT_SKU_MUST_UNIQUE);
        }

        List<ProductVariant> validateSku = productVariantRepository.findAllBySkuIn(skus);
        if(validateSku.size() > 0) {
            throw new BusinessException(ErrorCode.PRODUCT_VARIANT_SKU_ALREADY_EXISTS);
        }
        // tao ban ghi product
        Product product = new Product();
        product.setName(request.getName());
        product.setCategory(category);
        product.setBrand(request.getBrand());
        product.setDescription(request.getDescription());

        Product savedProduct = productRepository.save(product);

        List<ProductVariant> savedProductVariants = new ArrayList<>();
        List<Inventory> savedInventory = new ArrayList<>();


        // duyet lan luot
        for(AdminCreateProductVariantRequest req : request.getVariants()) {
        // tao cac ban ghi product variant tuong ung
            ProductVariant productVariant = new ProductVariant();
            productVariant.setName(req.getName());
            productVariant.setSku(normalizeSku(req.getSku()));
            productVariant.setUnitPrice(req.getUnitPrice());
            productVariant.setWeight(req.getWeight());
            productVariant.setColor(req.getColor());
            productVariant.setSize(req.getSize());
            productVariant.setImage(req.getImage());
            productVariant.setProduct(savedProduct);
        // tao inventory tuong ung va gan vao product variant
            Inventory inventory = new Inventory();
            inventory.setQuantityInStock(req.getQuantityInStock());
            inventory.setProductVariant(productVariant);

            productVariant.setInventory(inventory);

            savedInventory.add(inventory);
            savedProductVariants.add(productVariant);


        }
        // gan list vao product va save all


        productVariantRepository.saveAll(savedProductVariants);
        inventoryRepository.saveAll(savedInventory);

        savedProduct.setProductVariants(savedProductVariants);

        List<AdminCreatedProductVariantResponse> productVariantResponses = savedProductVariants
                .stream()
                .map(adminCreatedProductVariantMapper::toResponse)
                .toList();

        // tao product response va tra ve
        return adminCreateProductMapper.toResponse(savedProduct, productVariantResponses);
    }

    private String normalizeSku(String sku) {
        return sku.trim().toUpperCase(Locale.ROOT);
    }
}
