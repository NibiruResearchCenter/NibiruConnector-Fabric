package moe.alisalab.nibiruconnector.config;

import moe.alisalab.nibiruconnector.NibiruLogger;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;

public final class ConfigManager {

    public static String readConfig(String fileName) {
        try {
            var path = FabricLoader.getInstance().getConfigDir().resolve("nibiru-connector/" + fileName);
            if (Files.exists(path)) {
                var data = Files.readString(path);
                return data;
            }

            return "";
        }
        catch (Exception e) {
            NibiruLogger.warn("Failed to load config file " + fileName + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static boolean writeConfig(String fileName, String data) {
        try {
            var path = FabricLoader.getInstance().getConfigDir().resolve("nibiru-connector/" + fileName);
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            Files.writeString(path, data);
            return true;
        }
        catch (Exception e) {
            NibiruLogger.warn("Failed to save config file " + fileName + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
