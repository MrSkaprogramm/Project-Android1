package com.mitra.ui.screens.no_result_dialog

import android.view.LayoutInflater
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.mitra.R
import com.mitra.databinding.DialogNoResultBinding
import com.mitra.ui.base.BaseDialogFragment
import com.mitra.view.MitraAlternatriveButton
import com.mitra.view.MitraButton
import org.koin.android.viewmodel.ext.android.viewModel

class NoResultDialogFragment: BaseDialogFragment<DialogNoResultBinding>() {

    private val viewModel: NoResultViewModel by viewModel()

    private var findChat: MitraButton? =  null
    private var changeOptions: MitraAlternatriveButton? = null

    override fun bindView(inflater: LayoutInflater): DialogNoResultBinding =
        DialogNoResultBinding.inflate(inflater, null, false)

    override fun initView(view: View?) {
        findChat = view?.findViewById(R.id.find_chat)
        changeOptions = view?.findViewById(R.id.change_options)

        findChat?.setOnClickListener {
            viewModel.runFinder()
            this.view?.findNavController()?.popBackStack(R.id.loginFragment, false)
                ?: findNavController().popBackStack(R.id.loginFragment, false)
            this.view?.findNavController()?.navigate(R.id.dialog_search)
                ?: findNavController().navigate(R.id.dialog_search)
        }
        changeOptions?.setOnClickListener {
            this.view?.findNavController()?.popBackStack(R.id.loginFragment, false)
                ?: findNavController().popBackStack(R.id.loginFragment, false)
        }
    }
}