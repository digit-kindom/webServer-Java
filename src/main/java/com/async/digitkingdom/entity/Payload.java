package com.async.digitkingdom.entity;

import lombok.Data;

@Data
public class Payload {
    private Integer level;
    private Integer transitionTime;
    private Integer optionsMask;
    private Integer optionsOverride;
}
