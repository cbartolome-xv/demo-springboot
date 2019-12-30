package com.xv.cbartolome.demospringboot.controller;

import com.xv.cbartolome.demospringboot.service.StatusService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @Autowired
    private StatusService service;

    @RequestMapping("/status")
    public String getStatus() {
        return service.getStatus();
    }
} 
