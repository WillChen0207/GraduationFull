package com.twilight.twilight.Controller;

import com.twilight.twilight.Model.*;
import com.twilight.twilight.Service.ResourceService;
import com.twilight.twilight.Service.UserService;
import com.twilight.twilight.Service.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")

public class UserController {
    private final UserService userService;
    private final UtilService utilService;
    private final ResourceService resourceService;

    @Autowired
    public UserController(UserService userService, UtilService utilService, ResourceService resourceService) {
        this.userService = userService;
        this.utilService = utilService;
        this.resourceService = resourceService;
    }

    @GetMapping("/getUserById")
    public ApiResponse<User> getUserById(@RequestParam Long id) {
        User user = userService.getUserById(id).get();
        return new ApiResponse<>(200, "User found.", user);
    }

    @GetMapping("/getInfoById")
    public ApiResponse<InfoDTO> getUserNameById(@RequestParam Long id) {
        InfoDTO userInfo = userService.getInfoById(id);
        return new ApiResponse<>(200, "UserName found.", userInfo);
    }

    @PostMapping("/addUser")
    public ApiResponse<String> addUser(@RequestParam String userName,
                                       @RequestParam String email,
                                       @RequestParam Integer userType,
                                       @RequestParam String password) throws NoSuchAlgorithmException {
        User newUser = new User();
        newUser.setUserName(userName);
        newUser.setEmail(email);
        newUser.setUserType(userType);
        newUser.setPassword(utilService.hashEncrypt(password));
        userService.addUser(newUser);
        return new ApiResponse<>(200, "User added successfully.", email);
    }

    @PostMapping("/update")
    public ApiResponse<User> update(@RequestBody UserUpdateDTO userUpdateDto) {
        if (userUpdateDto.getUserName().isEmpty() && userUpdateDto.getEmail().isEmpty() && userUpdateDto.getUserType().equals(0)) {
            return new ApiResponse<>(500, "No changed information.", null);
        }
        userService.update(userUpdateDto.getUserId(), userUpdateDto.getUserName(), userUpdateDto.getEmail(), userUpdateDto.getUserType());
        Optional<User> userOptional = userService.getUserById(userUpdateDto.getUserId());
        return userOptional.map(user -> new ApiResponse<>(200, "User info updated successfully.", user))
                .orElseGet(() -> new ApiResponse<>(404, "User not found.", null));
    }

    @DeleteMapping("/deleteUser")
    public ApiResponse<String> deleteUser(@RequestParam Long id) {
        userService.deleteUser(id);
        return new ApiResponse<>(200, "User deleted successfully.", null);
    }

    @DeleteMapping("/deleteByUUID")
    public ApiResponse<String> deleteByUUID(@RequestParam UUID uuid) {
        userService.deleteByUUID(uuid);
        return new ApiResponse<>(200,"User deleted successfully.",null);
    }

    @GetMapping("/loginCheck")
    public ApiResponse<Long> loginCheck(@RequestParam String content,
                                          @RequestParam String password) throws NoSuchAlgorithmException {
        Long userId = userService.loginCheck(content, password);
            if (userId != -1) {
                return new ApiResponse<>(200, "You've logged in.", userId);
            } else {
                return new ApiResponse<>(500, "Username/Email or password incorrect.", userId);
        }
    }

    @GetMapping("/findAll")
    public ApiResponse<List<User>> findAll() {
        List<User> userList = userService.findAll();
        return new ApiResponse<>(200, "User list fetched successfully.", userList);
    }

    @GetMapping("/findByContent")
    public ApiResponse<User> findByContent(@RequestParam("content") String content) {
        User result = userService.getUserByContent(content);
        if (result == null) {
            return new ApiResponse<>(500, "User not exists.", null);
        } else {
            return new ApiResponse<>(200, "User found.", result);
        }
    }

    @PostMapping("/changePassword")
    public ApiResponse<String> changePassword(@RequestParam("id")Long id,
                                              @RequestParam("password") String password) throws NoSuchAlgorithmException {
        userService.changePassword(id, password);
        return new ApiResponse<>(200, "Password changed successfully.", null);
    }

    @GetMapping("/calcSim")
    public ApiResponse<List<SimilarityResultDTO>> calcSim(@RequestParam("userId") Long userId,
                                                          @RequestParam("simNum") Integer simNum) {
        return new ApiResponse<>(200, "User Similarity Calculated.", userService.calcSim(userId, simNum));
    }

    @GetMapping("/getRecommend")
    public ApiResponse<List<RecommendationDTO>> getRecommend(@RequestParam("userId") Long userId,
                                                             @RequestParam("recNum") Integer recNum,
                                                             @RequestParam("keyword") String keyword) {
        List<SimilarityResultDTO> simResList = userService.calcSim(userId, recNum);
        List<Long> similarUserIds = new ArrayList<>();
        for (SimilarityResultDTO simRes : simResList) {
            similarUserIds.add(simRes.getUserId2());
        }
        List<RecommendationDTO> recommendList = resourceService.getRecommend(similarUserIds, userId, recNum, keyword);
        if (recommendList.isEmpty()) {
            return new ApiResponse<>(201, "No recommend resource currently.", null);
        } else {
            return new ApiResponse<>(200, "Recommend list is as follows.", recommendList);
        }
    }

    @GetMapping("/getInteractedRes")
    public ApiResponse<List<Interaction>> getInteractedRes(@RequestParam("userId") Long userId) {
        return new ApiResponse<>(200, "Got Interactions.", userService.getInteractedRes(userId));
    }
}
