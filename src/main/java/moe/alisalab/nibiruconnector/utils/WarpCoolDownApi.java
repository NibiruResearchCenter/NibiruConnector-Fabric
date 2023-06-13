package moe.alisalab.nibiruconnector.utils;

import java.util.HashMap;
import java.util.UUID;

public class WarpCoolDownApi {

    private final HashMap<UUID, Long> coolDown = new HashMap<>();

    private final int seconds;

    public WarpCoolDownApi(int seconds) {
        this.seconds = seconds;
    }

    public boolean isCoolDown(UUID uuid) {
        if (coolDown.containsKey(uuid)) {
            if (coolDown.get(uuid) > System.currentTimeMillis()) {
                return true;
            } else {
                coolDown.remove(uuid);
                return false;
            }
        } else {
            return false;
        }
    }

    public int getCoolDownTime(UUID uuid) {
        if (coolDown.containsKey(uuid)) {
            return (int) ((coolDown.get(uuid) - System.currentTimeMillis()) / 1000L);
        } else {
            return 0;
        }
    }

    public void setCoolDown(UUID uuid) {
        coolDown.put(uuid, System.currentTimeMillis() + (seconds * 1000L));
    }
}
