package com.async.digitkingdom.service.impl;

import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.entity.Alert;
import com.async.digitkingdom.mapper.AlertMapper;
import com.async.digitkingdom.service.AlertService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AlertServiceImpl implements AlertService {
    @Resource
    private AlertMapper alertMapper;

    @Override
    public Result getAllAlert(Integer userId) {
        Alert alert = alertMapper.getAllAlertByUserId(userId);
        return Result.ok(alert);
    }

    @Override
    public Result confirmAlert(Integer alertId) {
        Alert alert = alertMapper.getAlertById(alertId);
        if(alert == null) {
            return Result.error("输入了错误的参数");
        }
        if(alert.getIsConfirmed()){
            return Result.error("该警告已被确认！");
        }
        if(alert.getIsDeleted()){
            return Result.error("未找到该警告！");
        }

        alertMapper.confirmAlert(alertId);
        return Result.ok("已确认！");
    }

    @Override
    public Result deleteAlert(Integer alertId) {
        Alert alert = alertMapper.getAlertById(alertId);
        if(alert == null) {
            return Result.error("输入了错误的参数");
        }
        if(alert.getIsDeleted()){
            return Result.error("未找到该警告！");
        }

        alertMapper.deleteById(alertId);
        return Result.ok("已删除！");
    }
}
