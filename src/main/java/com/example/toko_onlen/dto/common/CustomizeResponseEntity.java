package com.example.toko_onlen.dto.common;

import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CustomizeResponseEntity {

    public static <T> ResponseEntity<CommonResponse<T>> buildResponse(HttpStatus status, String message, T data) {

        CommonResponse<T> commonResponse = CommonResponse.<T>builder()
                .status(status.value())
                .message(message)
                .data(data)
                .timestamp(new Date())
                .build();

        return ResponseEntity.status(status).body(commonResponse);
    }
    // Method buildResponsePageable dengan T sebagai List konten
    public static <T> ResponseEntity<CommonResponsePageable<T>> buildResponsePageable(
            HttpStatus status,
            String message,
            List<T> data,
            Integer page,
            Integer size,
            Long totalElements,
            Integer totalPages) {

        CommonResponsePageable<T> response = CommonResponsePageable.<T>builder()
                .status(status.value())
                .message(message)
                .content(data)
                .timestamp(new Date())
                .page(page + 1)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .isLast(page.equals(totalPages))
                .build();

        return ResponseEntity.status(status).body(response);
    }

}