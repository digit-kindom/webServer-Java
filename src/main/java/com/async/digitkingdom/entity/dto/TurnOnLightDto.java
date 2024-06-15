package com.async.digitkingdom.entity.dto;

import com.async.digitkingdom.entity.Args;

public class TurnOnLightDto {
    private String message_id = "2";
    private String command = "commission_with_code";
    private Args args = new Args("MT:Y.ABCDEFG123456789");

    @Override
    public String toString() {
        return "{\"message_id\": \"" + message_id + "\", \"command\": \"" + command + "\", \"args\": {\"code\": \"" + args.getCode() + "\"}}";
    }
}
