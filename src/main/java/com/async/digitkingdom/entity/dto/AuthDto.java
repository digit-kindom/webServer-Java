package com.async.digitkingdom.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthDto {
    private String type;
    private String access_token;
}
