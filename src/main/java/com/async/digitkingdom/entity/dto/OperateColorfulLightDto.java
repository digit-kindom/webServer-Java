package com.async.digitkingdom.entity.dto;

import com.async.digitkingdom.entity.OperateColorfulLightArgs;
import com.async.digitkingdom.entity.OperateFanArgs;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OperateColorfulLightDto {
    private String message_id;
    private String command;
    private OperateColorfulLightArgs args;
    private Integer cluster_id;
    private String command_name;
}
