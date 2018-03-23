package project.meapy.meapy;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by yassi on 23/03/2018.
 */

public class MyAppCompatActivity extends AppCompatActivity {

    protected int colorSelectedOnSettings = 0; //default value

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
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorSelectedOnSettings));
    }

    private int getColorInHex(String color) {
        int hex = 0;
        if(color.equals("bordeau")) {
            hex = getResources().getColor(R.color.bordeau);
        }
        else if(color.equals("green")) {
            hex = getResources().getColor(R.color.green);
        }
        else if(color.equals("blue")) {
            hex = getResources().getColor(R.color.blue);
        }
        return hex;
    }
}
