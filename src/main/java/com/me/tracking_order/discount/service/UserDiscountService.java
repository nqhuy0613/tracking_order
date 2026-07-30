package com.me.tracking_order.discount.service;

import com.me.tracking_order.discount.dto.response.UserDiscountResponse;
import com.me.tracking_order.discount.mapper.UserDiscountMapper;
import com.me.tracking_order.discount.repository.UserDiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDiscountService {


    private final UserDiscountRepository userDiscountRepository;
    private final UserDiscountMapper userDiscountMapper;

    @Transactional(readOnly = true)
    public List<UserDiscountResponse> getCurrentUserDiscounts(
            String username
    ) {
        return userDiscountRepository
                .findActiveByUsername(username, LocalDateTime.now())
                .stream()
                .map(userDiscountMapper::toResponse)
                .toList();
    }
}
