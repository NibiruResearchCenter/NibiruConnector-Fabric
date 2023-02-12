package moe.alisalab.nibiruconnector.models;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

public class WhitelistListPlayerGroup {

    @JSONField(name = "groupName")
    public String GroupName;

    @JSONField(name = "players")
    public List<String> Players;

    public WhitelistListPlayerGroup(String groupName) {
        GroupName = groupName;
        Players = new ArrayList<>();
    }

    public void addPlayer(String player) {
        Players.add(player);
    }
}
