package com.mitra.ui.screens.search_dialog

import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.mitra.R
import com.mitra.databinding.DialogSearchCompanionBinding
import com.mitra.ui.base.BaseDialogFragment
import org.koin.android.viewmodel.ext.android.viewModel

class SearchCompanionDialogFragment : BaseDialogFragment<DialogSearchCompanionBinding>() {

    val viewModel by viewModel<SearchCompanionViewModel>()
    private var progressBar: ProgressBar? = null
    private var cancelButton: View? = null

    override fun bindView(
        inflater: LayoutInflater
    ): DialogSearchCompanionBinding = DialogSearchCompanionBinding.inflate(inflater, null, false)

    override fun initView(view: View?) {
        progressBar = _binding?.progress

        cancelButton?.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun firstInit(view: View?) {
        viewModel.subscribeRoomConnect().observe(this as LifecycleOwner) {
            findNavController().navigate(
                R.id.chatFragment,
                bundleOf(
                    "roomId" to it.room,
                    "name" to it.companionName,
                    "age" to it.companionAge
                )
            )
        }

        viewModel.subscribeUpdateProgress().observe(this as LifecycleOwner) {
            progressBar?.progress = it
        }

        viewModel.subscribeFinishProgress().observe(this as LifecycleOwner) {
            findNavController().popBackStack()
            findNavController().navigate(R.id.action_loginFragment_to_no_result)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.findCompanion(1000)
        progressBar?.max = 1000
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModel.subscribeRoomConnect().removeObservers(this)
        viewModel.cancelTimerFindCompanion()
    }
}