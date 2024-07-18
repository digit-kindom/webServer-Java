package com.async.digitkingdom.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PersistentNotificationDto {
    private String type;
    private Long id;
}
