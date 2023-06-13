package moe.alisalab.nibiruconnector.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import moe.alisalab.nibiruconnector.NibiruLogger;
import moe.alisalab.nibiruconnector.config.data.WarpPoint;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
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
        try {
            var path = FabricLoader.getInstance().getConfigDir().resolve("nibiru-connector/warp-points.json");
            if (Files.exists(path)) {
                var json = Files.readString(path);
                config = JSON.parseObject(json, WarpPointsConfig.class);
            }
            else {
                config = new WarpPointsConfig();
                config.warpPoints = new ArrayList<>();
            }

            return true;
        }
        catch (Exception e) {
            NibiruLogger.warn("Failed to load warp points config: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean saveConfig() {
        try {
            var path = FabricLoader.getInstance().getConfigDir().resolve("nibiru-connector/warp-points.json");
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            var json = JSON.toJSONString(config);
            Files.writeString(path, json);
            return true;
        }
        catch (Exception e) {
            NibiruLogger.warn("Failed to save warp points config: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
