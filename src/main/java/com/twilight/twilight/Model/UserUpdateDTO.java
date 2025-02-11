package com.twilight.twilight.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {
    private Long userId;
    private String userName;
    private String email;
    private String password;
    private Integer userType;
}
