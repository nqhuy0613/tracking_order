package com.me.tracking_order.returns.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.me.tracking_order.common.exception.BusinessException;
import com.me.tracking_order.common.exception.ErrorCode;
import com.me.tracking_order.common.response.PageResponse;
import com.me.tracking_order.returns.dto.admin.response.AdminDetailsReturnResponse;
import com.me.tracking_order.returns.dto.admin.response.AdminReturnSummaryResponse;
import com.me.tracking_order.returns.dto.admin.response.ReturnRequestCsvRow;
import com.me.tracking_order.returns.dto.customer.response.ReturnRequestResponse;
import com.me.tracking_order.returns.entity.ReturnRequest;
import com.me.tracking_order.returns.enums.ReturnRequestStatus;
import com.me.tracking_order.returns.mapper.AdminDetailReturnMapper;
import com.me.tracking_order.returns.mapper.ReturnRequestCsvRowMapper;
import com.me.tracking_order.returns.mapper.ReturnRequestMapper;
import com.me.tracking_order.returns.repository.ReturnRequestRepository;
import com.me.tracking_order.returns.specification.ReturnRequestSpecification;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AdminReturnRequestService {

    private static final int EXPORT_BATCH_SIZE = 500;
    private final ReturnRequestRepository returnRequestRepository;
    private final AdminDetailReturnMapper adminDetailReturnMapper;
    private final ReturnRequestMapper returnRequestMapper;
    private final EntityManager entityManager;
    private final ReturnRequestCsvRowMapper  returnRequestCsvRowMapper;


    @Transactional(readOnly = true)
    public AdminDetailsReturnResponse getDetailsReturnById(String id){
        ReturnRequest returnRequest = returnRequestRepository.findActiveReturnById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RETURN_REQUEST_NOT_FOUND));

        return adminDetailReturnMapper.toResponse(returnRequest);
    }

    @Transactional(readOnly = true)
    public PageResponse<ReturnRequestResponse> getAllReturnRequests(ReturnRequestStatus status, int pageNumber, int pageSize){
        Specification<ReturnRequest> specification = Specification.where(ReturnRequestSpecification.notDeleted());

        specification = specification.and(ReturnRequestSpecification.hasStatus(status));

        Pageable pageable = PageRequest.of(pageNumber-1, pageSize,  Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<ReturnRequest> returnRequestPage =
                returnRequestRepository.findAll(specification, pageable);

        return PageResponse.from(
                returnRequestPage,
                returnRequestMapper::toResponse
        );
    }

    @Transactional
    public ReturnRequestResponse markReturnRequestReceived(String id){
        ReturnRequest returnRequest = returnRequestRepository.findByIdAndIsDeletedFalseAndStatus(id, ReturnRequestStatus.IN_TRANSIT)
                .orElseThrow(() -> new BusinessException(ErrorCode.RETURN_REQUEST_NOT_VALID));

        returnRequest.setStatus(ReturnRequestStatus.RECEIVED);
        return returnRequestMapper.toResponse(returnRequest);
    }

    @Transactional(readOnly = true)
    public AdminReturnSummaryResponse getReturnRequestSummary(){

        // todo: gom thanh 1 cau sql
        return returnRequestRepository.getReturnRequestSummary(
                List.of(
                        ReturnRequestStatus.PENDING,
                        ReturnRequestStatus.IN_TRANSIT,
                        ReturnRequestStatus.RECEIVED
                ),
                ReturnRequestStatus.PENDING,
                ReturnRequestStatus.REFUNDED
        );
    }

    @Transactional(readOnly = true)
    public void exportReturnRequest(ReturnRequestStatus status, OutputStream outputStream){

        // luong service:
        // 1. tao excelWriter
        // 2. tao pageable
        // 3. lay batch tu repository
        // 4. chuyen entity tu batch -> dto
        // 5. ghi batch vao output stream
        // 6. clear trong persistence context
        // 7. batch<batchSize -> end, else pageNumber++ lap tu b2-> b7

        // tao try cath de excelWriter luon duoc dong dung cach
        try (ExcelWriter excelWriter = EasyExcel
                // ghi vao http response, moi dong co cau truc nhu dto
                .write(outputStream, ReturnRequestCsvRow.class)
                // file csv
                .excelType(ExcelTypeEnum.CSV)
                .charset(StandardCharsets.UTF_8)
                // giup nhan dien TV chinh xac hon
                .withBom(true)
                .autoCloseStream(false)
                .build()) {


            WriteSheet writeSheet = EasyExcel
                    .writerSheet("Returns")
                    .build();

            int pageNumber = 0;

            while(true){
                Pageable pageable = PageRequest.of(
                        pageNumber,
                        EXPORT_BATCH_SIZE,
                        Sort.by(Sort.Direction.DESC, "createdAt", "id"));

                List<ReturnRequest> batch = returnRequestRepository.findExportBatch(status, pageable);

                List<ReturnRequestCsvRow> rows = batch.stream()
                        .map(returnRequestCsvRowMapper::toCsvRow)
                        .toList();

                excelWriter.write(rows, writeSheet);

                entityManager.clear();

                if(batch.size() <  EXPORT_BATCH_SIZE){
                    break;
                }
                pageNumber++;
            }
            }

        }
}
