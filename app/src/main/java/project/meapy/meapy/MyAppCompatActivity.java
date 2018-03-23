package project.meapy.meapy;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by yassi on 23/03/2018.
 */

public class MyAppCompatActivity extends AppCompatActivity {

    protected int colorSelectedOnSettings; //color (int color)
    protected int colorId; //color id (int id)

    public MyAppCompatActivity() {
        super();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String colorSelected = preferences.getString(SettingsActivity.KEY_PREFERENCE_COLOR,"green");
        colorSelectedOnSettings = getColorInHex(colorSelected);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(colorSelectedOnSettings));
        }
    }

    protected ColorDrawable getColorDrawable(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String colorSelected = preferences.getString(SettingsActivity.KEY_PREFERENCE_COLOR,"green");
        colorSelectedOnSettings = getColorInHex(colorSelected);
        return new ColorDrawable(colorSelectedOnSettings);
    }

    private int getColorInHex(String colorName) {
        int color = 0;
        if(colorName.equals("bordeau")) {
            color = getResources().getColor(R.color.bordeau);
            colorId = R.color.bordeau;
        }
        else if(colorName.equals("green")) {
            color = getResources().getColor(R.color.green);
            colorId = R.color.green;
        }
        else if(colorName.equals("blue")) {
            color = getResources().getColor(R.color.blue);
            colorId = R.color.blue;
        }
        return color;
    }
}
