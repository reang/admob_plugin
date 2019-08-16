package dev.reang.admob_plugin;

import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;

public class InterstitialAd implements MethodCallHandler {
    private final BinaryMessenger messenger;
    private final Context context;
    private final SparseArray<com.google.android.gms.ads.InterstitialAd> ads = new SparseArray<>();

    InterstitialAd(Context context, BinaryMessenger messenger) {
        this.context = context;
        this.messenger = messenger;
    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        Integer id = call.argument("id");

        if (id == null) {
            result.notImplemented();
            return;
        }

        com.google.android.gms.ads.InterstitialAd ad = ads.get(id);

        switch (call.method) {
            case "load":
                AdRequest.Builder adBuilder = new AdRequest.Builder();
                Boolean nonPersonalizedAds = call.argument("nonPersonalizedAds");
                if (nonPersonalizedAds != null && nonPersonalizedAds) {
                    Bundle extras = new Bundle();
                    extras.putString("npa", "1");
                    adBuilder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
                }
                String testDevice = String.valueOf(call.argument("testDevice"));
                if (!testDevice.isEmpty()) adBuilder.addTestDevice(testDevice);

                if (ad == null) {
                    ad = new com.google.android.gms.ads.InterstitialAd(context);
                    ad.setAdUnitId((String) call.argument("adUnitId"));
                    ad.setAdListener(new AdListener(new MethodChannel(messenger,"reang/admob_plugin/interstitial_" + id)));
                    ads.put(id, ad);
                }
                ad.loadAd(adBuilder.build());
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
                ad.setAdListener(null);
                ads.remove(id);
                result.success(Boolean.TRUE);
                break;
            default: result.notImplemented();
        }
    }
}
