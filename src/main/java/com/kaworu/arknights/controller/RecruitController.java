package com.kaworu.arknights.controller;

import com.kaworu.arknights.cache.CombCache;
import com.kaworu.arknights.cache.PlayerCache;
import com.kaworu.arknights.model.Comb;
import com.kaworu.arknights.model.Player;
import com.kaworu.arknights.service.RecruitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RestController
public class RecruitController {
    @Value("${crawl.enable}")
    private boolean crawl;

    @Autowired
    private RecruitService recruitService;

    @GetMapping("/list")
    public Mono<List> list(){
        return Mono.just(recruitService.list());
    }

    @GetMapping("/find")
    public Mono<List> find(@Valid @RequestParam(value = "occupationTag", defaultValue = "") String occupationTag,
                           @Valid @RequestParam(value = "sexTag", defaultValue = "") String sexTag,
                           @Valid @RequestParam(value = "tags", defaultValue = "") String tags){
        List<String> tagList = StringUtils.isEmpty(tags) ? new ArrayList<>() : new ArrayList<>(Arrays.asList(tags.split(",")));
        List<Player> playerList = recruitService.findByTags(occupationTag, sexTag, tagList);
        return Mono.just(playerList);
    }

    @GetMapping("/comb")
    public Mono<List> comb(@Valid @RequestParam(value = "occupationTags", defaultValue = "") String occupationTags,
                           @Valid @RequestParam(value = "sexTags", defaultValue = "") String sexTags,
                           @Valid @RequestParam(value = "tags", defaultValue = "") String tags){
        List<String> occupationTagList = StringUtils.isEmpty(occupationTags) ? new ArrayList<>() : new ArrayList<>(Arrays.asList((occupationTags).split(",")));
        occupationTagList.add("");
        List<String> sexTagList = StringUtils.isEmpty(sexTags) ? new ArrayList<>() : new ArrayList<>(Arrays.asList((sexTags).split(",")));
        sexTagList.add("");
        List<String> tagList = StringUtils.isEmpty(tags) ? new ArrayList<>() : Arrays.asList((tags).split(","));
//        List<Comb> combList = recruitService.combination(occupationTagList, sexTagList, tagList);
        List<Comb> combList = CombCache.INSTANCE.find(occupationTagList, sexTagList, tagList);

        return Mono.just(combList);
    }

    /**
     * 重新获取数据，同时清除缓存
     * @return
     */
    @GetMapping("/crawl")
    public Mono<String> crawl(){
        if(!crawl){
            return Mono.just("接口已关闭");
        }

        try {
            recruitService.crawl();
            CombCache.INSTANCE.clear();
            return Mono.just("数据已爬取");
        } catch (IOException e) {
            e.printStackTrace();
            return Mono.just("数据已爬取失败\n" + e.toString());
        }
    }

    /**
     * 刷新
     * @return
     */
    @GetMapping("/refresh")
    public Mono<String> refresh(){
        PlayerCache.INSTANCE.refresh();
        CombCache.INSTANCE.clear();
        return Mono.just("干员数据已重新加载");
    }
}
