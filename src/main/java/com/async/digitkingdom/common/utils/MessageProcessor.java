package com.async.digitkingdom.common.utils;

import org.springframework.stereotype.Component;

@Component
public class MessageProcessor {

    private String latestMessage;

    public void process(String message) {
        System.out.println("Processing message: " + message);
        this.latestMessage = message;
    }

    public String getLatestMessage() {
        return latestMessage;
    }
}
