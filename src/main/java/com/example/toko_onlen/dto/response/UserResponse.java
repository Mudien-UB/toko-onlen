package com.example.toko_onlen.dto.response;

import com.example.toko_onlen.model.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Setter;

import java.util.Date;

@Setter
@Builder
public class UserResponse {

    private String id;
    private String username;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    public static UserResponse of(User user) {
        if(user == null) return null;
        return UserResponse.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
