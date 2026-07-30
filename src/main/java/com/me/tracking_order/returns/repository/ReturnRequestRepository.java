package com.me.tracking_order.returns.repository;

import com.me.tracking_order.returns.entity.ReturnRequest;
import com.me.tracking_order.returns.enums.ReturnRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, String>, JpaSpecificationExecutor<ReturnRequest> {

    @Query("""
        select distinct rq
            from ReturnRequest rq
            join fetch rq.order o
            join fetch o.user u
            left join fetch rq.returnLogs rl
            where rq.isDeleted = false
              and rq.id = :id
         order by rl.createdAt desc
         
""")
    Optional<ReturnRequest> findActiveReturnById(
            @Param("id") String id
    );

    @Override
    @EntityGraph(attributePaths = {
            "order",
            "order.user"
    })
    Page<ReturnRequest> findAll(
            Specification<ReturnRequest> specification,
            Pageable pageable
    );

    Optional<ReturnRequest> findByIdAndIsDeletedFalseAndStatus(
            String id,
            ReturnRequestStatus status
    );

    long countByIsDeletedFalseAndStatus(ReturnRequestStatus status);

    long countByIsDeletedFalseAndStatusIn(Collection<ReturnRequestStatus> statuses);

    @Query("""
        select sum(o.totalAmount)
            from ReturnRequest rr
            join rr.order o
            where rr.isDeleted = false 
              and rr.status = :status
""")
    BigDecimal totalRefunds(
            @Param("status") ReturnRequestStatus status
    );
}
