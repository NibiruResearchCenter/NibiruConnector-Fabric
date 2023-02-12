package moe.alisalab.nibiruconnector.models;

import com.alibaba.fastjson2.annotation.JSONField;

import java.sql.Timestamp;

public class GeneralCommandResponse {

    @JSONField(name = "timestamp")
    public long Timestamp;

    @JSONField(name = "message")
    public String Message;

    public GeneralCommandResponse(String message) {
        Message = message;
        Timestamp = new Timestamp(System.currentTimeMillis()).getTime();
    }
}