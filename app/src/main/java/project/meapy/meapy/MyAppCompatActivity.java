package project.meapy.meapy;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yassi on 23/03/2018.
 */

public class MyAppCompatActivity extends AppCompatActivity {

    protected int colorSelectedOnSettings; //color (int color)
    protected int colorId; //color id (int id)
    private Map<String,Integer> colorMessages =  new HashMap<>();

    public MyAppCompatActivity() {
        super();
    }
    private void configureToolbar(){
        if(toolbarToConfig()) {
            android.support.v7.app.ActionBar actionbar = getSupportActionBar();
            if (actionbar != null) {
                actionbar.setDisplayHomeAsUpEnabled(true);
                actionbar.setHomeButtonEnabled(true);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean toolbarToConfig(){
        return true;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();

        ColorDrawable colorDrawable = getColorDrawable();
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setBackgroundDrawable(colorDrawable);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        MyApplication.addActivity(this);
    }
    @Override
    protected void onPause(){
        super.onPause();
        MyApplication.removeActivity();
    }

    public int getColorSelectedOnSettings() {
        return this.colorSelectedOnSettings;
    }

    public int getColorId() {
        return this.colorId;
    }

    public Map<String, Integer> getColorMessage() {
        return this.colorMessages;
    }

    protected ColorDrawable getColorDrawable(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String colorSelected = preferences.getString(SettingsActivity.KEY_PREFERENCE_COLOR,"green");
        colorSelectedOnSettings = getColorAssociated(colorSelected);
        return new ColorDrawable(colorSelectedOnSettings);
    }

    private int getColorAssociated(String colorName) {
        int color = 0;
        if(colorName.equals("bordeau")) {
            color = getResources().getColor(R.color.bordeau);
            colorId = R.color.bordeau;
//            colorMessages.put("Sent",R.color.messageSentBordeau);
            colorMessages.put("Sent",R.color.messageSentBordeau);
        }
        else if(colorName.equals("green")) {
            color = getResources().getColor(R.color.green);
            colorId = R.color.green;
//            colorMessages.put("Sent",R.color.messageSentGreen);
            colorMessages.put("Sent",R.color.messageSentGreen);
        }
        else if(colorName.equals("blue")) {
            color = getResources().getColor(R.color.blue);
            colorId = R.color.blue;
//            colorMessages.put("Sent",R.color.messageSentBlue);
            colorMessages.put("Sent",R.color.messageSentBlue);
        }
        return color;
    }
}
