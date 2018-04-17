package project.meapy.meapy;

import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import project.meapy.meapy.bean.User;

public class MyAppCompatWithRewardedVideo extends MyAppCompatActivity implements RewardedVideoAdListener {

    public static final String SAMPLE_APMOB_ID = "ca-app-pub-3940256099942544~3347511713";
    public static final String SAMPLE_APMOB_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";

    protected RewardedVideoAd rewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize ads
        MobileAds.initialize(this,SAMPLE_APMOB_ID);

        //video will be displayed
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(this);

        //to load the video
        loadVideoAd();
    }

    @Override
    public void onRewardedVideoAdLoaded() {}

    @Override
    public void onRewardedVideoAdOpened() {}

    @Override
    public void onRewardedVideoStarted() {}

    @Override
    public void onRewardedVideoAdClosed() {
        loadVideoAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        MyApplication.getUser().setCoins(MyApplication.getUser().getCoins()+ User.DEFAULT_NUMBER_COINS); //set user coins
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {}

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {}

    /**
     * load the video ad
     */
    protected void loadVideoAd() {
        if(!rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.loadAd(SAMPLE_APMOB_UNIT_ID,new AdRequest.Builder().build());
        }
    }
}
