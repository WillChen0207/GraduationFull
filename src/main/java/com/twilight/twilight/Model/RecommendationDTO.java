package com.twilight.twilight.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendationDTO {
    private Long resourceId;
    private String resourceName;
    private Integer resourceType;
    private Double score;
}

