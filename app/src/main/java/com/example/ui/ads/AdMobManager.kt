package com.example.ui.ads

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdEventCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

object AdMobManager {
    private const val TAG = "AdMobManager"

    // Production Google AdMob Configuration
    const val ADMOB_APP_ID = "ca-app-pub-6552361512276767~9927979296"
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-6552361512276767/9365208253"

    // Prevent repeated or excessive interstitial ads (minimum 90 seconds between ads)
    private const val INTERSTITIAL_COOLDOWN_MS = 90_000L

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val initializeStarted = AtomicBoolean(false)

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    @Volatile
    private var interstitialAd: InterstitialAd? = null

    @Volatile
    private var isInterstitialLoading = false

    @Volatile
    private var lastInterstitialShownTimeMs = 0L

    fun initialize(context: Context) {
        if (!initializeStarted.compareAndSet(false, true)) return

        val appContext = context.applicationContext
        scope.launch {
            try {
                val config = InitializationConfig.Builder(ADMOB_APP_ID).build()
                MobileAds.initialize(appContext, config) {
                    _isInitialized.value = true
                    loadInterstitialAd()
                }
            } catch (t: Throwable) {
                Log.w(TAG, "GMA Next-Gen initialization failed safely: ${t.message}")
            }
        }
    }

    fun loadInterstitialAd() {
        if (!_isInitialized.value || interstitialAd != null || isInterstitialLoading) return
        isInterstitialLoading = true

        try {
            val request = AdRequest.Builder(INTERSTITIAL_AD_UNIT_ID).build()
            InterstitialAd.load(
                request,
                object : AdLoadCallback<InterstitialAd> {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        isInterstitialLoading = false
                        interstitialAd = ad
                        ad.adEventCallback = object : InterstitialAdEventCallback {
                            override fun onAdDismissedFullScreenContent() {
                                interstitialAd = null
                                loadInterstitialAd()
                            }

                            override fun onAdFailedToShowFullScreenContent(
                                fullScreenContentError: FullScreenContentError
                            ) {
                                interstitialAd = null
                                loadInterstitialAd()
                            }
                        }
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        isInterstitialLoading = false
                        interstitialAd = null
                        Log.d(TAG, "Interstitial failed to load: ${adError.message}")
                    }
                }
            )
        } catch (t: Throwable) {
            isInterstitialLoading = false
            interstitialAd = null
            Log.w(TAG, "Error requesting interstitial ad: ${t.message}")
        }
    }

    fun showInterstitialIfReady(activity: Activity) {
        if (!_isInitialized.value || activity.isFinishing || activity.isDestroyed) return

        val ad = interstitialAd ?: run {
            loadInterstitialAd()
            return
        }

        val now = System.currentTimeMillis()
        if (now - lastInterstitialShownTimeMs < INTERSTITIAL_COOLDOWN_MS) {
            return
        }

        try {
            interstitialAd = null
            lastInterstitialShownTimeMs = now
            ad.show(activity)
        } catch (t: Throwable) {
            Log.w(TAG, "Failed to show interstitial ad: ${t.message}")
            interstitialAd = null
        }
    }
}

tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
