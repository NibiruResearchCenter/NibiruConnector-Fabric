package moe.alisalab.nibiruconnector.utils;

import moe.alisalab.nibiruconnector.exceptions.LuckpermApiException;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.util.Tristate;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public final class LuckPermsApi {
    public static LuckPerms API = LuckPermsProvider.get();

    public static User getUser(UUID uuid) throws LuckpermApiException {
        try {
            return API.getUserManager().loadUser(uuid).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new LuckpermApiException(e.getMessage());
        }
    }

    public static Tristate checkPermission(ServerPlayerEntity player, String permission) {
        return API.getPlayerAdapter(ServerPlayerEntity.class).getPermissionData(player)
                .checkPermission(permission);
    }

    public static String getUserGroup(UUID uuid) throws LuckpermApiException {
        return getUser(uuid).getPrimaryGroup();
    }

    public static Set<UUID> getAllUsers() throws LuckpermApiException {
        try {
            return API.getUserManager().getUniqueUsers().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new LuckpermApiException(e.getMessage());
        }
    }

    public static Group getGroup(String groupName) {
        return API.getGroupManager().getGroup(groupName);
    }

    public static Set<Group> getAllGroups() {
        return Set.copyOf(API.getGroupManager().getLoadedGroups()
                .stream()
                .filter(g -> !g.getName().equals("default"))
                .toList());
    }

    public static boolean isGroupExist(String group) {
        return API.getGroupManager().getGroup(group) != null;
    }

    public static void saveUser(User user) {
        API.getUserManager().saveUser(user);
    }
}