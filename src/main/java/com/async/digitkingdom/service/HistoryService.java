package com.async.digitkingdom.service;

import com.async.digitkingdom.common.Result;

public interface HistoryService {
    Result getResentData(Integer clusterId, String range,Integer nodeId);
}
