package com.mopub.mobileads;

import static com.mopub.mobileads.MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR;
import static com.mopub.mobileads.MoPubErrorCode.NETWORK_NO_FILL;

import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/*
 * Compatible with version 4.0.30 of the Google Play Services SDK.
 */
public class GooglePlayServicesInterstitial extends CustomEventInterstitial {
    /*
     * These keys are intended for MoPub internal use. Do not modify.
     */
    private static final String AD_UNIT_ID_KEY = "adUnitID";
    private static final String LOCATION_KEY = "location";

    private CustomEventInterstitialListener mInterstitialListener;
    private InterstitialAd mGoogleInterstitialAd;

    @Override
    protected void loadInterstitial(
            Context context,
            CustomEventInterstitialListener customEventInterstitialListener,
            Map<String, Object> localExtras,
            Map<String, String> serverExtras) {
        mInterstitialListener = customEventInterstitialListener;

        String adUnitId;

        if (extrasAreValid(serverExtras)) {
            adUnitId = serverExtras.get(AD_UNIT_ID_KEY);
        } else {
            mInterstitialListener.onInterstitialFailed(ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mGoogleInterstitialAd = new InterstitialAd(context);
        mGoogleInterstitialAd.setAdListener(new InterstitialAdListener());
        mGoogleInterstitialAd.setAdUnitId(adUnitId);

        AdRequest adRequest = new AdRequest.Builder().build();

        mGoogleInterstitialAd.loadAd(adRequest);
    }

    @Override
    protected void showInterstitial() {
        if (mGoogleInterstitialAd.isLoaded()) {
            mGoogleInterstitialAd.show();
        } else {
            Log.d("MoPub", "Tried to show a Google Play Services interstitial ad before it finished loading. Please try again.");
        }
    }

    @Override
    protected void onInvalidate() {
        if (mGoogleInterstitialAd != null) {
            mGoogleInterstitialAd.setAdListener(null);
        }
    }

    private boolean extrasAreValid(Map<String, String> serverExtras) {
        return serverExtras.containsKey(AD_UNIT_ID_KEY);
    }

    private class InterstitialAdListener extends AdListener {
        /*
    	 * Google Play Services AdListener implementation
    	 */
        @Override
        public void onAdClosed() {
            Log.d("MoPub", "Google Play Services interstitial ad dismissed.");
            if (mInterstitialListener != null) {
                mInterstitialListener.onInterstitialDismissed();
            }
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            Log.d("MoPub", "Google Play Services interstitial ad failed to load.");
            if (mInterstitialListener != null) {
                mInterstitialListener.onInterstitialFailed(NETWORK_NO_FILL);
            }
        }

        @Override
        public void onAdLeftApplication() {
            Log.d("MoPub", "Google Play Services interstitial ad clicked.");
            if (mInterstitialListener != null) {
                mInterstitialListener.onInterstitialClicked();
            }
        }

        @Override
        public void onAdLoaded() {
            Log.d("MoPub", "Google Play Services interstitial ad loaded successfully.");
            if (mInterstitialListener != null) {
                mInterstitialListener.onInterstitialLoaded();
            }
        }

        @Override
        public void onAdOpened() {
            Log.d("MoPub", "Showing Google Play Services interstitial ad.");
            if (mInterstitialListener != null) {
                mInterstitialListener.onInterstitialShown();
            }
        }
    }

    @Deprecated // for testing
    InterstitialAd getGoogleInterstitialAd() {
        return mGoogleInterstitialAd;
    }

    @Override
    protected View showInterstitialView(ViewGroup holder, AdViewController adViewController) {
        // TODO Auto-generated method stub
        return null;
    }


}
