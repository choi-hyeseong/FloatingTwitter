package com.comet.floatingtwitter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.comet.floatingtwitter.callback.ActivityCallback
import com.comet.floatingtwitter.model.MainViewModel

class MainFragment : Fragment() {

    var callback : ActivityCallback? = null;
    private val viewModel : MainViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        callback = context as ActivityCallback
        super.onAttach(context)
    }

    override fun onDetach() {
        callback = null;
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
        viewModel.load();
        btn.setOnClickListener { callback?.startService()}
        stop.setOnClickListener { callback?.stopService() }
        setting.setOnClickListener { callback?.switchSetting() }
        return view;
    }

}