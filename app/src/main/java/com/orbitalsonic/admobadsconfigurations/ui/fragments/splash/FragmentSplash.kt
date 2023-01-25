package com.orbitalsonic.admobadsconfigurations.ui.fragments.splash

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.orbitalsonic.admobadsconfigurations.R
import com.orbitalsonic.admobadsconfigurations.adsconfig.callbacks.InterstitialOnLoadCallBack
import com.orbitalsonic.admobadsconfigurations.adsconfig.callbacks.InterstitialOnShowCallBack
import com.orbitalsonic.admobadsconfigurations.databinding.FragmentSplashBinding
import com.orbitalsonic.admobadsconfigurations.helpers.firebase.RemoteConstants.rcvInterSplash
import com.orbitalsonic.admobadsconfigurations.ui.activities.SplashActivity
import com.orbitalsonic.admobadsconfigurations.ui.fragments.base.BaseFragment

class FragmentSplash : BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash) {

    private val mHandler = Handler(Looper.getMainLooper())
    private val adsRunner = Runnable { checkAdvertisement() }
    private var isInterstitialLoadOrFailed = false
    private var mCounter: Int = 0

    override fun onViewCreatedOneTime() {
        fetchRemoteConfiguration()
    }

    override fun onViewCreatedEverytime() {}


    private fun fetchRemoteConfiguration() {
        diComponent.remoteConfiguration.checkRemoteConfig { fetchSuccessfully ->
            if (isAdded){
                if (fetchSuccessfully) {
                    mCounter = 0
                    loadAds()
                }else{
                    mHandler.removeCallbacks { adsRunner }
                    moveNext(2000)
                }
            }
        }
    }

    private fun loadAds() {
        if (isAdded) {
            Log.d("AdsInformation", "Call Open App Ad")
            diComponent.admobOpenApp.fetchAd()
            when (rcvInterSplash) {
                0 -> {
                    isInterstitialLoadOrFailed = true
                }
                1 -> {
                    Log.d("AdsInformation", "Call Admob Splash Interstitial")
                    diComponent.admobInterstitialAds.loadInterstitialAd(
                        activity,
                        getResString(R.string.admob_interstitial_splash_ids),
                        rcvInterSplash,
                        diComponent.sharedPreferenceUtils.isAppPurchased,
                        diComponent.internetManager.isInternetConnected,
                        object : InterstitialOnLoadCallBack {
                            override fun onAdFailedToLoad(adError: String) {
                                isInterstitialLoadOrFailed = true
                            }

                            override fun onAdLoaded() {
                                isInterstitialLoadOrFailed = true
                            }

                            override fun onPreloaded() {
                                isInterstitialLoadOrFailed = true
                            }

                        })
                }
                else -> {
                    isInterstitialLoadOrFailed = true
                }
            }
        }


    }

    private fun checkAdvertisement() {
        if (mCounter < 12) {
            try {
                mCounter++
                if (isInterstitialLoadOrFailed) {
                    mHandler.removeCallbacks { adsRunner }
                    moveNext()
                }else{
                    mHandler.removeCallbacks { adsRunner }
                    mHandler.postDelayed(
                        adsRunner,
                        (1000)
                    )
                }

            } catch (e: Exception) {
                Log.e("AdsInformation","${e.message}")
            }

        } else {
            isInterstitialLoadOrFailed = true
            mHandler.removeCallbacks { adsRunner }
            moveNext()
        }

    }

    private fun  moveNext(timeMili:Long = 500) {
        if (isAdded){
            withDelay(timeMili) {
                lifecycleScope.launchWhenResumed {
                    (activity as SplashActivity).nextActivity()
                    diComponent.admobInterstitialAds.showInterstitialAd(activity,object : InterstitialOnShowCallBack {
                        override fun onAdDismissedFullScreenContent() {}
                        override fun onAdFailedToShowFullScreenContent() {}
                        override fun onAdShowedFullScreenContent() {}
                        override fun onAdImpression() {}

                    })
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mHandler.removeCallbacks(adsRunner)
    }

    override fun onResume() {
        super.onResume()
        mHandler.post(adsRunner)
    }

    override fun navIconBackPressed() {}

    override fun onBackPressed() {}

}