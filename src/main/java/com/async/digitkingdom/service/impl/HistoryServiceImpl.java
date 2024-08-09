package com.async.digitkingdom.service.impl;

import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.entity.dto.AirQualityHistoryDto;
import com.async.digitkingdom.mapper.AirQualitySensorHistoryMapper;
import com.async.digitkingdom.service.HistoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryServiceImpl implements HistoryService {
    private final AirQualitySensorHistoryMapper airQualitySensorHistoryMapper;

    public HistoryServiceImpl(AirQualitySensorHistoryMapper airQualitySensorHistoryMapper) {
        this.airQualitySensorHistoryMapper = airQualitySensorHistoryMapper;
    }

    @Override
    public Result getResentData(Integer clusterId, String range, Integer nodeId) {
        List<AirQualityHistoryDto> airQualityHistoryDtos = airQualitySensorHistoryMapper.selectData(clusterId,range,nodeId);
        return Result.ok(airQualityHistoryDtos);
    }
}
