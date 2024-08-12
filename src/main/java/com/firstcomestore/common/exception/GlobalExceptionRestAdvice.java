package com.firstcomestore.common.exception;

import com.firstcomestore.common.dto.ResponseDTO;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionRestAdvice {

    @ExceptionHandler
    public ResponseEntity<ResponseDTO<Void>> applicationException(ApplicationException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
            .status(e.getErrorCode().getHttpStatus())
            .body(ResponseDTO.error(e.getErrorCode()));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseDTO<Void>> httpMessageNotReadableException(
        HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ResponseDTO.errorWithMessage(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler
    public ResponseDTO<Void> bindException(BindException e) {
        log.warn("[bindException] Message = {}",
            NestedExceptionUtils.getMostSpecificCause(e).getMessage());

        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = getSortedFieldErrors(bindingResult);

        String response = fieldErrors.stream()
            .map(fieldError -> String.format("%s (%s=%s)",
                    fieldError.getDefaultMessage(),
                    fieldError.getField(),
                    fieldError.getRejectedValue()
                )
            ).collect(Collectors.joining(", "));
        return ResponseDTO.errorWithMessage(HttpStatus.BAD_REQUEST, response);
    }

    private List<FieldError> getSortedFieldErrors(BindingResult bindingResult) {
        List<String> declaredFields = Arrays.stream(
                Objects.requireNonNull(bindingResult.getTarget()).getClass().getDeclaredFields())
            .map(Field::getName)
            .toList();

        return bindingResult.getFieldErrors().stream()
            .filter(fieldError -> declaredFields.contains(fieldError.getField()))
            .sorted(Comparator.comparingInt(fe -> declaredFields.indexOf(fe.getField())))
            .collect(Collectors.toList());
    }

    @ExceptionHandler
    public ResponseEntity<ResponseDTO<Void>> dbException(DataAccessException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ResponseDTO.errorWithMessage(HttpStatus.INTERNAL_SERVER_ERROR, "디비 에러!"));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseDTO<Void>> serverException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ResponseDTO.errorWithMessage(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러!"));
    }
}
