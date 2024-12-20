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
import androidx.fragment.app.viewModels
import com.comet.floatingtwitter.callback.ActivityCallback
import com.comet.floatingtwitter.databinding.MainLayoutBinding
import com.comet.floatingtwitter.overlay.type.ServiceRequirement
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var callback: ActivityCallback? = null
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val bind = MainLayoutBinding.inflate(layoutInflater)
        initListener(bind)
        initObserver()
        return bind.root
    }

    private fun initObserver() {
        mainViewModel.serviceRequirementLiveData.observe(viewLifecycleOwner) {
            it.getContent()?.let { requirement ->
                when (requirement) {
                    ServiceRequirement.TOKEN -> Toast.makeText(requireContext(), R.string.oauth_not_set, Toast.LENGTH_LONG).show()
                    ServiceRequirement.SETTING -> Toast.makeText(requireContext(), R.string.setting_invalid, Toast.LENGTH_LONG).show()
                    ServiceRequirement.BOTH -> Toast.makeText(requireContext(), R.string.invalid_both, Toast.LENGTH_LONG).show()
                    //이미 실행중인경우 안전하게 메시지 전달
                    else -> kotlin.runCatching {  callback?.startService() }.onFailure { failure -> Toast.makeText(requireContext(), failure.message, Toast.LENGTH_LONG).show() }
                }
            }
        }
    }

    //setting으로 이동
    private fun initListener(binding: MainLayoutBinding) {
        binding.settings.setOnClickListener {
            callback?.switchSetting()
        }
        binding.start.setOnClickListener {
            mainViewModel.requestStartService() //서비스 시작은 vm으로 요청
        }
        binding.stop.setOnClickListener {
            callback?.stopService()
        }
    }

    override fun onAttach(context: Context) {
        callback = context as ActivityCallback
        super.onAttach(context)
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }


}