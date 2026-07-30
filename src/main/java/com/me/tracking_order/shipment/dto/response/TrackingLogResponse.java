package com.me.tracking_order.shipment.dto.response;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class TrackingLogResponse {

    private String id;

    private String oldStatus;

    private String newStatus;

    private String title;

    private String description;

    private String location;

    private LocalDateTime createdAt;

}
