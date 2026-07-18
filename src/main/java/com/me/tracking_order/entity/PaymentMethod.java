package com.me.tracking_order.entity;

import com.me.tracking_order.enums.PaymentMethodStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "payment_methods", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_payment_methods_type_provider",
                columnNames = {"type", "payment_provider"}
        )
})
public class PaymentMethod extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator
    @Column(name = "id", length = 36)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PaymentMethodStatus type;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "payment_provider", nullable = false, length = 100)
    private String paymentProvider;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "fee_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal feeAmount = BigDecimal.ZERO;

    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled = true;

    @OneToMany(mappedBy = "paymentMethod", fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();
}
