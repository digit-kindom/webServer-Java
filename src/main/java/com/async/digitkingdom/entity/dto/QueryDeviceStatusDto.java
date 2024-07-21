package com.async.digitkingdom.entity.dto;

import com.async.digitkingdom.entity.Args;
import lombok.Data;

@Data
public class QueryDeviceStatusDto {
    private String messageId;
    private String command;
    private Integer node_id;
    private String attribute_path;
}
