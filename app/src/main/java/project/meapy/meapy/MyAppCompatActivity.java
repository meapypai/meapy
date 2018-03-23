package project.meapy.meapy;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by yassi on 23/03/2018.
 */

public class MyAppCompatActivity extends AppCompatActivity {

    public MyAppCompatActivity() {
        super();
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String colorSelected = preferences.getString(SettingsActivity.KEY_PREFERENCE_KEY,"Bordeau");

        String color = getColorInHex(colorSelected);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(color)));
    }

    private String getColorInHex(String color) {
        String hex= "";
        if(color.equals("bordeau")) {
            hex = "#83182A";
        }
        else if(color.equals("green")) {
            hex = "#57b99e";
        }
        else if(color.equals("blue")) {
            hex = "25BBE9";
        }
        return hex;
    }
}
