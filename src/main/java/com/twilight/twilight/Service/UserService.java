package com.twilight.twilight.Service;

import com.twilight.twilight.Model.InfoDTO;
import com.twilight.twilight.Model.Interaction;
import com.twilight.twilight.Model.SimilarityResultDTO;
import com.twilight.twilight.Model.User;
import com.twilight.twilight.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UtilService utilService;

    public UserService(UserRepository userRepository, UtilService utilService) {
        this.userRepository = userRepository;
        this.utilService = utilService;
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }
    public InfoDTO getInfoById(Long id) {
        return userRepository.getInfoById(id);
    }

    public void addUser(User user) {
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void deleteByUUID(UUID uuid) {
        userRepository.deleteByUUID(uuid);
    }

    public void update(Long userId, String userName, String email, Integer userType) {
        Optional<User> currentUser = userRepository.findById(userId);
        if (currentUser.isPresent()) {
            User user = currentUser.get();
            user.setUserName(userName);
            user.setEmail(email);
            userRepository.save(user);
        }
    }


    public void changePassword(Long id, String password) throws NoSuchAlgorithmException {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User newUser = user.get();
            String newPassword = utilService.hashEncrypt(password);
            newUser.setPassword(newPassword);
        }
    }

    public Long loginCheck(String content, String password) throws NoSuchAlgorithmException {
        String newPassword = utilService.hashEncrypt(password);
        Optional<Long> result = userRepository.loginCheck(content, newPassword);
        return result.orElseGet(() -> (long) -1);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User getUserByContent(String content) {
        return userRepository.findByContent(content);
    }

    public List<SimilarityResultDTO> calcSim(Long userId, Integer simNum) {
        return userRepository.calcSim(userId, simNum);
    }

    public List<Interaction> getInteractedRes(Long userId) {
        return userRepository.getInteractedRes(userId);
    }
}
