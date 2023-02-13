package moe.alisalab.nibiruconnector.models;

import com.alibaba.fastjson2.annotation.JSONField;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class WhitelistListPlayer {

    @JSONField(name = "name")
    public String PlayerName;

    @JSONField(name = "lastJoin")
    public long LastJoin;

    @JSONField(name = "daysSinceLastJoin")
    public long DaysSinceLastJoin;

    public WhitelistListPlayer(String playerName, Instant lastJoin) {
        PlayerName = playerName;
        LastJoin = lastJoin.toEpochMilli();
        DaysSinceLastJoin = ChronoUnit.DAYS.between(Instant.now(), lastJoin);
    }

    public WhitelistListPlayer(String playerName, long lastJoin, long daysSinceLastJoin) {
        PlayerName = playerName;
        LastJoin = lastJoin;
        DaysSinceLastJoin = daysSinceLastJoin;
    }

}
