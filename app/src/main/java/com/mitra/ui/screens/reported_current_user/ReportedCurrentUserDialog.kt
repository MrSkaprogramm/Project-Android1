package com.mitra.ui.screens.reported_current_user

import android.view.LayoutInflater
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.mitra.R
import com.mitra.databinding.DialogReportedCurrentUserBinding
import com.mitra.ui.base.BaseDialogFragment
import com.mitra.ui.screens.splash.SplashFragment
import org.koin.android.viewmodel.ext.android.viewModel

class ReportedCurrentUserDialog : BaseDialogFragment<DialogReportedCurrentUserBinding>() {

    val viewModel by viewModel<ReportedCurrentUserViewModel>()

    override fun bindView(
        inflater: LayoutInflater
    ): DialogReportedCurrentUserBinding =
        DialogReportedCurrentUserBinding.inflate(inflater, null, false)

    override fun initView(view: View?) {
        _binding?.okay?.setOnClickListener {
            if (arguments?.getBoolean("splash", false) == true) {
                this.view?.findNavController()?.popBackStack()
                setResult(SplashFragment.AWAIT_REPORT_DIALOG, null)
            } else {
                viewModel.leaveRoom()
                findNavController().popBackStack(R.id.loginFragment, false)
            }
        }
    }

    override fun useCancelable() = false
}