package com.mitra.ui.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.mitra.network.SocketClient
import com.mitra.ui.main.MainActivity
import org.koin.android.ext.android.inject

abstract class BaseDialogFragment<T : ViewBinding> : DialogFragment() {

    private var firstRun = true
    private var keyListener: String? = null
    var _binding: T? = null

    abstract fun bindView(inflater: LayoutInflater): T

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val tempContext = context
        _binding = bindView(layoutInflater)
        val binding = _binding

        return if (tempContext != null) {
            val view = binding?.root

            initView(view)

            if (firstRun) {
                firstRun = false
                firstInit(view)
            }
            isCancelable = useCancelable()
            AlertDialog.Builder(tempContext)
                .setView(view)
                .create()
        } else {
            super.onCreateDialog(savedInstanceState)
        }

    }

    fun setResultFragmentListener(key: String, listener: (Bundle?) -> Unit) {
        mainActivity().results[key] = listener
    }

    fun setResult(key: String, bundle: Bundle?) {
        mainActivity().results[key]?.invoke(bundle)
    }

    private fun mainActivity() = activity as MainActivity

    fun getRouter() = mainActivity().router

    abstract fun initView(view: View?)

    open fun firstInit(view: View?) {}

    open fun useCancelable() = true

    fun getInterstitialAd() = mainActivity().mInterstitialAd

    fun setCloseAds(listener: (() -> Unit)) {
        mainActivity().setListenerCloseAds(listener)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        if (!inject<SocketClient>().value.isConnected() && canCheckConnected()) {
            mainActivity().runAppAgain()
        }
    }

    open fun canCheckConnected() = true

    override fun onDestroy() {
        super.onDestroy()

        keyListener?.let {
            mainActivity().results.remove(it)
        }
    }
}