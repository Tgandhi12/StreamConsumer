package com.example.stream.service;

import com.example.stream.model.CdcEvent;

public interface EventHandler {

    void handle(CdcEvent event);
}