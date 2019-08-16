package dev.reang.admob_plugin;

import android.content.Context;
import android.os.Bundle;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;

public class RewardedAd implements MethodCallHandler {
    private final RewardedVideoAd ad;

    RewardedAd(Context context, MethodChannel channel) {
        ad = MobileAds.getRewardedVideoAdInstance(context);
        ad.setRewardedVideoAdListener(new AdListener(channel));
    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        switch (call.method) {
            case "load":
                if (!ad.isLoaded()) {
                    AdRequest.Builder adBuilder = new AdRequest.Builder();
                    Boolean nonPersonalizedAds = call.argument("nonPersonalizedAds");
                    if (nonPersonalizedAds != null && nonPersonalizedAds) {
                        Bundle extras = new Bundle();
                        extras.putString("npa", "1");
                        adBuilder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
                    }
                    String testDevice = String.valueOf(call.argument("testDevice"));
                    if (!testDevice.isEmpty()) adBuilder.addTestDevice(testDevice);

                    ad.loadAd((String) call.argument("adUnitId"), adBuilder.build());
                }
                result.success(Boolean.TRUE);
                break;
            case "show":
                if (!ad.isLoaded()) {
                    result.success(Boolean.FALSE);
                    return;
                }
                ad.show();
                result.success(Boolean.TRUE);
                break;
            case "dispose":
            default: result.notImplemented();
        }
    }
}
