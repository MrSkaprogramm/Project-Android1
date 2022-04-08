package com.mitra.ui.screens.report_other

import android.view.LayoutInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import com.mitra.R
import com.mitra.databinding.DialogReportOtherCompanionBinding
import com.mitra.ui.base.BaseDialogFragment
import org.koin.android.viewmodel.ext.android.viewModel

class ReportOtherFragment : BaseDialogFragment<DialogReportOtherCompanionBinding>() {

    private val viewModel by viewModel<ReportOtherViewModel>()

    override fun bindView(
        inflater: LayoutInflater
    ): DialogReportOtherCompanionBinding =
        DialogReportOtherCompanionBinding.inflate(inflater, null, false)

    override fun initView(view: View?) {
        viewModel.resetNeedShowReportOther()
        _binding?.otherSend?.setOnClickListener {
            if (_binding?.additionalText?.text.toString().isNotEmpty()) {
                viewModel.sendOther(_binding?.additionalText?.text.toString())
                findNavController().popBackStack()
                findNavController().navigate(R.id.reported_companion)
            }
        }
    }
}