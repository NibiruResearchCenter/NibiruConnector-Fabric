package moe.alisalab.nibiruconnector.models;

import com.alibaba.fastjson2.annotation.JSONField;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class WhitelistListResponse {

    @JSONField(name = "timestamp")
    private long Timestamp;

    @JSONField(name = "playerGroup")
    private List<WhitelistListPlayerGroup> PlayerGroup;

    public WhitelistListResponse() {
        Timestamp = new Timestamp(System.currentTimeMillis()).getTime();
        PlayerGroup = new ArrayList<>();
    }

    public void addPlayerGroup(WhitelistListPlayerGroup pg) {
        PlayerGroup.add(pg);
    }

    public long getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(long timestamp) {
        Timestamp = timestamp;
    }

    public List<WhitelistListPlayerGroup> getPlayerList() {
        return PlayerGroup;
    }

    public void setPlayerList(List<WhitelistListPlayerGroup> playerList) {
        PlayerGroup = playerList;
    }
}
