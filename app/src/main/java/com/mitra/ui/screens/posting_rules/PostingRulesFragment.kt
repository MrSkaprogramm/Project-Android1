package com.mitra.ui.screens.posting_rules

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mitra.R
import com.mitra.data.PrivacyTerms
import com.mitra.databinding.FragmentPrivacyBinding
import com.mitra.ui.base.BaseFragment
import com.mitra.utils.toHtml


class PostingRulesFragment : BaseFragment<FragmentPrivacyBinding>() {

    override fun bindView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPrivacyBinding = FragmentPrivacyBinding.inflate(inflater, container, false)

    override fun initView(view: View?) {
        setTitle(getString(R.string.posting_rules_title))
        _binding?.privacyTerms?.text = PrivacyTerms.POSTING_RULES.toHtml()
    }

    override fun isCustomClickListener() = true

    override fun setCustomListener(): () -> Unit = { findNavController().popBackStack() }
}