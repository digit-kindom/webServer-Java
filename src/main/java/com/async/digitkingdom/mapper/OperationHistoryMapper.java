package com.async.digitkingdom.mapper;

import com.async.digitkingdom.entity.OperationHistory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OperationHistoryMapper {
    @Insert("insert into device.operation_history (id, device_id, operation_name, operation_time, operator) VALUE (#{operationId},#{deviceId},#{operationName},#{operationTime},#{userId})")
    void insert(String operationName, Integer userId, String deviceId, LocalDateTime operationTime, String operationId);

    @Select("select * from device.operation_history where operator = #{userId}")
    List<OperationHistory> getAllHistoryByUserId(Integer userId);

    @Delete("delete from device.operation_history where operator = #{userId} and id = #{id}")
    void deleteById(Integer id, Integer userId);
}
