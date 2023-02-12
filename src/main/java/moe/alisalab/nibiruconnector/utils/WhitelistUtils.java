package moe.alisalab.nibiruconnector.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import com.mojang.authlib.GameProfile;

import java.io.*;
import java.util.List;
import java.util.UUID;

public final class WhitelistUtils {

    public static List<GameProfile> getWhitelistProfiles(File whitelistFile) {
        try {
            var reader = new BufferedReader(new FileReader(whitelistFile));

            var sb = new StringBuilder();

            String st;
            while ((st = reader.readLine()) != null) {
                var formatted = st.replaceAll("[\\n\\t ]", "");
                sb.append(formatted);
            }

            class WhitelistEntry {
                @JSONField(name = "name")
                public String name;
                @JSONField(name = "uuid")
                public String uuid;
            }

            var entries = JSON.parseArray(sb.toString(), WhitelistEntry.class);

            return entries.stream()
                    .map(e -> new GameProfile(UUID.fromString(e.uuid), e.name))
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
