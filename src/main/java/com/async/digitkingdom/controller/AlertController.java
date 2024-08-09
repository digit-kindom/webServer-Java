package com.async.digitkingdom.controller;

import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.entity.LoginUser;
import com.async.digitkingdom.service.AlertService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/alert")
public class AlertController {
    @Resource
    private AlertService alertService;

    @GetMapping()
    public Result getAllAlert(){
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = loginUser.getUser().getId();
        return alertService.getAllAlert(userId);
    }

    @GetMapping("/confirm")
    public Result confirmAlert(Integer alertId){
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = loginUser.getUser().getId();
        return alertService.confirmAlert(alertId);
    }

    @GetMapping("/delete/{alertId}")
    public Result deleteAlert(@PathVariable Integer alertId){
        return alertService.deleteAlert(alertId);
    }
}
