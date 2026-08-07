package com.me.tracking_order.review.repository;

import com.me.tracking_order.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, String> {
    @Query("""
        select count(r)
        from Review r
        join r.orderItem o
        join o.productVariant pv
        where r.isDeleted = false 
          and o.isDeleted = false 
          and pv.isDeleted = false 
          and pv.id = :id
""")
    int getReviewCountByProductVariantId(@Param("id") String id);

    @Query("""
        select r
        from Review r
        join r.orderItem o
        join o.productVariant pv
        join pv.product p
        where r.isDeleted = false 
          and o.isDeleted = false 
          and pv.isDeleted = false 
          and pv.id = :id
          and p.isDeleted = false
         order by r.createdAt desc 
""")
    List<Review> findAllByProductVariantId(@Param("id") String id);

    List<Review> findAllByOrderItem_IdIn(Collection<String> orderIds);
}
