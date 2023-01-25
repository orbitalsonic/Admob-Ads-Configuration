package com.orbitalsonic.admobadsconfigurations.ui.fragments.sample

import com.orbitalsonic.admobadsconfigurations.R
import com.orbitalsonic.admobadsconfigurations.adsconfig.callbacks.BannerCallBack
import com.orbitalsonic.admobadsconfigurations.adsconfig.enums.NativeType
import com.orbitalsonic.admobadsconfigurations.databinding.FragmentSampleBinding
import com.orbitalsonic.admobadsconfigurations.helpers.firebase.RemoteConstants
import com.orbitalsonic.admobadsconfigurations.ui.fragments.base.BaseFragment

class FragmentSample : BaseFragment<FragmentSampleBinding>(R.layout.fragment_sample) {

    override fun onViewCreatedOneTime() {
        loadAds()
    }

    override fun onViewCreatedEverytime() {}

    private fun loadAds(){
        diComponent.admobNativeAds.loadNativeAds(
            activity,
            binding.adsPlaceHolder,
            getResString(R.string.admob_native_home_ids),
            RemoteConstants.rcvNativeHome,
            diComponent.sharedPreferenceUtils.isAppPurchased,
            diComponent.internetManager.isInternetConnected,
            NativeType.LARGE,
            object : BannerCallBack {
                override fun onAdFailedToLoad(adError: String) {}
                override fun onAdLoaded() {}
                override fun onAdImpression() {}
                override fun onPreloaded() {}
                override fun onAdClicked() {}
                override fun onAdClosed() {}
                override fun onAdOpened() {}
                override fun onAdSwipeGestureClicked() {}
            }
        )
    }

    override fun navIconBackPressed() {
        onBackPressed()
    }

    override fun onBackPressed() {
        popFrom(R.id.fragmentSample)
    }
}