package project.meapy.meapy;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

/**
 * Created by yassi on 22/03/2018.
 */

public class SettingsActivity extends MyAppCompatActivity {

    public static final String KEY_PREFERENCE_COLOR = "themeColor";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,new Fragment()).commit();

    }

    public static class Fragment extends PreferenceFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.activity_settings);
        }
    }
}
