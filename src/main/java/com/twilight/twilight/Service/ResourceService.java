package com.twilight.twilight.Service;

import com.twilight.twilight.Model.InteractionDTO;
import com.twilight.twilight.Model.RecommendationDTO;
import com.twilight.twilight.Model.Resource;
import com.twilight.twilight.Repository.ResourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public void addResource(Resource resource) {
        resourceRepository.save(resource);
    }

    public void deleteResource(Long id) {
        resourceRepository.deleteById(id);
    }

    public void deleteByUUID(UUID uuid) {
        resourceRepository.deleteByUUID(uuid);
    }

    public Optional<Resource> getResourceById(Long id) {
        return resourceRepository.findById(id);
    }

    public List<Resource> findAll() {
        return resourceRepository.findAll();
    }

    public void provide(Long userId, Long resourceId) {
        resourceRepository.provide(userId, resourceId);
    }

    public Long getId(UUID uuid) {
        return resourceRepository.getId(uuid);
    }

    public void clear(Long resourceId) {
        resourceRepository.clear(resourceId);
    }

    public String download(Long userId, Long resourceId) {
        return resourceRepository.download(userId, resourceId);
    }

    public void collect(Long userId, Long resourceId, Integer flag) {
        resourceRepository.collect(userId, resourceId, flag);
    }

    public void view(Long userId, Long resourceId) {
        resourceRepository.view(userId, resourceId);
    }

    public void like(Long userId, Long resourceId, Integer flag) {
        resourceRepository.like(userId, resourceId, flag);
    }

    public List<RecommendationDTO> getRecommend(List<Long> similarUserIds, Long userId, Integer recNum, String keyword) {
        return resourceRepository.getRecommend(similarUserIds, userId, recNum, keyword);
    }

    public InteractionDTO getInteractionStates(Long userId, Long resourceId) {
        return resourceRepository.getInteractionStates(userId, resourceId);
    }

//    public Double calcPreference(Long userId) {
//        return resourceRepository.calcPreference(userId);
//    }

}
