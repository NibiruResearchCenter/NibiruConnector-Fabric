package moe.alisalab.nibiruconnector.models;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.List;

public class GroupedStringResponse {

    @JSONField(name = "data")
    public List<String> Data;

    public GroupedStringResponse(List<String> data) {
        Data = data;
    }

}
