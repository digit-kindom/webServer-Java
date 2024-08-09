package com.async.digitkingdom.service;

import com.async.digitkingdom.common.Result;

public interface AlertService {
    Result getAllAlert(Integer userId);

    Result confirmAlert(Integer alertId);

    Result deleteAlert(Integer alertId);
}
