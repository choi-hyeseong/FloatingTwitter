package com.comet.floatingtwitter

import android.os.Bundle
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.comet.floatingtwitter.util.PreferenceUtil

class SettingFragment : PreferenceFragmentCompat(){

    private var preference : PreferenceUtil? = PreferenceUtil.INSTANCE;

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences);

    }

    private fun load() {
        // TODO 로드시 enable등등 설정하기
        val token = preference?.getString("token");
        if (!token.isNullOrEmpty()) {
            findPreference<Preference>("oauth_field")?.summary = resources.getString(R.string.oauth_set);
        }

    }

    override fun onStart() {
        super.onStart()
        load() //제발 되라..
    }
}