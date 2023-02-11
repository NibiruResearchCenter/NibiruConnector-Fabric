package alisalab.nibiruconnector.utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public final class LuckPermsApi {
    public static LuckPerms API = LuckPermsProvider.get();

    public static CompletableFuture<User> getUser(UUID uuid) {
        return API.getUserManager().loadUser(uuid);
    }

    public static String getUserGroup(UUID uuid) throws ExecutionException, InterruptedException {
        return getUser(uuid).get().getPrimaryGroup();
    }

    public static CompletableFuture<Set<UUID>> getAllUsers() {
        return API.getUserManager().getUniqueUsers();
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