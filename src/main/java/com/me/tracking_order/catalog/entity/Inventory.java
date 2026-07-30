package com.me.tracking_order.catalog.entity;

import com.me.tracking_order.common.persistence.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "inventories", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_inventories_product_variant",
                columnNames = "product_variant_id"
        )
})
public class Inventory extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator
    @Column(name = "id", length = 36)
    private String id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "product_variant_id",
            nullable = false,
            unique = true,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_inventories_product_variant")
    )
    private ProductVariant productVariant;

    @Column(name = "quantity_in_stock", nullable = false)
    private int quantityInStock = 0;
}
