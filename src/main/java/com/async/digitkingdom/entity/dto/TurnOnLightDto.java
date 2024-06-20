package com.async.digitkingdom.entity.dto;

import com.async.digitkingdom.entity.Args;
import lombok.Data;

@Data
public class TurnOnLightDto {
    private String messageId;
    private String command;
    private Args args;
}
