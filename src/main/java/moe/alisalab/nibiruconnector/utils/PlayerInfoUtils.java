package moe.alisalab.nibiruconnector.utils;

import com.mojang.authlib.GameProfile;
import net.minecraft.util.Uuids;

public final class PlayerInfoUtils {

    public static GameProfile getGameProfile(String name) {
        return new GameProfile(Uuids.getOfflinePlayerUuid(name), name);
    }
}
