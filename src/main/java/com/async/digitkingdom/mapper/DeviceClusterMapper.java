package com.async.digitkingdom.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DeviceClusterMapper {
    @Insert("insert into device_cluster (device_id, cluster, endpoint) VALUES (#{deviceId},#{clusterId},#{endpoint})")
    void addDeviceCluster(@Param("clusterId") int clusterId, @Param("endpoint")int endpoint, @Param("deviceId")String deviceId);
}
