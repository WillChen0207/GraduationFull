package com.twilight.twilight.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.twilight.twilight.Model.*;
import com.twilight.twilight.Service.ResourceService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/resource")
public class ResourceController {

    private final ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/upload")
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 使用相对路径建立文件存储目录，相对于JAR的运行目录
            Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(path);  // 确保目录存在

            Path filePath = path.resolve(file.getOriginalFilename());
            file.transferTo(filePath);

            return new ApiResponse<>(200, "File uploaded successfully.", filePath.toString());
        } catch (Exception e) {
            return new ApiResponse<>(400, "Failed to upload file.", e.getMessage());
        }
    }

    @GetMapping("/getResourceById")
    public ApiResponse<Resource> getResourceById(@RequestParam Long id) {
        Resource resource = resourceService.getResourceById(id).get();
        return new ApiResponse<>(200, "Resource found.", resource);
    }

    @PostMapping("/addResource")
    public ApiResponse<String> addResource(@RequestBody ResourceDTO res) throws NoSuchAlgorithmException, ParseException {
        Resource newResource = new Resource();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        newResource.setResourceName(res.getResourceName());
        newResource.setResourceType(res.getResourceType());
        newResource.setProvider(res.getProvider());
        newResource.setPostTime(res.getPostTime());
        newResource.setContent(res.getContent());
        resourceService.addResource(newResource);
        Long resourceId = resourceService.getId(newResource.getId());
        resourceService.provide(res.getProvider(), resourceId);
        return new ApiResponse<>(200, "Resource added successfully.", null);
    }

    @PostMapping("/update")
    public ApiResponse<Resource> update(@RequestParam Long id,
                                        @RequestParam String resourceName,
                                        @RequestParam Integer resourceType,
                                        @RequestParam Long provider,
                                        @RequestParam String postTime,
                                        @RequestParam String content) throws ParseException {
        Resource newResource = resourceService.getResourceById(id).get();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        newResource.setResourceName(resourceName);
        newResource.setResourceType(resourceType);
        newResource.setProvider(provider);
        newResource.setPostTime(sdf.parse(postTime));
        newResource.setContent(content);
        resourceService.addResource(newResource);
        Long resourceId = resourceService.getId(newResource.getId());
        resourceService.clear(resourceId);
        resourceService.provide(provider, resourceId);
        Optional<Resource> resourceOptional = resourceService.getResourceById(id);
        return resourceOptional.map(resource -> new ApiResponse<>(200, "Resource info updated successfully.", resource)).orElseGet(() -> new ApiResponse<>(500, "Resource info update unsuccessful.", null));
    }

    @DeleteMapping("/deleteResource")
    public ApiResponse<String> deleteResource(@RequestParam Long id) {
        resourceService.deleteResource(id);
        return new ApiResponse<>(200, "Resource deleted successfully.", null);
    }

    @DeleteMapping("/deleteByUUID")
    public ApiResponse<String> deleteByUUID(@RequestParam UUID uuid) {
        resourceService.deleteByUUID(uuid);
        return new ApiResponse<>(200, "Resource deleted successfully.", null);
    }

    @GetMapping("/findAll")
    public ApiResponse<List<Resource>> findAll() {
        List<Resource> resourceList = resourceService.findAll();
        return new ApiResponse<>(200, "Resource list fetched successfully.", resourceList);
    }

    @GetMapping("/download")
    public ApiResponse<String> download(@RequestParam("userId") Long userId,
                                        @RequestParam("resourceId") Long resourceId) {
        String resourceUrl = resourceService.download(userId, resourceId);
        return new ApiResponse<>(200, "Downloaded.", resourceUrl);
    }

    @PostMapping("/collect")
    public ApiResponse<String> collect(@RequestParam("userId") Long userId,
                                       @RequestParam("resourceId")Long resourceId,
                                       @RequestParam("flag") Integer flag) {
        resourceService.collect(userId, resourceId, flag);
        return new ApiResponse<>(200, "Collected/DisCollected.", null);
    }

    @PostMapping("/view")
    public ApiResponse<String> view(@RequestParam("userId") Long userId,
                                    @RequestParam("resourceId") Long resourceId) {
        resourceService.view(userId, resourceId);
        return new ApiResponse<>(200, "Viewed.", null);
    }

    @PostMapping("/like")
    public ApiResponse<String> like(@RequestParam("userId") Long userId,
                                    @RequestParam("resourceId")Long resourceId,
                                    @RequestParam("flag") Integer flag) {
        resourceService.like(userId, resourceId, flag);
        return new ApiResponse<>(200, "Liked/Disliked.", null);
    }

    @GetMapping("/getRecommend")
    public ApiResponse<List<RecommendationDTO>> getRecommend(@RequestBody JsonNode requestBody) {
        JsonNode similarUserIdsNode = requestBody.get("similarUserIds");
        List<Long> similarUserIds = new ArrayList<>();
        if (similarUserIdsNode.isArray()) {
            for (JsonNode idNode : similarUserIdsNode) {
                similarUserIds.add(idNode.asLong());
            }
        }
        Long userId = requestBody.get("userId").asLong();
        Integer recNum = requestBody.get("recNum").asInt();
        String keyword = String.valueOf(requestBody.get("recNum"));
        List<RecommendationDTO> recommendList = resourceService.getRecommend(similarUserIds, userId, recNum, keyword);
        return new ApiResponse<>(200, "Recommend list is as follows.", recommendList);
    }

    @GetMapping("/getInteractionStates")
    public ApiResponse<InteractionDTO> getInteractionStates(@RequestParam Long userId,
                                                            @RequestParam Long resourceId) {
        return new ApiResponse<>(200, "Got Interaction States.", resourceService.getInteractionStates(userId,resourceId));
    }

//    @PostMapping("/calcPreference")
//    public ApiResponse<Double> calcPreference(@RequestParam Long userId) {
//        Double preference = resourceService.calcPreference(userId);
//        return new ApiResponse<>(200, "Preference calculated.",preference);
//    }
}
