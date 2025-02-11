package com.twilight.twilight.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Interaction {
    private Long userId;
    private Long resourceId;
    private String resourceName;
    private Integer resourceType;
    private Long view;
    private Integer like;
    private Integer collect;
    private Long download;
}
