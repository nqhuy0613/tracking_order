package com.me.tracking_order.returns.dto.admin.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReturnLogResponse {
    private String id;
    private String oldStatus;
    private String newStatus;
    private String title;
    private String description;
    private boolean isDeleted;
    private LocalDateTime createdAt;
}
