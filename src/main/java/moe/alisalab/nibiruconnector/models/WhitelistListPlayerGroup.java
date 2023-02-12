package moe.alisalab.nibiruconnector.models;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

public class WhitelistListPlayerGroup {

    @JSONField(name = "groupName")
    private String GroupName;

    @JSONField(name = "players")
    private List<String> Players;

    public WhitelistListPlayerGroup(String groupName) {
        GroupName = groupName;
        Players = new ArrayList<>();
    }

    public void addPlayer(String player) {
        Players.add(player);
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public List<String> getPlayers() {
        return Players;
    }

    public void setPlayers(List<String> players) {
        Players = players;
    }
}
