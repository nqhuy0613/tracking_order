package com.me.tracking_order.returns.specification;

import com.me.tracking_order.returns.entity.ReturnRequest;
import com.me.tracking_order.returns.enums.ReturnRequestStatus;
import org.springframework.data.jpa.domain.Specification;

public class ReturnRequestSpecification {

    public static Specification<ReturnRequest> hasStatus(ReturnRequestStatus status){
        return (root, query, cb) -> {
            if(status == null){
                return cb.conjunction();
            }

            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<ReturnRequest> notDeleted(){
        return (root, query, cb) -> cb.isFalse(root.get("isDeleted"));
    }
}
