package project.meapy.meapy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;

import project.meapy.meapy.activities.MyAppCompatActivity;
import project.meapy.meapy.bean.User;

/**
 * Created by yassi on 14/04/2018.
 */

public class MyAccountActivity extends MyAppCompatActivity implements RewardedVideoAdListener{

    private Button reloadMeapsMyAccount;
    private RewardedVideoAd rewardedVideoAd;
    private TextView myMeapsPoints;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_account);

        MobileAds.initialize(this,PostDetailsActivity.SAMPLE_APMOB_ID);

        reloadMeapsMyAccount = (Button)findViewById(R.id.reloadMeapsMyAccount);
        myMeapsPoints = (TextView)findViewById(R.id.myMeapsPoints);

        myMeapsPoints.setText(String.valueOf(MyApplication.getUser().getCoins()));

        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(this);

        loadVideoAd();

        reloadMeapsMyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rewardedVideoAd.isLoaded()) {
                    rewardedVideoAd.show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_my_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null ;
        switch(item.getItemId()) {
            case R.id.disconnect:
                intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                FirebaseAuth.getInstance().signOut();
                break;
        }
        if(intent != null) {
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
        rewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        rewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        rewardedVideoAd.destroy(this);
        super.onDestroy();
    }

    private void loadVideoAd() {
        if(!rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.loadAd(PostDetailsActivity.SAMPLE_APMOB_UNIT_ID,new AdRequest.Builder().build());
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadVideoAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        MyApplication.getUser().setCoins(MyApplication.getUser().getCoins()+ User.DEFAULT_NUMBER_COINS);
        myMeapsPoints.setText(String.valueOf(MyApplication.getUser().getCoins()));
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }
}
