package com.comet.floatingtwitter

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import com.comet.floatingtwitter.util.PreferenceUtil

class SettingFragment : PreferenceFragmentCompat() {

    private var preference: PreferenceUtil = PreferenceUtil.INSTANCE
    private lateinit var mention : EditTextPreference
    private lateinit var dm : EditTextPreference
    private lateinit var twin : EditTextPreference
    private lateinit var seek : SeekBarPreference
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    private fun load() {
        val token = preference?.getString("token")
        mention = findPreference("mention_color")!!
        dm = findPreference("dm_color")!!
        twin = findPreference("twin_color")!!
        seek = findPreference("icon_size")!!
        if (!token.isNullOrEmpty())
            findPreference<Preference>("oauth_field")?.summary =
                resources.getString(R.string.oauth_set)
        if (mention.text.isNullOrEmpty())
            mention.text = "#FF0000"
        if (dm.text.isNullOrEmpty())
            dm.text = "#87CEEB"
        if (twin.text.isNullOrEmpty())
            twin.text = "#FFA500"
    }

    override fun onStart() {
        super.onStart()
        load() //제발 되라..

    }

    override fun onStop() {
        super.onStop()
        preference?.apply {
            putString("mention_color", mention.text)
            putString("dm_color", dm.text)
            putString("twin_color", twin.text)
            putInt("icon_size", seek.value)
        }
    }
}