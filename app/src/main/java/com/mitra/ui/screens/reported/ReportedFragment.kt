package com.mitra.ui.screens.reported

import android.view.LayoutInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import com.mitra.R
import com.mitra.databinding.DialogReportedCompanionBinding
import com.mitra.ui.base.BaseDialogFragment
import com.mitra.view.MitraAlternatriveButton
import com.mitra.view.MitraButton
import org.koin.android.ext.android.inject

class ReportedFragment : BaseDialogFragment<DialogReportedCompanionBinding>() {

    private val viewModel by inject<ReportedViewModel>()

    private var findChat: MitraButton? =  null
    private var changeOptions: MitraAlternatriveButton? = null

    override fun bindView(
        inflater: LayoutInflater
    ): DialogReportedCompanionBinding =
        DialogReportedCompanionBinding.inflate(inflater, null, false)

    override fun initView(view: View?) {
        findChat = view?.findViewById(R.id.find_chat)
        changeOptions = view?.findViewById(R.id.change_options)

        findChat?.setOnClickListener {
            val executeFun = {
                viewModel.leaveRoom()
                viewModel.runFinder()
                findNavController().popBackStack(R.id.loginFragment, false)
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
                viewModel.leaveRoom()
                findNavController().popBackStack(R.id.loginFragment, false)
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

    override fun useCancelable(): Boolean = false
}