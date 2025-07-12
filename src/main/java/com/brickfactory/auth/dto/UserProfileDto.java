package com.brickfactory.auth.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class UserProfileDto {
    private Long id;
    private String email;
    private String fullName;
    private Instant createdAt;
}