package com.comet.floatingtwitter.twitter.setting.view

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.comet.floatingtwitter.R
import com.comet.floatingtwitter.callback.ActivityCallback
import com.comet.floatingtwitter.databinding.SettingLayoutBinding
import com.comet.floatingtwitter.twitter.setting.view.validator.ValidateResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private val viewModel: SettingViewModel by viewModels()
    private var callback: ActivityCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as ActivityCallback
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = SettingLayoutBinding.inflate(inflater)
        initView(binding)
        initViewModel(binding)
        return binding.root
    }


    private fun initView(binding: SettingLayoutBinding) {
        applyColorViewer(binding.mentionColor, binding.mentionInput)
        applyColorViewer(binding.dmColor, binding.dmInput)
        applyColorViewer(binding.bothColor, binding.bothInput)
        binding.oauthSetup.setOnClickListener {
            callback?.switchOAuth()
        }
    }

    // EditText 텍스트 변경시 컬러 할당해주기
    // 이거를 vm으로 넘겨서 livedata로 받으면 좀 무리가 많이 가지 않을까..? 이정도는 뷰에서도 괜찮지 않나 싶음
    private fun applyColorViewer(textView: TextView, editText: EditText) {
        editText.addTextChangedListener {
            kotlin.runCatching {
                textView.setTextColor(Color.parseColor(it.toString()))
            }
        }
    }


    private fun initViewModel(binding: SettingLayoutBinding) {
        // 토큰 설정여부 관측
        viewModel.isTokenSetLiveData.observe(viewLifecycleOwner) { isExist ->
            if (isExist) binding.oauthText.text = getString(R.string.oauth_set)
            else binding.oauthText.text = getString(R.string.oauth_not_set)
        }

        // 설정 정보 관측
        viewModel.settingDataLiveData.observe(viewLifecycleOwner) { setting ->
            binding.slider.value = setting.size.toFloat()
            binding.mentionInput.setText(setting.mentionColor)
            binding.dmInput.setText(setting.directMessageColor)
            binding.bothInput.setText(setting.bothNotifyColor)
        }

        viewModel.responseLiveData.observe(viewLifecycleOwner) { response ->
            response.getContent()?.let {
                if (it.type == ValidateResult.OK) {
                    Toast.makeText(requireContext(), R.string.save_success, Toast.LENGTH_LONG)
                        .show()
                    callback?.switchMain()
                }
                else {
                    // error field map
                    val errorField = it.field.map {
                        binding.root.findViewById<EditText>(resources.getIdentifier(it, "id", requireContext().packageName)).text.toString()
                    }
                    Toast.makeText(requireContext(), "잘못된 값이 작성되었습니다. ${errorField.joinToString(separator = ", ")}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        binding.save.setOnClickListener {
            viewModel.saveSetting(binding.slider.value.toInt(), binding.mentionInput.text.toString(), binding.dmInput.text.toString(), binding.bothInput.text.toString())
        }

    }

    private fun formatColor(value: Int): String = String.format("#%06X", value.or(0xFFFFFF))


}