package com.twilight.twilight.Controller;

import com.twilight.twilight.Model.ApiResponse;
import com.twilight.twilight.Model.Resource;
import com.twilight.twilight.Model.User;
import com.twilight.twilight.Service.ExcelService;
import com.twilight.twilight.Service.ResourceService;
import com.twilight.twilight.Service.UserService;
import com.twilight.twilight.Service.UtilService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/excel")
public class ExcelController {
    private final ExcelService excelService;
    private final UserService userService;
    private final UtilService utilService;
    private final ResourceService resourceService;

    public ExcelController(ExcelService excelService, UserService userService, UtilService utilService, ResourceService resourceService) {
        this.excelService = excelService;
        this.userService = userService;
        this.utilService = utilService;
        this.resourceService = resourceService;
    }

    @PostMapping("/upload")
    public ApiResponse<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();

            Workbook workbook = new XSSFWorkbook(inputStream);

            String fileName = file.getOriginalFilename();

            String dataType;
            if (fileName != null && fileName.toLowerCase().contains("user")) {
                dataType = "User";
            } else if (fileName != null && fileName.toLowerCase().contains("resource")) {
                dataType = "Resource";
            } else if (fileName != null && fileName.toLowerCase().contains("download")) {
                dataType = "download";
            } else {
                return new ApiResponse<>(500, "Unsupported file type", null);
            }

            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> iterator = sheet.iterator();

            if (iterator.hasNext()) {
                iterator.next();
            }

            while (iterator.hasNext()) {
                Row currentRow = iterator.next();

                if ("User".equals(dataType)) {
                    String userName = currentRow.getCell(0).getStringCellValue();
                    String email = currentRow.getCell(1).getStringCellValue();
                    int userType = (int) currentRow.getCell(2).getNumericCellValue();
                    String password = currentRow.getCell(3).getStringCellValue();
                    String encryptedPassword = utilService.hashEncrypt(password);
                    User user = new User();
                    user.setUserName(userName);
                    user.setEmail(email);
                    user.setUserType(userType);
                    user.setPassword(encryptedPassword);
                    userService.addUser(user);
                } else if ("Resource".equals(dataType)) {
                    String resourceName = currentRow.getCell(0).getStringCellValue();
                    Integer resourceType = (int) currentRow.getCell(1).getNumericCellValue();
                    Long providerId = (long) currentRow.getCell(2).getNumericCellValue();
                    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                    String dateStr = currentRow.getCell(3).getStringCellValue();
                    Date postTime = fmt.parse(dateStr);
                    String content = currentRow.getCell(4).getStringCellValue();
                    Resource resource = new Resource();
                    resource.setResourceName(resourceName);
                    resource.setResourceType(resourceType);
                    resource.setProvider(providerId);
                    resource.setPostTime(postTime);
                    resource.setContent(content);
                    resourceService.addResource(resource);
                    Long resourceId = resourceService.getId(resource.getId());
                    resourceService.provide(providerId,resourceId);
                } else {
                    Long userId = (long) currentRow.getCell(0).getNumericCellValue();
                    Long resourceId = (long) currentRow.getCell(1).getNumericCellValue();
                    resourceService.download(userId, resourceId);
                }
            }

            workbook.close();
            inputStream.close();
        } catch (IOException | NoSuchAlgorithmException | ParseException e) {
            return new ApiResponse<>(500, "Upload unsuccessfully.", e.toString());
        }
        return new ApiResponse<>(200, "Excel file uploaded successfully.", null);
    }

}
