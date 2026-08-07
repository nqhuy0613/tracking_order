package com.me.tracking_order.returns.repository;

import com.me.tracking_order.returns.dto.admin.response.AdminReturnSummaryResponse;
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
import java.util.List;
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


    @Query("""
    select new com.me.tracking_order.returns.dto.admin.response.AdminReturnSummaryResponse(
        count(case
            when rr.status in :activeStatuses then 1
        end),
        count(case
            when rr.status = :receivedStatus then 1
        end),
        sum(case
            when rr.status = :refundedStatus then o.totalAmount
            else null
        end)
    )
    from ReturnRequest rr
    join rr.order o
    where rr.isDeleted = false
""")
    AdminReturnSummaryResponse getReturnRequestSummary(
            @Param("activeStatuses")
            Collection<ReturnRequestStatus> activeStatuses,

            @Param("receivedStatus")
            ReturnRequestStatus receivedStatus,

            @Param("refundedStatus")
            ReturnRequestStatus refundedStatus
    );

    @EntityGraph(attributePaths = {
            "order",
            "order.user"
    })
    @Query("""
        select rr
        from ReturnRequest rr
        where rr.isDeleted = false 
          and (:status is null or :status = rr.status)
""")
    List<ReturnRequest> findExportBatch(
            @Param("status") ReturnRequestStatus status,
            Pageable pageable
    );
}
