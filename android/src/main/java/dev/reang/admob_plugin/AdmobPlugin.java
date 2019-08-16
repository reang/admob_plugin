package dev.reang.admob_plugin;

import android.content.Context;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import static com.google.android.gms.ads.RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE;

public class AdmobPlugin implements MethodCallHandler {
    private final Context context;

    public static void registerWith(Registrar registrar) {
        if (registrar.activity() == null) return;

        final MethodChannel channel1 = new MethodChannel(registrar.messenger(), "reang/admob_plugin");
        channel1.setMethodCallHandler(new AdmobPlugin(registrar.activeContext()));

        final MethodChannel channel2 = new MethodChannel(registrar.messenger(),"reang/admob_plugin/interstitial");
        channel2.setMethodCallHandler(new InterstitialAd(registrar.activeContext(), registrar.messenger()));

        final MethodChannel channel3 = new MethodChannel(registrar.messenger(),"reang/admob_plugin/rewarded");
        channel3.setMethodCallHandler(new RewardedAd(registrar.activeContext(), channel3));

        registrar.platformViewRegistry().registerViewFactory("reang/admob_plugin/banner", new BannerAd(registrar.messenger()));
    }

    private AdmobPlugin(Context context) {
        this.context = context;
    }

    @Override
    public void onMethodCall(final MethodCall call, Result result) {
        if (call.method.equals("initialize")) {
            MobileAds.initialize(context, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                    RequestConfiguration.Builder requestBuilder = MobileAds.getRequestConfiguration().toBuilder();

                    Boolean childDirected = call.argument("childDirected");
                    if (childDirected != null && childDirected) {
                        requestBuilder.setTagForChildDirectedTreatment(TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE);
                    }

                    //https://support.google.com/admob/answer/7562142
                    //requestBuilder.setMaxAdContentRating(MAX_AD_CONTENT_RATING_T);
                    MobileAds.setRequestConfiguration(requestBuilder.build());
                }
            });
            result.success(Boolean.TRUE);
        } else {
            result.notImplemented();
        }
    }
}
