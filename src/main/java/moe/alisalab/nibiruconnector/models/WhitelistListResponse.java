package moe.alisalab.nibiruconnector.models;

import com.alibaba.fastjson2.annotation.JSONField;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class WhitelistListResponse {

    @JSONField(name = "timestamp")
    public long Timestamp;

    @JSONField(name = "playerGroups")
    public List<WhitelistListPlayerGroup> PlayerGroups;

    public WhitelistListResponse() {
        Timestamp = new Timestamp(System.currentTimeMillis()).getTime();
        PlayerGroups = new ArrayList<>();
    }

    public void addPlayerGroup(WhitelistListPlayerGroup pg) {
        PlayerGroups.add(pg);
    }
}
