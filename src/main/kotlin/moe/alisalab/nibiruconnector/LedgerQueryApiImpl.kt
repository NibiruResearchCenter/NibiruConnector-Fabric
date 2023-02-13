package moe.alisalab.nibiruconnector

import com.github.quiltservertools.ledger.Ledger
import com.github.quiltservertools.ledger.database.DatabaseManager
import com.github.quiltservertools.ledger.utility.PlayerResult
import com.mojang.authlib.GameProfile
import kotlinx.coroutines.future.future
import moe.alisalab.nibiruconnector.api.LedgerQueryApi
import java.util.concurrent.CompletableFuture

object LedgerQueryApiImpl : LedgerQueryApi {

    override fun queryPlayers(profiles: MutableSet<GameProfile>): CompletableFuture<List<PlayerResult>> {
        return Ledger.future { DatabaseManager.searchPlayers(profiles) }
    }

}