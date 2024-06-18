package com.async.digitkingdom.controller;

import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.service.OperationHistoryService;
import com.async.digitkingdom.service.impl.OperationHistoryServiceImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operationHistory")
public class OperationHistoryController {

    private final OperationHistoryService operationHistoryService;

    public OperationHistoryController(OperationHistoryService operationHistoryService) {
        this.operationHistoryService = operationHistoryService;
    }

    @GetMapping
    public Result selectAllHistory(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        return operationHistoryService.seleteHistory(pageNum, pageSize);
    }

    @DeleteMapping
    public Result deleteHistory(@RequestParam Integer id){
        return operationHistoryService.deleteHistory(id);
    }
}
