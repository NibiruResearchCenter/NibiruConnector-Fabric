package moe.alisalab.nibiruconnector.models;

import com.alibaba.fastjson2.annotation.JSONField;

import java.sql.Timestamp;

public class GeneralCommandResponse {

    @JSONField(name = "timestamp")
    private long Timestamp;

    @JSONField(name = "message")
    private String Message;

    public GeneralCommandResponse(String message) {
        Message = message;
        Timestamp = new Timestamp(System.currentTimeMillis()).getTime();
    }

    public long getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(long timestamp) {
        Timestamp = timestamp;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}