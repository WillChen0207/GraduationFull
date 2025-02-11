package com.twilight.twilight.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InfoDTO {
    private Long userId;
    private String userName;
    private String email;
    private Integer userType;

    public InfoDTO(Long userId, String userName, String email, Integer userType) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.userType = userType;
    }

    public InfoDTO() {
    }
}
