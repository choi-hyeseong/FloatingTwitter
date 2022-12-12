package com.comet.floatingtwitter

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.comet.floatingtwitter.callback.ActivityCallback
import com.comet.floatingtwitter.model.Settings
import com.comet.floatingtwitter.util.PreferenceUtil

class MainFragment : Fragment() {

    private var callback: ActivityCallback? = null
    private val preference: PreferenceUtil = PreferenceUtil.INSTANCE

    override fun onAttach(context: Context) {
        callback = context as ActivityCallback
        super.onAttach(context)
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //load data
        val view = inflater.inflate(R.layout.main_layout, container, false)
        val btn = view.findViewById<Button>(R.id.start)
        val stop = view.findViewById<Button>(R.id.stop)
        val setting = view.findViewById<Button>(R.id.settings)
        btn.setOnClickListener { start() }
        stop.setOnClickListener { callback?.stopService() }
        setting.setOnClickListener { callback?.switchSetting() }
        return view
    }

    private fun start() {
        val token = preference.getString("token")
        val refresh = preference.getString("refresh")
        val size = preference.getInt("icon_size")
        val mention = parseColor(preference.getString("mention_color"))
        val dm = parseColor(preference.getString("dm_color"))
        val twin = parseColor(preference.getString("twin_color"))
        if (token.isNullOrEmpty())
            Toast.makeText(context, "토큰이 지정되어 있지 않습니다. 설정 -> OAuth 토큰 설정하기를 진행해주세요.", Toast.LENGTH_LONG).show()
        else if (size <= 0)
            Toast.makeText(context, "아이콘 크기가 0이하로 지정되어 있습니다. 다시 설정해주세요.", Toast.LENGTH_LONG).show()
        else {
            //위에서 null check
            val setting = Settings(token, refresh, size, mention, dm, twin)
            callback?.startService(setting)
        }
    }

    private fun parseColor(str: String?): Int {
        return try {
            Color.parseColor(str)
        } catch (e: IllegalArgumentException) {
            Color.WHITE
        }
    }

}