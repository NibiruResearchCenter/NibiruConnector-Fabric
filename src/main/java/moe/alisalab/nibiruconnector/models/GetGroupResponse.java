package moe.alisalab.nibiruconnector.models;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.List;

public class GetGroupResponse {

    @JSONField(name = "groups")
    public List<String> Groups;

    public GetGroupResponse(List<String> groups) {
        Groups = groups;
    }

}
