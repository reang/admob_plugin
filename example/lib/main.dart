import 'package:flutter/material.dart';

import 'package:admob_plugin/admob_plugin.dart' as Admob;

void main() {
  Admob.initialize();
  Admob.testDevice = '11081C2C32429CAE8364B0C04EBFC6F7';
  Admob.bannerUnitId = 'ca-app-pub-3940256099942544/6300978111';
  Admob.interstitialUnitId = 'ca-app-pub-3940256099942544/1033173712';
  Admob.rewardedUnitId = 'ca-app-pub-3940256099942544/5224354917';
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();

    Admob.InterstitialAd(hashCode).load();
    Admob.InterstitialAd(hashCode).listener =
        (event, args) => print('INTERSTITIAL => $event');

    Admob.RewardedAd().load();
    Admob.RewardedAd().listener = (event, args) => print('REWARDED => $event');
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('AdMob Plugin Example'),
        ),
        body: Column(
          children: <Widget>[
            Expanded(
              child: Center(
                child: Text('Hello World!'),
              ),
            ),
            Expanded(
              child: Column(
                children: <Widget>[
                  RaisedButton(
                    onPressed: () => Admob.InterstitialAd(hashCode).show(),
                    child: const Text('Show Interstitial Ad'),
                  ),
                  RaisedButton(
                    onPressed: () => Admob.RewardedAd().show(),
                    child: const Text('Show Rewarded Ad'),
                  ),
                  RaisedButton(
                    onPressed: () => Admob.RewardedAd().load(),
                    child: const Text('Load Rewarded Ad'),
                  ),
                ],
              ),
            ),
            const Admob.BannerAd.banner()
          ],
        ),
      ),
    );
  }

  @override
  void dispose() {
    Admob.InterstitialAd(hashCode).dispose();
    super.dispose();
  }
}
