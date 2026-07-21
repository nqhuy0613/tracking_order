package com.me.tracking_order.repository;

import com.me.tracking_order.entity.UserDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserDiscountRepository
        extends JpaRepository<UserDiscount, String> {

    @Query("""
            select ud
            from UserDiscount ud
            join fetch ud.discount d
            join ud.user u
            where u.username = :username
              and u.isDeleted = false
              and ud.isDeleted = false
              and d.isDeleted = false
              and ud.startAt <= :currentTime
              and ud.expiredAt >= :currentTime
            order by ud.expiredAt asc
            """)
    List<UserDiscount> findActiveByUsername(
            @Param("username") String username,
            @Param("currentTime") LocalDateTime currentTime
    );
}
