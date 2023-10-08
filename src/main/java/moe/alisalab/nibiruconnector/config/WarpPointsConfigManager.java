package moe.alisalab.nibiruconnector.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import moe.alisalab.nibiruconnector.NibiruLogger;
import moe.alisalab.nibiruconnector.config.data.WarpPoint;

import java.util.ArrayList;
import java.util.List;

public class WarpPointsConfigManager {

    public static class WarpPointsConfig {
        @JSONField(name = "warp_points")
        public List<WarpPoint> warpPoints;
    }

    private static WarpPointsConfigManager instance;
    private WarpPointsConfig config;

    public static WarpPointsConfigManager getInstance() {
        if (instance == null) {
            instance = new WarpPointsConfigManager();
        }
        return instance;
    }

    public List<String> getWarpPointNames() {
        var names = new ArrayList<String>();
        for (var warpPoint : config.warpPoints) {
            names.add(warpPoint.name);
        }
        return names;
    }

    public WarpPoint getWarpPoint(String name) {
        for (var warpPoint : config.warpPoints) {
            if (warpPoint.name.equals(name)) {
                return warpPoint;
            }
        }
        return null;
    }

    public List<WarpPoint> getWarpPoints() {
        return config.warpPoints;
    }

    public boolean addWarpPoint(String name, String world, double x, double y, double z, float yaw, float pitch) {
        if (getWarpPoint(name) != null) {
            return false;
        }

        var warpPoint = new WarpPoint();
        warpPoint.name = name;
        warpPoint.world = world;
        warpPoint.x = x;
        warpPoint.y = y;
        warpPoint.z = z;
        warpPoint.yaw = yaw;
        warpPoint.pitch = pitch;
        config.warpPoints.add(warpPoint);
        return saveConfig();
    }

    public boolean removeWarpPoint(String name) {
        for (var warpPoint : config.warpPoints) {
            if (warpPoint.name.equals(name)) {
                config.warpPoints.remove(warpPoint);
                return saveConfig();
            }
        }
        return false;
    }

    private WarpPointsConfigManager() {
        var result = loadConfig();
        if (result) {
            NibiruLogger.info("Loaded warp points config.");
        }
        else {
            NibiruLogger.warn("Failed to load warp points config.");
        }
    }

    private boolean loadConfig() {
        var result = ConfigManager.readConfig("warp-points.json");
        if (result == null) {
            return false;
        }

        if (result == "") {
            config = new WarpPointsConfig();
            config.warpPoints = new ArrayList<>();
            return true;
        }

        config = JSON.parseObject(result, WarpPointsConfig.class);
        return true;
    }

    private boolean saveConfig() {
        var json = JSON.toJSONString(config);
        return ConfigManager.writeConfig("warp-points.json", json);
    }
}
