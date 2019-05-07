package com.kaworu.arknights.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Comb {
    /**
     * 职业
     */
    private String occupationTag;

    /**
     * 性别
     */
    private String sexTag;

    /**
     * Tag
     */
    private List<String> tags;

    /**
     * 结果
     */
    private List<Player> playerList;

    /**
     * 最低
     */
    private String floors;

    /**
     * 各星级数量，从一星到六星
     */
    private List<Integer> levels;


}
