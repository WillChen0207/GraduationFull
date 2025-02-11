package com.twilight.twilight.Model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Date;
import java.util.UUID;


@Node("Resource")
@Getter
@Setter
public class Resource {
    @Id
    @GeneratedValue(GeneratedValue.UUIDGenerator.class)
    private UUID id;
    private String resourceName;
    private Integer resourceType;
    private Long provider;
    private Date postTime;
    private String content;

    public Resource() {

    }

    public Resource(UUID id, String resourceName, Integer resourceType, Long provider, Date postTime, String content) {
        this.id = id;
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.provider = provider;
        this.postTime = postTime;
        this.content = content;
    }

    public Resource(String resourceName, Integer resourceType, Long provider, Date postTime, String content) {
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.provider = provider;
        this.postTime = postTime;
        this.content = content;
    }
}
