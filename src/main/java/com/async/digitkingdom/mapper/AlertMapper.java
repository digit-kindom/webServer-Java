package com.async.digitkingdom.mapper;

import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.entity.Alert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AlertMapper {

    Alert getAllAlertByUserId(@Param("userId") Integer userId);

    Alert getAlertById(@Param("alertId") Integer alertId);

    @Update("update device.alert_collection set is_confirmed = 1 where alert_id = #{alertId}")
    void confirmAlert(Integer alertId);

    @Update("update device.alert_collection set is_delete = 1 where alert_id = #{alertId}")
    void deleteById(Integer alertId);
}
