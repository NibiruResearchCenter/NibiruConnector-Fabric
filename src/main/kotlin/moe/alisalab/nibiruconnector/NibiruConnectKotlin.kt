package moe.alisalab.nibiruconnector

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import moe.alisalab.nibiruconnector.api.LedgerQueryApi
import kotlin.coroutines.CoroutineContext

object NibiruConnectKotlin : CoroutineScope {

    @JvmStatic
    val ledgerQueryApi: LedgerQueryApi = LedgerQueryApiImpl

    override val coroutineContext: CoroutineContext = Dispatchers.IO

}