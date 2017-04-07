package android.example.com.visualizerpreferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.widget.Toast;

/**
 * Created by bazinga on 2017/4/6.
 */

public class SettingFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener
,Preference.OnPreferenceChangeListener{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        addPreferencesFromResource(R.xml.pref_visualizer);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

        PreferenceScreen preferenceScreen = getPreferenceScreen();

        int count = preferenceScreen.getPreferenceCount();

        for(int i = 0; i < count; i++){

            Preference p = preferenceScreen.getPreference(i);

            if(!(p instanceof CheckBoxPreference)){

                String value = sharedPreferences.getString(p.getKey(),"");

                setPreferenceSummary(p,value);
            }
        }

        // 用于检测输入的合法行，PreferenceChangeListener 和 SharedPreferenceChangeListener
        // 的区别是前者在保存到 SharedPreferences 前触发用于检测输入的合法性，而后者是在保存后触发。

        Preference preference = findPreference(getString(R.string.pref_size_key));

        preference.setOnPreferenceChangeListener(this);
    }

    private void setPreferenceSummary(Preference preference, String value){

        if (preference instanceof ListPreference){

            ListPreference listPreference = (ListPreference) preference;

            int preIndex = listPreference.findIndexOfValue(value);

            if(preIndex >= 0){

                listPreference.setSummary(listPreference.getEntries()[preIndex]);

            }
        }else if(preference instanceof EditTextPreference){

            preference.setSummary(value);

        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Preference preference = findPreference(key);

        if (null != preference){

            if(!(preference instanceof  CheckBoxPreference)){

                String value = sharedPreferences.getString(preference.getKey(),"");

                setPreferenceSummary(preference , value);

            }else if(preference instanceof  EditTextPreference){

                String value = sharedPreferences.getString(preference.getKey(),"");

                setPreferenceSummary(preference , value);

            }

        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().
                registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().
                unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        Toast error = Toast.makeText(getContext(),
                "Please select a number between 0.1 and 3", Toast.LENGTH_SHORT);

        String sizeKey = getString(R.string.pref_size_key);

        if (preference.getKey().equals(sizeKey)) {

            String stringSize = ((String) (newValue)).trim();

            if (stringSize.equals("")) stringSize = "1";

            try { // 防止输入的是文本而非汉字

                float size = Float.parseFloat(stringSize);

                if (size > 3 || size <= 0) {

                    error.show();

                    return false;
                }
            } catch (NumberFormatException nfe) {

                error.show();

                return false;
            }
        }
        return true;
    }
}
