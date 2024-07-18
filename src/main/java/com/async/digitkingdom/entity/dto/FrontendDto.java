package com.async.digitkingdom.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FrontendDto {
    private String type;
    private String language;
    private String category;
    private List integration;
    private Long id;
}
