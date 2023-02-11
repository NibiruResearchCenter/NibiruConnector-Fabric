package alisalab.nibiruconnector.utils;

import alisalab.nibiruconnector.exceptions.LuckpermApiException;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;

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

    public static boolean isGroupExist(String group) {
        return API.getGroupManager().getGroup(group) != null;
    }

    public static void saveUser(User user) {
        API.getUserManager().saveUser(user);
    }
}