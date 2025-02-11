package com.twilight.twilight.Model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ResourceDTO {
    private String resourceName;
    private Integer resourceType;
    private Long provider;
    private Date postTime;
    private String content;
}
