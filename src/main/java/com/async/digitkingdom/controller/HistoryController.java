package com.async.digitkingdom.controller;

import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.service.HistoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController()
@RequestMapping("/history")
public class HistoryController {

    @Resource
    private HistoryService historyService;

    @GetMapping()
    public Result getRecentData(@RequestParam Integer clusterId, @RequestParam String range, @RequestParam Integer nodeId){
        return historyService.getResentData(clusterId,range,nodeId);
    }
}
