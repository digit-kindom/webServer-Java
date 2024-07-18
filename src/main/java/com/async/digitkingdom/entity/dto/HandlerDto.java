package com.async.digitkingdom.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HandlerDto {
    private String handler;
    private Boolean show_advanced_options;
}
