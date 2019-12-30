package com.xv.cbartolome.demospringboot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StatusService {

    @Value("${status.name}")
    private String statusProperty;

    public String getStatus() {
        return statusProperty;
    }

}