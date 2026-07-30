package com.me.tracking_order.shipment.entity;

import com.me.tracking_order.common.persistence.BaseEntity;
import com.me.tracking_order.order.entity.Order;

import com.me.tracking_order.shipment.enums.ShipmentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "shipments", uniqueConstraints = {
        @UniqueConstraint(name = "uk_shipments_tracking_number", columnNames = "tracking_number"),
        @UniqueConstraint(name = "uk_shipments_order", columnNames = "order_id")
})
public class Shipment extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator
    @Column(name = "id", length = 36)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShipmentStatus status = ShipmentStatus.PENDING;

    @Column(name = "tracking_number", nullable = false, length = 100)
    private String trackingNumber;

    @Column(name = "estimated_delivery")
    private LocalDateTime estimatedDelivery;

    @Column(name = "receiver_name", nullable = false, length = 255)
    private String receiverName;

    @Column(name = "receiver_phone", nullable = false, length = 20)
    private String receiverPhone;

    @Column(name = "shipping_province", nullable = false, length = 255)
    private String shippingProvince;

    @Column(name = "shipping_commune", nullable = false, length = 255)
    private String shippingCommune;

    @Column(name = "shipping_detail_address", nullable = false, length = 500)
    private String shippingDetailAddress;

    @Column(name = "current_location", length = 255)
    private String currentLocation;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "order_id",
            nullable = false,
            unique = true,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_shipments_order")
    )
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "carrier_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_shipments_carrier")
    )
    private Carrier carrier;

    @OneToMany(mappedBy = "shipment", fetch = FetchType.LAZY)
    private List<TrackingLog> trackingLogs = new ArrayList<>();
}
