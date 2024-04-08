package com.async.digitkingdom.entity.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterUserDTO {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dateJoined;
    private LocalDate lastLogin;
    private Boolean isActive;
}
