package com.me.tracking_order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "product_variants", uniqueConstraints = {
        @UniqueConstraint(name = "uk_product_variants_sku", columnNames = "sku")
})
public class ProductVariant extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "sku", nullable = false, length = 100)
    private String sku;

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "weight", precision = 10, scale = 2)
    private BigDecimal weight;

    @Column(name = "color", length = 100)
    private String color;

    @Column(name = "image", length = 500)
    private String image;

    @Column(name = "size", length = 100)
    private String size;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_product_variants_product")
    )
    private Product product;

    @OneToOne(mappedBy = "productVariant", fetch = FetchType.LAZY)
    private Inventory inventory;

    @OneToMany(mappedBy = "productVariant", fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "productVariant", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();
}
