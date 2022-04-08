package com.mitra.ui.screens.banned_current_user

import android.view.LayoutInflater
import android.view.View
import com.mitra.databinding.DialogBannedCurrentUserBinding
import com.mitra.ui.base.BaseDialogFragment
import org.koin.android.viewmodel.ext.android.viewModel

class BannedCurrentUserDialog : BaseDialogFragment<DialogBannedCurrentUserBinding>() {

    val viewModel by viewModel<BannedCurrentUserViewModel>()

    override fun bindView(
        inflater: LayoutInflater
    ): DialogBannedCurrentUserBinding =
        DialogBannedCurrentUserBinding.inflate(inflater, null, false)

    override fun initView(view: View?) {
        _binding?.okay?.setOnClickListener {
            viewModel.leaveRoom()
            activity?.finish()
        }
    }
}