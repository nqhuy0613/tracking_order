package com.me.tracking_order.catalog.repository;

import com.me.tracking_order.catalog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findByNameAndIsDeletedFalse(String name);

    List<Category> findByIsDeletedFalse();
}

