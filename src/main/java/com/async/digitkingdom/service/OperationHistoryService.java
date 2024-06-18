package com.async.digitkingdom.service;

import com.async.digitkingdom.common.Result;

public interface OperationHistoryService {
    Result seleteHistory(Integer pageNum, Integer pageSize);

    Result deleteHistory(Integer id);
}
