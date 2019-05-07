package com.kaworu.arknights.cache;

import com.kaworu.arknights.model.Player;
import com.kaworu.arknights.repository.PlayerRepository;
import com.kaworu.arknights.utils.SpringUtil;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum PlayerCache {
    INSTANCE;

    private static List<Player> playerList;

    static {
        playerList = init();
    }

    /**
     * 获取play列表
     * @return
     */
    public List<Player> list(){
        return playerList;
    }

    /**
     * 刷新列表
     * @return
     */
    public void refresh(){
        playerList = init();
    }

    private static List<Player> init(){
        PlayerRepository playerRepository = SpringUtil.getBean(PlayerRepository.class);
        List<Player> playerList = playerRepository.findByAccessLike("%公开招募%");
        for(Player player : playerList){
            if(StringUtils.isEmpty(player.getTag())){
                player.setTags(new ArrayList<>());
            }else {
                player.setTags(Arrays.asList(player.getTag().split(",")));
            }
        }
        return playerList;
    }

}
