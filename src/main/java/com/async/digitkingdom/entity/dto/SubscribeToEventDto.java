package com.async.digitkingdom.entity.dto;

import lombok.Data;

@Data
public class SubscribeToEventDto {
    private int id;
    private String type;
    private String event_type;
}
