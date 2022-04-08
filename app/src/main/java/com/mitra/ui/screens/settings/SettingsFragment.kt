package com.mitra.ui.screens.settings

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mitra.R
import com.mitra.databinding.FragmentSettingsBinding
import com.mitra.ui.base.BaseFragment
import org.koin.android.viewmodel.ext.android.viewModel

class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun bindView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater, container, false)

    override fun initView(view: View?) {
        setTitle(getString(R.string.settings_title))
        _binding?.vibration?.isChecked = viewModel.getVibration()
        _binding?.sound?.isChecked = viewModel.getSound()
        _binding?.vibration?.setOnCheckedChangeListener { _, isChecked ->
            context?.let {
                viewModel.setVibration(isChecked, it)
            }
        }
        _binding?.sound?.setOnCheckedChangeListener { _, isChecked ->
            context?.let {
                viewModel.setSound(isChecked, it)
                _binding?.vibration?.isEnabled = isChecked
            }
        }
        _binding?.email?.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:mitra.feedback@gmail.com")
            startActivity(intent)
        }
        _binding?.rate?.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("market://details?id=com.mitra.chat.anonymous.ws")
            startActivity(intent)
        }
        _binding?.agreement?.setOnClickListener {
            findNavController().navigate(R.id.privacyFragment)
        }
        _binding?.postingRules?.setOnClickListener {
            findNavController().navigate(R.id.postingRulesFragment)
        }
    }
}