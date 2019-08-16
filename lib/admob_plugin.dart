import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

enum AdEvent {
  loaded,
  failedToLoad,
  clicked,
  impression,
  opened,
  leftApplication,
  closed,
  rewarded,
  started,
  completed,
}

typedef AdListener = void Function(AdEvent event, Map<String, dynamic> args);

const Map<String, AdEvent> _adEvent = <String, AdEvent>{
  'onAdLoaded': AdEvent.loaded,
  'onAdOpened': AdEvent.opened,
  'onAdFailedToLoad': AdEvent.failedToLoad,
  'onAdClicked': AdEvent.clicked,
  'onAdImpression': AdEvent.impression,
  'onAdLeftApplication': AdEvent.leftApplication,
  'onAdClosed': AdEvent.closed,
  'onRewarded': AdEvent.rewarded,
  'onVideoStarted': AdEvent.started,
  'onVideoCompleted': AdEvent.completed,
};

const String _CHANNEL_ = 'reang/admob_plugin';

Future<bool> _invokeMethod(MethodChannel channel, String method, [dynamic args]) async {
  final bool result = await channel.invokeMethod<bool>(method, args);
  return result;
}
//-----------------------------------------------------------------------------
String testDevice, bannerUnitId, interstitialUnitId, rewardedUnitId;
bool childDirected = false, nonPersonalizedAds = false;

Future<bool> initialize() async {
  return await _invokeMethod(const MethodChannel(_CHANNEL_), 'initialize', <String, dynamic>{
    'childDirected': childDirected,
  });
}
//-----------------------------------------------------------------------------
class AdSize {
  final int width, height;
  final String type;
  const AdSize._({@required this.width, @required this.height, @required this.type});
  static const AdSize BANNER = AdSize._(width: 320, height: 50, type: 'BANNER');
  static const AdSize FULL_BANNER = AdSize._(width: 468, height: 60, type: 'FULL_BANNER');
  static const AdSize LARGE_BANNER = AdSize._(width: 320, height: 100, type: 'LARGE_BANNER');
  static const AdSize LEADERBOARD = AdSize._(width: 728, height: 90, type: 'LEADERBOARD');
  static const AdSize MEDIUM_RECTANGLE = AdSize._(width: 300, height: 250, type: 'MEDIUM_RECTANGLE');
  static const AdSize SMART_BANNER = AdSize._(width: -1, height: -2, type: 'SMART_BANNER');
  Map<String, dynamic> get toMap => <String, dynamic>{'width': width, 'height': height, 'type': type};
}
//-----------------------------------------------------------------------------
class BannerAd extends StatelessWidget {
  const BannerAd._(this.size, {Key key, this.listener}) : super(key: key);
  const BannerAd.banner({this.listener}) : size = AdSize.BANNER;
  const BannerAd.fullBanner({this.listener}) : size = AdSize.FULL_BANNER;
  const BannerAd.largeBanner({this.listener}) : size = AdSize.LARGE_BANNER;
  const BannerAd.smartBanner({this.listener}) : size = AdSize.SMART_BANNER;
  const BannerAd.leaderboard({this.listener}) : size = AdSize.LEADERBOARD;
  const BannerAd.mediumRectangle({this.listener}) : size = AdSize.MEDIUM_RECTANGLE;

  final AdSize size;
  final AdListener listener;

  @override
  Widget build(BuildContext context) {
    if (Platform.isAndroid) {
      return SizedBox(
        height: size.height >= 0 ? size.height.toDouble() : double.infinity,
        width: size.width >= 0 ? size.width.toDouble() : double.infinity,
        child: AndroidView(
          key: UniqueKey(),
          viewType: '$_CHANNEL_/banner',
          creationParamsCodec: const StandardMessageCodec(),
          creationParams: <String, dynamic>{
            'adSize': size.type,
            'adUnitId': bannerUnitId,
            'testDevice': testDevice,
            'nonPersonalizedAds': nonPersonalizedAds,
          },
          onPlatformViewCreated: (int id) {
            MethodChannel('$_CHANNEL_/banner_$id').setMethodCallHandler((MethodCall call) async {
              assert(call.arguments is Map);
              final Map<String, dynamic> args = Map<String, dynamic>.from(call.arguments);
              final AdEvent event = _adEvent[call.method];
              if (listener != null) listener(event, args);
              //print('======> $event | $args <======');
            });
          },
        ),
      );
    }

    return SizedBox.shrink();
  }
}
//-----------------------------------------------------------------------------
class InterstitialAd {
  static const MethodChannel _channel = const MethodChannel('$_CHANNEL_/interstitial');
  static final Map<int, InterstitialAd> _ads = <int, InterstitialAd>{};

  AdListener listener;

  InterstitialAd._(this._id, this._adChannel) {
    _adChannel.setMethodCallHandler((MethodCall call) async {
      assert(call.arguments is Map);
      final Map<String, dynamic> args = Map<String, dynamic>.from(call.arguments);
      final AdEvent event = _adEvent[call.method];
      if (listener != null) listener(event, args);
    });

    _ads[_id] = this;
  }

  factory InterstitialAd(int id) =>
      _ads[id] ?? InterstitialAd._(id, MethodChannel('$_CHANNEL_/interstitial_$id'));

  final int _id;
  final MethodChannel _adChannel;

  void load() async {
    await _invokeMethod(_channel, 'load', <String, dynamic>{
      'id': _id,
      'adUnitId': interstitialUnitId,
      'testDevice': testDevice,
      'nonPersonalizedAds': nonPersonalizedAds,
    });
  }

  void show() async {
    if (!await _invokeMethod(_channel, 'show', <String, dynamic>{'id': _id})) {
      load();
    }
  }

  void dispose() async {
    await _invokeMethod(_channel, 'dispose', <String, dynamic>{'id': _id});
    _adChannel.setMethodCallHandler(null);
    _ads.remove(_id);
  }
}
//-----------------------------------------------------------------------------
class RewardedAd {
  static const MethodChannel _channel = const MethodChannel('$_CHANNEL_/rewarded');
  static RewardedAd _ad;

  AdListener listener;

  RewardedAd._(MethodChannel adChannel) {
    adChannel.setMethodCallHandler((MethodCall call) async {
      assert(call.arguments is Map);
      final Map<String, dynamic> args = Map<String, dynamic>.from(call.arguments);
      final AdEvent event = _adEvent[call.method];
      if (listener != null) listener(event, args);
    });
  }

  factory RewardedAd() => _ad ??= RewardedAd._(_channel);

  void load() async {
    await _invokeMethod(_channel, 'load', <String, dynamic>{
      'adUnitId': rewardedUnitId,
      'testDevice': testDevice,
      'nonPersonalizedAds': nonPersonalizedAds,
    });
  }

  void show() async => await _invokeMethod(_channel, 'show');
}
