package com.kaworu.arknights.cache;

import com.kaworu.arknights.model.Comb;
import com.kaworu.arknights.service.RecruitService;
import com.kaworu.arknights.utils.SpringUtil;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 已使用caffeine作为代替方案
 */
@Deprecated
public enum CombCache {
    INSTANCE;

    private static ConcurrentHashMap<String, List<Comb>> combConcurrentHashMap;
    private static RecruitService recruitService;

    static {
        combConcurrentHashMap = new ConcurrentHashMap<>();
        recruitService = SpringUtil.getBean(RecruitService.class);
    }

    /**
     * 从缓存中获取组合结果
     * 不存在时计算
     * @param occupationTagList
     * @param sexTagList
     * @param tagList
     * @return
     */
    public List<Comb> find(List<String> occupationTagList, List<String> sexTagList, List<String> tagList){
        String tagBuilder = buildString(occupationTagList) + buildString(sexTagList) + buildString(tagList);
        List<Comb> combList = combConcurrentHashMap.get(tagBuilder);
        if(combList == null || combList.size() < 0){
            List<Comb> newCombList = recruitService.combination(occupationTagList,sexTagList,tagList);
            combConcurrentHashMap.put(tagBuilder, newCombList);
            return newCombList;
        }else{
            return combList;
        }
    }

    /**
     * 清除缓存
     */
    public void clear(){
        combConcurrentHashMap = new ConcurrentHashMap<>();
    }

    private String buildString(List<String> stringList){
        if(stringList == null || stringList.size() <= 0){
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for(String str : stringList){
            stringBuilder.append(str == null ? "" : str);
        }
        return stringBuilder.toString();
    }
}
