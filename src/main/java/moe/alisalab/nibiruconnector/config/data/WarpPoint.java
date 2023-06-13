package moe.alisalab.nibiruconnector.config.data;

import com.alibaba.fastjson2.annotation.JSONField;

public class WarpPoint {
    @JSONField(name = "name")
    public String name;
    @JSONField(name = "world")
    public String world;
    @JSONField(name = "x")
    public double x;
    @JSONField(name = "y")
    public double y;
    @JSONField(name = "z")
    public double z;
    @JSONField(name = "yaw")
    public float yaw;
    @JSONField(name = "pitch")
    public float pitch;
}
