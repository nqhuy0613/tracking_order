package com.me.tracking_order.catalog.service;

import com.me.tracking_order.catalog.dto.customer.response.CategoryResponse;
import com.me.tracking_order.catalog.entity.Category;
import com.me.tracking_order.catalog.mapper.CategoryMapper;
import com.me.tracking_order.catalog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {


    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findByIsDeletedFalse();

        return categories.stream()
                .map(categoryMapper::toResponse)
                .toList();
    }


}
