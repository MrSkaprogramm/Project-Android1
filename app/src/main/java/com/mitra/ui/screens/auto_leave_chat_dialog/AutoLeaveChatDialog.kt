package com.mitra.ui.screens.auto_leave_chat_dialog

import android.view.LayoutInflater
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.mitra.R
import com.mitra.databinding.DialogAutoLeaveChatBinding
import com.mitra.ui.base.BaseDialogFragment
import com.mitra.view.MitraAlternatriveButton
import com.mitra.view.MitraButton
import org.koin.android.viewmodel.ext.android.viewModel

class AutoLeaveChatDialog : BaseDialogFragment<DialogAutoLeaveChatBinding>() {

    private val viewModel by viewModel<AutoLeaveChatViewModel>()
    private var findChat: MitraButton? =  null
    private var changeOptions: MitraAlternatriveButton? = null

    override fun bindView(
        inflater: LayoutInflater
    ): DialogAutoLeaveChatBinding = DialogAutoLeaveChatBinding.inflate(inflater, null, false)

    override fun initView(view: View?) {

        if (viewModel.checkFirstChat()) {
            viewModel.setFirstChat(false)
        }

        findChat = view?.findViewById(R.id.find_chat)
        changeOptions = view?.findViewById(R.id.change_options)

        findChat?.setOnClickListener {
            val executeFun = {
                viewModel.runFinder()
                this.view?.findNavController()?.popBackStack(R.id.loginFragment, false)
                    ?: findNavController().popBackStack(R.id.loginFragment, false)
            }

            if (getInterstitialAd().isLoaded) {
                getInterstitialAd().show()
                setCloseAds {
                    executeFun()
                }
            } else {
                executeFun()
            }
        }
        changeOptions?.setOnClickListener {
            val executeFun = {
                this.view?.findNavController()?.popBackStack(R.id.loginFragment, false)
                    ?: findNavController().popBackStack(R.id.loginFragment, false)
            }

            if (getInterstitialAd().isLoaded) {
                getInterstitialAd().show()
                setCloseAds {
                    executeFun()
                }
            } else {
                executeFun()
            }
        }
    }

    override fun useCancelable() = false
}