package com.me.tracking_order.returns.entity;

import com.me.tracking_order.common.persistence.BaseEntity;
import com.me.tracking_order.order.entity.Order;

import com.me.tracking_order.returns.enums.ReturnRequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "return_requests")
public class ReturnRequest extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "origin_type", nullable = false, length = 100)
    private String originType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReturnRequestStatus status = ReturnRequestStatus.PENDING;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "order_id",
            nullable = false,
            unique = true,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_return_requests_order")
    )
    private Order order;

    @OneToMany(mappedBy = "returnRequest", fetch = FetchType.LAZY)
    private List<ReturnItem> returnItems = new ArrayList<>();

    @OneToMany(mappedBy = "returnRequest", fetch = FetchType.LAZY)
    private List<ReturnLog> returnLogs = new ArrayList<>();
}
