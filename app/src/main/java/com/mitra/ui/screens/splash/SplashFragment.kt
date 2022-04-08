package com.mitra.ui.screens.splash

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.*
import com.mitra.R
import com.mitra.databinding.FragmentSplashBinding
import com.mitra.ui.base.BaseFragment
import org.koin.android.viewmodel.ext.android.viewModel

class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    private val viewModel: SplashViewModel by viewModel()

    private var startTimestamp: Long = 0

    override fun bindView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSplashBinding = FragmentSplashBinding.inflate(inflater, container, false)

    override fun isShowToolbar() = false

    override fun firstInit(view: View?) {
        viewModel.subscribeShowApproveLicenseButton().observe(this as LifecycleOwner) {
            if (!it) {
                _binding?.firstStart?.visibility = View.VISIBLE
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    activity?.let {
                        try {
                            this.view?.findNavController()
                                ?.navigate(R.id.action_splashFragment_to_loginFragment)
                        } catch (t: Throwable) {
                            val navOptions = NavOptions
                                .Builder()
                                .setPopUpTo(R.id.splashFragment, true)
                                .build()
                            this.view?.findNavController()
                                ?.navigate(R.id.loginFragment, null, navOptions)
                        }

                        //getRouter()?.goToFilter()
                    }
                }, getPostTime())
            }
        }
        viewModel.subscribeTemporaryLockedUser().observe(this as LifecycleOwner) {
            if (it) {
                activity?.let {
//                    getRouter()?.goToReportedCurrentUser(bundleOf("splash" to true))
                    this.view
                        ?.findNavController()
                        ?.navigate(
                            R.id.action_splashFragment_to_reported_current_user,
                            bundleOf("splash" to true)
                        )
                }
            }

            setResultFragmentListener(AWAIT_REPORT_DIALOG) {}
        }

        viewModel.subscribeShowBlockedUserDialog().observe(this as LifecycleOwner) {
            if (it) {
                activity?.let {
                    this.view?.findNavController()
                        ?.navigate(R.id.action_splashFragment_to_banned_current_user)
//                    getRouter()?.goToBanned()
                }
            }
        }

        viewModel.subscribeStartChat().observe(this as LifecycleOwner) {
            Handler(Looper.getMainLooper()).postDelayed({
                activity?.let {
//                    getRouter()?.goToFilter()
                    this.view?.findNavController()
                        ?.navigate(
                            R.id.action_splashFragment_to_loginFragment,
                            bundleOf("show_chat" to true)
                        )
                }
            }, getPostTime())
        }
    }

    private lateinit var androidId: String

    @SuppressLint("HardwareIds")
    override fun initView(view: View?) {
        // Inflate the layout for this fragment
        androidId = Settings.Secure.getString(
            context?.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        startTimestamp = System.currentTimeMillis()
        _binding?.continueBtn?.setOnClickListener {
            viewModel.licenseApprove()

            activity?.let {
//                getRouter()?.goToFilter()
                this.view?.findNavController()
                    ?.navigate(R.id.action_splashFragment_to_loginFragment)
            }
        }

        val text = getString(R.string.privacy_text)
        val ss = SpannableString(text)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                activity?.let {
                    this@SplashFragment.view?.findNavController()
                        ?.navigate(R.id.action_splashFragment_to_privacyFragment)
//                    getRouter()?.goToPrivacy()
                }
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        ss.setSpan(clickableSpan, 27, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        _binding?.privacy?.text = ss
        _binding?.privacy?.movementMethod = LinkMovementMethod.getInstance()
        _binding?.privacy?.highlightColor = Color.TRANSPARENT
        viewModel.getUser()
    }

    private fun getPostTime(): Long {
        val difference = System.currentTimeMillis() - startTimestamp
        return if (difference < 3000) 3000 - difference else 100
    }

    override fun canCheckConnected() = false

    companion object {
        const val AWAIT_REPORT_DIALOG = "await_report_dialog"
    }

}