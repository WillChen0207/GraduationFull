package com.twilight.twilight.Controller;

import com.twilight.twilight.Model.ApiResponse;
import com.twilight.twilight.handler.ExcelUploadException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/test")
public class ApiTestController {
    @PostMapping("/io")
    public ApiResponse<String> testIOException() throws IOException {
        throw new IOException("Simulated IOException test.");
    }

    @PostMapping("/excelupload")
    public ApiResponse<String> testExcelUploadException() throws ExcelUploadException {
        throw new ExcelUploadException("Simulated ExcelUploadException test.", null);
    }
}
