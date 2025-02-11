package com.twilight.twilight.handler;

import com.twilight.twilight.Model.ApiResponse;
import org.neo4j.driver.exceptions.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IOException.class)
    public ApiResponse<String> handleIOException(IOException e) {
        String message = "I/O操作异常";
        log.error(message);
        return new ApiResponse<>(500, message, e.toString());
    }

    @ExceptionHandler(DatabaseException.class)
    public ApiResponse<String> databaseIOException(DatabaseException e) {
        String message = "数据库操作异常";
        log.error(message);
        return new ApiResponse<>(500, message, e.toString());
    }

    @ExceptionHandler(ExcelUploadException.class)
    public ApiResponse<String> handleExcelException(ExcelUploadException e) {
        String message = "Excel上传时出现错误";
        log.error(message);
        return new ApiResponse<>(500, message, e.toString());
    }
}

