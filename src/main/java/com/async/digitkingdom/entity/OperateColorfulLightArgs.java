package com.async.digitkingdom.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OperateColorfulLightArgs {
    private Integer endpoint_id;
    private Integer node_id;
    private ColorfulLightPayload payload;
}
