package com.twilight.twilight.Model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.UUID;


@Node("User")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(GeneratedValue.UUIDGenerator.class)
    private UUID id;
    private String userName;
    private String email;
    private Integer userType;
    private String password;

    public User(UUID id, String userName, String email, Integer userType, String password) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.userType = userType;
        this.password = password;
    }

    public User() {

    }

}
