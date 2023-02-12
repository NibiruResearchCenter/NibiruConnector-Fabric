package moe.alisalab.nibiruconnector.models;

import com.alibaba.fastjson2.annotation.JSONField;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class WhitelistListResponse {

    @JSONField(name = "timestamp")
    private long Timestamp;

    @JSONField(name = "playerGroups")
    private List<WhitelistListPlayerGroup> PlayerGroups;

    public WhitelistListResponse() {
        Timestamp = new Timestamp(System.currentTimeMillis()).getTime();
        PlayerGroups = new ArrayList<>();
    }

    public void addPlayerGroup(WhitelistListPlayerGroup pg) {
        PlayerGroups.add(pg);
    }

    public long getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(long timestamp) {
        Timestamp = timestamp;
    }

    public List<WhitelistListPlayerGroup> getPlayerList() {
        return PlayerGroups;
    }

    public void setPlayerList(List<WhitelistListPlayerGroup> playerList) {
        PlayerGroups = playerList;
    }
}
