package com.async.digitkingdom.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ManifestDto {
    private String type;
    private String integration;
    private Long id;
}
