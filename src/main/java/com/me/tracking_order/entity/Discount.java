package com.me.tracking_order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "discounts", uniqueConstraints = {
        @UniqueConstraint(name = "uk_discounts_code", columnNames = "code")
})
public class Discount extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "discount_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(name = "max_discount_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal maxDiscountAmount = BigDecimal.ZERO;

    @Column(name = "min_order_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal minOrderAmount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "discount", fetch = FetchType.LAZY)
    private List<UserDiscount> userDiscounts = new ArrayList<>();
}
