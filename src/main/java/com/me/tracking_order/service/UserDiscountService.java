package com.me.tracking_order.service;

import com.me.tracking_order.dto.response.UserDiscountResponse;
import com.me.tracking_order.mapper.UserDiscountMapper;
import com.me.tracking_order.repository.UserDiscountRepository;
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
