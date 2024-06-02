package com.async.digitkingdom.mapper;

import com.async.digitkingdom.entity.Device;
import com.async.digitkingdom.entity.LoginUser;
import com.async.digitkingdom.entity.dto.UpdateDeviceDto;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface DeviceMapper {
    void insert(@Param("device") Device device);

//    @Select("select * from device")
//    List<Device> getAll();

    @Select("select * from device where user_id = #{userId}")
    List<Device> getAllByUserId(Integer userId);

    @Select("select * from device where device_id = #{deviceId}")
    Device getByDeviceId(String deviceId);

    @Delete("delete from device where device_id = #{deviceId}")
    void deleteByDeviceId(String deviceId);

    void update(UpdateDeviceDto updateDeviceDto);
}
