package com.dcmoote.inkwell.util

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Wraps the Google Play Billing Library. Exposes a single isPro StateFlow that the rest
// of the app observes to gate Pro features without touching billing logic directly.
class BillingManager(context: Context) {

    companion object {
        const val PRODUCT_ID = "daily_creative_pro"
    }

    private val _isPro = MutableStateFlow(false)
    val isPro: StateFlow<Boolean> = _isPro.asStateFlow()

    private val billingClient = BillingClient.newBuilder(context.applicationContext)
        .setListener { result, purchases ->
            // Called when a purchase completes while the billing flow is open.
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                purchases?.forEach { handlePurchase(it) }
            }
        }
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
        )
        .build()

    init {
        connect()
    }

    private fun connect() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Immediately check for any existing purchases so isPro is accurate on launch.
                    queryPurchases()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Reconnect on next user action
            }
        })
    }

    // Queries Play for current purchase state and updates isPro accordingly.
    // Also acknowledges any unacknowledged purchases — Play requires this within 3 days
    // or the purchase is automatically refunded.
    fun queryPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        billingClient.queryPurchasesAsync(params) { result, purchases ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                _isPro.value = purchases.any {
                    it.products.contains(PRODUCT_ID) &&
                        it.purchaseState == Purchase.PurchaseState.PURCHASED
                }
                purchases
                    .filter { it.purchaseState == Purchase.PurchaseState.PURCHASED && !it.isAcknowledged }
                    .forEach { acknowledgePurchase(it) }
            }
        }
    }

    // Opens the Play Store purchase sheet for the Pro product.
    fun launchPurchaseFlow(activity: Activity) {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )
        billingClient.queryProductDetailsAsync(
            QueryProductDetailsParams.newBuilder().setProductList(productList).build()
        ) { result, productDetailsList ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK &&
                productDetailsList.isNotEmpty()
            ) {
                val billingParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(
                        listOf(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetailsList.first())
                                .build()
                        )
                    )
                    .build()
                billingClient.launchBillingFlow(activity, billingParams)
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.products.contains(PRODUCT_ID) &&
            purchase.purchaseState == Purchase.PurchaseState.PURCHASED
        ) {
            _isPro.value = true
            if (!purchase.isAcknowledged) acknowledgePurchase(purchase)
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { }
    }
}
