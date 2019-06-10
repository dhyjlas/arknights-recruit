package com.kaworu.arknights.service.impl;

import com.kaworu.arknights.model.Comb;
import com.kaworu.arknights.model.Player;
import com.kaworu.arknights.repository.PlayerRepository;
import com.kaworu.arknights.service.RecruitService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class RecruitServiceImpl implements RecruitService{
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private RecruitService recruitService;

    @Override
    @Cacheable(value="player", key="1")
    public List<Player> list(){
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

    /**
     * 按选择条件筛选
     * @param occupationTag
     * @param sexTag
     * @param tags0
     * @return
     */
    @Override
    @Cacheable(value="tags", key="#root.target.getTagsKey(#occupationTag,#sexTag,#tags0)")
    public List<Player> findByTags(String occupationTag, String sexTag, List<String> tags0){
        List<String> tags = new ArrayList<>(tags0);
        boolean level5 = false;
        boolean level6 = false;
        for(String tag : tags){
            if("资深干员".equals(tag)){
                level5 = true;
            } else if("高级资深干员".equals(tag)){
                level6 = true;
            }
        }
        tags.remove("资深干员");
        tags.remove("高级资深干员");

        List<Player> result = new ArrayList<>();
        for(Player player : recruitService.list()){
            //判断职业是否满足
            if(!StringUtils.isEmpty(occupationTag) && !occupationTag.equals(player.getOccupation())){
                continue;
            }

            //判断性别是否满足
            if(!StringUtils.isEmpty(sexTag) && !sexTag.equals(player.getSex())){
                continue;
            }

            //筛选tag为空时直接满足
            if(tags != null && tags.size() > 0){
                //player.tag为空时直接不满足
                if(player.getTags() == null || player.getTags().size() < 1){
                    continue;
                }

                boolean flag = true;  //是否满足
                for(String tag : tags){
                    if(!player.getTags().contains(tag)){
                        flag = false;
                        break;
                    }
                }
                if(!flag){
                    continue;
                }
            }

            result.add(player);
        }

        Collections.sort(result, (o1, o2) -> {
            if(o1.getLevel().compareTo(o2.getLevel()) > 0){
                return 1;
            }else if(o1.getLevel().compareTo(o2.getLevel()) < 0){
                return -1;
            }else{
                return 0;
            }
        });

        if(level5 && !level6){
            return screenLevel(result, "5");
        }

        if(!level5 && level6){
            return screenLevel(result, "6");
        }

        if(level5 && level6){
            return screenLevel(result, "5", "6");
        }

        return screenLevel(result, "3", "4", "5");
    }

    /**
     * 筛选星级
     * @param playerList
     * @param level
     * @return
     */
    private List<Player> screenLevel(List<Player> playerList, String... level){
        List<Player> result = new ArrayList<>();
        for(Player player : playerList){
            if(Arrays.asList(level).contains(player.getLevel())){
                result.add(player);
            }
        }

        return result;
    }

    /**
     * 组合选择各种情况
     * @param occupationTags
     * @param sexTags
     * @param tags
     * @return
     */
    @Override
    @Cacheable(value="comb", key="#root.target.getCombKey(#occupationTags,#sexTags,#tags)")
    public List<Comb> combination(List<String> occupationTags, List<String> sexTags, List<String> tags){
        List<Comb> combList = new ArrayList<>();
        for(String occupationTag : occupationTags){  //组合职业
            for(String sexTag : sexTags){            //组合性别
                int n = tags.size();
                int nbit = 1 << n;
                for(int i = 0; i < nbit; i++) {      //组合Tag
                    List<String> combTags = new ArrayList<>();
                    for(int j = 0; j < n; j++) {
                        int tmp = 1 << j ;
                        if((tmp & i) != 0) {
                            combTags.add(tags.get(j));
                        }
                    }

                    Comb comb = new Comb();
                    comb.setOccupationTag(occupationTag);
                    comb.setSexTag(sexTag);
                    comb.setTags(combTags);
                    comb.setPlayerList(recruitService.findByTags(occupationTag, sexTag, combTags));
//                    System.out.println("职业：" + comb.getOccupationTag() + "  性别：" + comb.getSexTag() + "  Tag：" + comb.getTags() + "  结果数：" + comb.getPlayerList().size());
                    if(comb.getPlayerList().size() > 0){
                        statistics(comb);
                        combList.add(comb);
                    }
                }
            }
        }

        sort(combList);
        return combList;
    }

    /**
     * 统计可能性结果
     * @param comb
     */
    private void statistics(Comb comb){
        Integer[] levels = {0, 0, 0, 0, 0 ,0};

        comb.setFloors("6");
        for(Player player : comb.getPlayerList()){
            if(player.getLevel().compareTo(comb.getFloors()) < 0){
                comb.setFloors(player.getLevel());
            }
            levels[Integer.valueOf(player.getLevel()) - 1] ++;
        }

        comb.setLevels(Arrays.asList(levels));
    }

    /**
     * 对结果进行推荐排序
     * @param combList
     */
    private void sort(List<Comb> combList){
        Collections.sort(combList, (o1, o2) -> {
            if(o1.getFloors().compareTo(o2.getFloors()) < 0) {
                return 1;
            }else if(o1.getFloors().compareTo(o2.getFloors()) > 0){
                return -1;
            }else{
                int floors = Integer.valueOf(o1.getFloors());
                for(int i = floors ; i <= 6 ;i++){
                    if(o1.getLevels().get(i - 1) > o2.getLevels().get(i - 1)){
                        return 1;
                    }else if(o1.getLevels().get(i - 1) < o2.getLevels().get(i - 1)){
                        return -1;
                    }
                }

                int tagNumber1 = (StringUtils.isEmpty(o1.getOccupationTag()) ? 0 : 1) + (StringUtils.isEmpty(o1.getSexTag()) ? 0 : 1) + o1.getTags().size();
                int tagNumber2 = (StringUtils.isEmpty(o2.getOccupationTag()) ? 0 : 1) + (StringUtils.isEmpty(o2.getSexTag()) ? 0 : 1) + o2.getTags().size();
                if(tagNumber1 > tagNumber2){
                    return 1;
                }else if(tagNumber1 < tagNumber2){
                    return -1;
                }else {
                    return 0;
                }
            }
        });
    }

    /**
     * 数据爬取
     * @throws IOException
     */
    @Override
    public void crawl() throws IOException {
        playerRepository.deleteAll();
        Document document = Jsoup.connect("http://wiki.joyme.com/arknights/%E5%B9%B2%E5%91%98%E6%95%B0%E6%8D%AE%E8%A1%A8")
                .header("X-DevTools-Emulate-Network-Conditions-Client-Id", "8C56E9AF561964EA7FAE697309B115CE")
                .header("upgrade-insecure-requests", "1")
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36")
                .get();

        Elements elements = document.body().select("table[id=CardSelectTr]").select("tr");

        int i = 0;
        for (Element e : elements) {
            if((i++) == 0)
                continue;
            Elements playInfo = e.select("td");

            Player player = new Player();
            player.setName(playInfo.get(1).text());
            player.setOccupation(playInfo.get(3).text());
            player.setLevel(playInfo.get(4).text());
            player.setSex(playInfo.get(5).text());
            player.setAccess(playInfo.get(7).text().replaceAll("、", ","));
            player.setTag(playInfo.get(18).text().replaceAll("、", ","));
            player.setPic(playInfo.get(0).select("div[class=floatnone]").select("img").attr("src"));
            player.setSrc("http://wiki.joyme.com" + playInfo.get(0).select("div[class=floatnone]").select("a").attr("href"));
            playerRepository.save(player);
        }
    }

    @CacheEvict(value="tags", allEntries=true)
    public void clearTags() {
    }

    @CacheEvict(value="comb", allEntries=true)
    public void clearComb() {
    }

    @CacheEvict(value="player", allEntries=true)
    public void clearPlayer() {
    }

    public String getTagsKey(String occupationTag, String sexTag, List<String> tags0){
        return occupationTag + "-" + sexTag + "-"  + buildString(tags0);
    }

    public String getCombKey(List<String> occupationTags, List<String> sexTags, List<String> tags){
        return buildString(occupationTags) + "-"  + buildString(sexTags) + "-"  + buildString(tags);
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
