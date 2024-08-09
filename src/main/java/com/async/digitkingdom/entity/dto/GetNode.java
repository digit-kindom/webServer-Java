package com.async.digitkingdom.entity.dto;

import com.async.digitkingdom.entity.Args;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetNode {
    private String command;
    private Args args;
    private String message_id;
}
