package com.kaworu.arknights.service;

import com.kaworu.arknights.model.Comb;
import com.kaworu.arknights.model.Player;

import java.io.IOException;
import java.util.List;

public interface RecruitService {

    List<Player> list();

    /**
     * 按选择条件筛选
     */
    List<Player> findByTags(String occupationTag, String sexTag, List<String> tags0);

    /**
     * 组合选择各种情况
     */
    List<Comb> combination(List<String> occupationTags, List<String> sexTags, List<String> tags);

    /**
     * 数据爬取
     */
    void crawl() throws IOException;

    void clearTags();

    void clearComb();

    void clearPlayer();
}
