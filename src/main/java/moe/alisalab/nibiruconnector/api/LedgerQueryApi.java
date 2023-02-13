package moe.alisalab.nibiruconnector.api;

import com.github.quiltservertools.ledger.utility.PlayerResult;
import com.mojang.authlib.GameProfile;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface LedgerQueryApi {
    CompletableFuture<List<PlayerResult>> queryPlayers(Set<GameProfile> profiles);

}
