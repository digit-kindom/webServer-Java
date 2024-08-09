package com.async.digitkingdom.mapper;

import com.async.digitkingdom.entity.dto.AirQualityHistoryDto;
import com.async.digitkingdom.entity.vo.ConcentrationMeasurementVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AirQualitySensorHistoryMapper {

    void insert(AirQualityHistoryDto airQualityHistoryDto);

    List<AirQualityHistoryDto> selectData(@Param("clusterId") Integer clusterId,@Param("range") String range,@Param("nodeId") Integer nodeId);
}
