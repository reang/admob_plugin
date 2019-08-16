# admob_plugin

Simple AdMob plugin for Flutter framework

*iOS: not supported yet!

# Usage
Add this to your package's pubspec.yaml file:

```dart
dependencies:
  admob_plugin:
    git:
      url: git://github.com/reang/admob_plugin.git
```

Now in your Dart code, you can use:
```dart
import 'package:admob_flutter/admob_flutter.dart' as Admob;
```

Initialize it:
```dart
void main() async {
  Admob.initialize();
  Admob.testDevice = '11081C2C32429CAE8364B0C04EBFC6F7';
  Admob.bannerUnitId = 'ca-app-pub-3940256099942544/6300978111';
  Admob.interstitialUnitId = 'ca-app-pub-3940256099942544/1033173712';
  Admob.rewardedUnitId = 'ca-app-pub-3940256099942544/5224354917';

  runApp(MyApp());
}
```

### Interstitial Ad
```dart
Admob.InterstitialAd(hashCode).load();
Admob.InterstitialAd(hashCode).listener = (event, args) => print('INTERSTITIAL => $event');
...
Admob.InterstitialAd(hashCode).show();
...
```

### Rewarded Ad
```dart
Admob.RewardedAd().load();
Admob.RewardedAd().listener = (event, args) => print('REWARDED => $event');
...
Admob.RewardedAd().show();
...
```

### Banner Ad
```dart
...
child: const Admob.BannerAd.banner(),
...
```
