package com.async.digitkingdom.entity.dto;

import com.async.digitkingdom.entity.Args;
import com.async.digitkingdom.entity.OperateFanArgs;
import lombok.Data;

@Data
public class OperateFanDto {
    private String message_id;
    private String command;
    private OperateFanArgs args;
}
