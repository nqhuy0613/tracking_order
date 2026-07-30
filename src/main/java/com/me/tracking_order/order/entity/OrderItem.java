package com.me.tracking_order.order.entity;

import com.me.tracking_order.catalog.entity.ProductVariant;
import com.me.tracking_order.common.persistence.BaseEntity;
import com.me.tracking_order.returns.entity.ReturnItem;
import com.me.tracking_order.review.entity.Review;

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
@Table(name = "order_items", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_order_items_order_variant",
                columnNames = {"order_id", "product_variant_id"}
        )
})
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "order_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_order_items_order")
    )
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "product_variant_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_order_items_product_variant")
    )
    private ProductVariant productVariant;

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @OneToMany(mappedBy = "orderItem", fetch = FetchType.LAZY)
    private List<ReturnItem> returnItems = new ArrayList<>();

    @OneToOne(mappedBy = "orderItem", fetch = FetchType.LAZY)
    private Review review;
}
