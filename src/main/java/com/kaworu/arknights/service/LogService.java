package com.kaworu.arknights.service;

import com.kaworu.arknights.model.Comb;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

public interface LogService {
    void save(String content, List<Comb> request, ServerWebExchange exchange);
}
