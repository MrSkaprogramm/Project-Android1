package com.mitra.ui.screens.exit_chat_dialog_screen

import android.view.LayoutInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.mitra.R
import com.mitra.databinding.DialogExitChatBinding
import com.mitra.ui.base.BaseDialogFragment
import com.mitra.view.MitraAlternatriveButton
import com.mitra.view.MitraButton
import org.koin.android.viewmodel.ext.android.viewModel


class ExitChatDialogFragment : BaseDialogFragment<DialogExitChatBinding>() {

    private val viewModel by viewModel<ExitChatViewModel>()

    private var findChat: MitraButton? =  null
    private var changeOptions: MitraAlternatriveButton? = null

    override fun bindView(
        inflater: LayoutInflater
    ): DialogExitChatBinding = DialogExitChatBinding.inflate(inflater, null, false)

    override fun initView(view: View?) {

        if (viewModel.checkFirstChat()) {
            viewModel.setFirstChat(false)
        }

        findChat = view?.findViewById(R.id.find_chat)
        changeOptions = view?.findViewById(R.id.change_options)

        findChat?.setOnClickListener {
            viewModel.leaveRoom()
            val executeFunc = {
                viewModel.runFinder()
                findNavController().popBackStack(R.id.loginFragment, false)
            }

            if (getInterstitialAd().isLoaded) {
                getInterstitialAd().show()
                setCloseAds {
                    executeFunc()
                }
            } else {
                executeFunc()
                getInterstitialAd().loadAd(AdRequest.Builder().build())
            }
        }
        changeOptions?.setOnClickListener {
            viewModel.leaveRoom()
            val executeFunc = {
                findNavController().popBackStack(R.id.loginFragment, false)
            }

            if (getInterstitialAd().isLoaded) {
                getInterstitialAd().show()
                setCloseAds {
                    executeFunc()
                }
            } else {
                executeFunc()
                getInterstitialAd().loadAd(AdRequest.Builder().build())
            }
        }
    }

    override fun useCancelable() = true
}