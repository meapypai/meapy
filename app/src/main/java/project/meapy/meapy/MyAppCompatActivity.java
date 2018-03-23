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

    private int colorSelectedOnSettings = 0; //default value

    public MyAppCompatActivity() {
        super();
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String colorSelected = preferences.getString(SettingsActivity.KEY_PREFERENCE_COLOR,"Green");
        Toast.makeText(this,colorSelected,Toast.LENGTH_SHORT).show();
        colorSelectedOnSettings = getColorInHex(colorSelected);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorSelectedOnSettings));
    }

    public int  getColorSelectedOnSettings() {
        return this.colorSelectedOnSettings;
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
