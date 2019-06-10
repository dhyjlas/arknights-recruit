package com.kaworu.arknights.service.impl;

import com.alibaba.fastjson.JSON;
import com.kaworu.arknights.model.Comb;
import com.kaworu.arknights.model.Log;
import com.kaworu.arknights.repository.LogRepository;
import com.kaworu.arknights.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class LogServiceImpl implements LogService{
    @Autowired
    private LogRepository logRepository;

    @Override
    @Async
    public void save(String content, List<Comb> result, ServerWebExchange exchange) {
        Log log = new Log();
        log.setContent(content);
        log.setIp(exchange.getRequest().getRemoteAddress().getHostString());
//        log.setResult(JSON.toJSONString(result));
        log.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        logRepository.save(log);
    }
}
