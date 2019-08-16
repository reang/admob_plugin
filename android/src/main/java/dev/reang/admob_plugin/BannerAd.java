package dev.reang.admob_plugin;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.HashMap;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class BannerAd extends PlatformViewFactory {
    private final BinaryMessenger messenger;

    BannerAd(BinaryMessenger messenger) {
        super(StandardMessageCodec.INSTANCE);
        this.messenger = messenger;
    }

    @Override
    public PlatformView create(Context context, int id, Object args) {
        return new BannerView(context, id, (HashMap) args);
    }

    private class BannerView implements PlatformView {
        private final MethodChannel channel;
        private final AdView adView;

        BannerView(Context context, int id, HashMap args) {
            channel = new MethodChannel(messenger,"reang/admob_plugin/banner_"+id);
            channel.setMethodCallHandler(new MethodChannel.MethodCallHandler() {
                @Override
                public void onMethodCall(MethodCall call, MethodChannel.Result result) {
                    if (call.method.equals("dispose")) dispose();
                    else result.notImplemented();
                }
            });

            AdRequest.Builder adBuilder = new AdRequest.Builder();
            Boolean nonPersonalizedAds = (Boolean)args.get("nonPersonalizedAds");
            if (nonPersonalizedAds != null && nonPersonalizedAds) {
                Bundle extras = new Bundle();
                extras.putString("npa", "1");
                adBuilder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
            }
            String testDevice = String.valueOf(args.get("testDevice"));
            if (!testDevice.isEmpty()) adBuilder.addTestDevice(testDevice);

            adView = new AdView(context);
            adView.setAdSize(getAdSize(String.valueOf(args.get("adSize"))));
            adView.setAdUnitId(String.valueOf(args.get("adUnitId")));
            adView.setAdListener(new AdListener(channel));

            adView.loadAd(adBuilder.build());
        }

        private AdSize getAdSize(String adType) {
            switch (adType) {
                case "FULL_BANNER": return AdSize.FULL_BANNER;
                case "LARGE_BANNER": return AdSize.LARGE_BANNER;
                case "LEADERBOARD": return AdSize.LEADERBOARD;
                case "MEDIUM_RECTANGLE": return AdSize.MEDIUM_RECTANGLE;
                case "SMART_BANNER": return AdSize.SMART_BANNER;
                default: return AdSize.BANNER;
            }
        }

        @Override
        public View getView() {
            return adView;
        }

        @Override
        public void dispose() {
            adView.setAdListener(null);
            adView.setVisibility(View.GONE);
            adView.destroy();
            channel.setMethodCallHandler(null);
        }
    }

}
