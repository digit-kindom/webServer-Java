package com.async.digitkingdom.entity.dto;

import com.async.digitkingdom.entity.Args;
import lombok.Data;

@Data
public class AdjustLightDto {
    private String message_id;
    private String command;
    private Args args;
    private String deviceId;
    private Integer level;
    private Integer transitionTime;
    private Integer optionsMask;
    private Integer optionsOverride;
}
