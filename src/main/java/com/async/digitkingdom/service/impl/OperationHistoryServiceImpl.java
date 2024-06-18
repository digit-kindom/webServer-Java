package com.async.digitkingdom.service.impl;

import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.entity.LoginUser;
import com.async.digitkingdom.entity.OperationHistory;
import com.async.digitkingdom.mapper.OperationHistoryMapper;
import com.async.digitkingdom.service.OperationHistoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationHistoryServiceImpl implements OperationHistoryService {
    private final OperationHistoryMapper operationHistoryMapper;

    public OperationHistoryServiceImpl(OperationHistoryMapper operationHistoryMapper) {
        this.operationHistoryMapper = operationHistoryMapper;
    }

    @Override
    public Result seleteHistory(Integer pageNum, Integer pageSize) {
        LoginUser user = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = user.getUser().getId();
        List<OperationHistory> operationHistories = operationHistoryMapper.getAllHistoryByUserId(userId);
        PageHelper pageHelper = new PageHelper();
        pageHelper.startPage(pageNum, pageSize);
        PageInfo pageInfo = new PageInfo(operationHistories);
        return Result.ok(pageInfo);
    }

    @Override
    public Result deleteHistory(Integer id) {
        LoginUser user = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = user.getUser().getId();
        try {
            operationHistoryMapper.deleteById(id, userId);
        }catch (Exception e){
            return Result.error("删除失败");
        }
        return Result.ok("删除成功！");
    }
}
