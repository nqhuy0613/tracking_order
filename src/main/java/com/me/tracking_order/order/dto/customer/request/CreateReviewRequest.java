package com.me.tracking_order.order.dto.customer.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateReviewRequest {
    @Valid
    @NotEmpty
    private List<ReviewRequest> reviews;
}
