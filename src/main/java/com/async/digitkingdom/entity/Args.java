package com.async.digitkingdom.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Args {
    private String code;
    private Integer node_id;
    private Integer endpoint_id;
    private Integer cluster_id;
    private String command_name;
    private String response_type;
    private String timed_request_timeout_ms;
    private String interaction_timeout_ms;
    private Payload payload;
}
