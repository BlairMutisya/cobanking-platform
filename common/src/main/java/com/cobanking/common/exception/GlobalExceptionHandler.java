package com.cobanking.common.exception;

import com.cobanking.common.api.BaseApiResponse;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseApiResponse<List<String>>> handleValidation(MethodArgumentNotValidException exception) {
        List<String> details = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest()
                .body(BaseApiResponse.failure("Request validation failed", details));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleNotFound(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseApiResponse.failure(exception.getMessage(), null));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleBusiness(BusinessException exception) {
        return ResponseEntity.badRequest()
                .body(BaseApiResponse.failure(exception.getMessage(), null));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleDataIntegrity() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(BaseApiResponse.failure("The request conflicts with existing data", null));
    }
}
