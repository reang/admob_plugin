package dev.reang.admob_plugin;

import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;

public class AdListener extends com.google.android.gms.ads.AdListener implements RewardedVideoAdListener {
    private final MethodChannel channel;

    AdListener(MethodChannel channel) {
        this.channel = channel;
    }

    @Override
    public void onAdLoaded() {
        channel.invokeMethod("onAdLoaded", argsMap());
    }

    @Override
    public void onAdOpened() {
        channel.invokeMethod("onAdOpened", argsMap());
    }

    @Override
    public void onAdFailedToLoad(int errorCode) {
        channel.invokeMethod("onAdFailedToLoad", argsMap("errorCode", errorCode));
    }

    @Override
    public void onAdClicked() {
        channel.invokeMethod("onAdClicked", argsMap());
    }

    @Override
    public void onAdClosed() {
        channel.invokeMethod("onAdClosed", argsMap());
    }

    @Override
    public void onAdImpression() {
        channel.invokeMethod("onAdImpression", argsMap());
    }

    @Override
    public void onAdLeftApplication() {
        channel.invokeMethod("onAdLeftApplication", argsMap());
    }

    @Override
    public void onRewardedVideoStarted() {
        channel.invokeMethod("onVideoStarted", argsMap());
    }

    @Override
    public void onRewardedVideoCompleted() {
        channel.invokeMethod("onVideoCompleted", argsMap());
    }

    @Override
    public void onRewarded(RewardItem item) {
        channel.invokeMethod("onRewarded", argsMap("type", item.getType(), "amount", item.getAmount()));
    }

    @Override
    public void onRewardedVideoAdLoaded() { onAdLoaded(); }

    @Override
    public void onRewardedVideoAdOpened() { onAdOpened(); }

    @Override
    public void onRewardedVideoAdLeftApplication() { onAdLeftApplication(); }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) { onAdFailedToLoad(i); }

    @Override
    public void onRewardedVideoAdClosed() { onAdClosed(); }

    private Map<String, Object> argsMap(Object... args) {
        Map<String, Object> arguments = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) arguments.put(args[i].toString(), args[i + 1]);
        return arguments;
    }
}
