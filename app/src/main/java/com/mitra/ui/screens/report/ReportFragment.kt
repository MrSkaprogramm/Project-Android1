package com.mitra.ui.screens.report

import android.view.LayoutInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import com.mitra.R
import com.mitra.databinding.DialogReportCompanionBinding
import com.mitra.ui.base.BaseDialogFragment
import org.koin.android.viewmodel.ext.android.viewModel

class ReportFragment : BaseDialogFragment<DialogReportCompanionBinding>() {

    private val viewModel by viewModel<ReportViewModel>()

    override fun bindView(
        inflater: LayoutInflater
    ): DialogReportCompanionBinding = DialogReportCompanionBinding.inflate(inflater, null, false)

    override fun initView(view: View?) {
        _binding?.other?.setOnClickListener {
            viewModel.otherClick()
            findNavController().popBackStack()
            findNavController().navigate(R.id.action_chatFragment_to_report_other)
        }
        _binding?.inapropriate?.setOnClickListener {
            viewModel.inappropriateClick()
            findNavController().popBackStack()
            findNavController().navigate(R.id.reported_companion)
        }
        _binding?.spam?.setOnClickListener {
            viewModel.spamClick()
            findNavController().popBackStack()
            findNavController().navigate(R.id.reported_companion)
        }
    }
}