package com.async.digitkingdom.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OperateFanArgs {
    private Integer node_id;
    private String attribute_path;
    private Integer value;
}
