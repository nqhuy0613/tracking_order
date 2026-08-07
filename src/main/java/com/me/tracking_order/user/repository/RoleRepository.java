package com.me.tracking_order.user.repository;

import com.me.tracking_order.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findByNameAndIsDeletedFalse(String name);
}
