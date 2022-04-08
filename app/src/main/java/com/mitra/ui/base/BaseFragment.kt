package com.mitra.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.mitra.R
import com.mitra.network.SocketClient
import com.mitra.ui.main.MainActivity
import org.koin.android.ext.android.inject

abstract class BaseFragment<T : ViewBinding> : Fragment() {

    private var firstCreate = true
    private var keyListener: String? = null
    var _binding: T? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firstCreate = true
    }

    abstract fun bindView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindView(inflater, container)
        val binding = _binding
        val view = binding?.root

        if (isShowToolbar()) {
            mainActivity().showToolbar()
        } else {
            mainActivity().hideToolbar()
        }

        mainActivity().setCustomListenerBack(setCustomListener())
        mainActivity().useCustomListenerBack(isCustomClickListener())
        mainActivity().setNavigationIcon(getIconBack())
        mainActivity().setCustomBackPressed(isCustomBackPressed(), customBackPressed())
        mainActivity().setAvatarNavBar(isShowAvatarNavBar(), getAvatarNavBar())
        mainActivity().inflateMenu(getMenuId(), getMenuItemClickListener())

        if (isShowTabs()) {
            mainActivity().showTabs()
        } else {
            mainActivity().hideTabs()
        }

        initView(view)

        if (firstCreate) {
            firstCreate = false
            firstInit(view)
        }


        return view
    }

    override fun onResume() {
        super.onResume()

        if (!inject<SocketClient>().value.isConnected() && canCheckConnected()) {
            //mainActivity().runAppAgain()
        }
    }

    open fun isShowTabs() = false

    fun getTabs() = mainActivity().getTabs()

    open fun canCheckConnected() = true

    fun setResultFragmentListener(key: String, listener: (Bundle?) -> Unit) {
        mainActivity().results[key] = listener
    }

    open fun firstInit(view: View?) {}

    open fun isCustomBackPressed() = false

    open fun customBackPressed(): () -> Unit = {}

    fun setTitle(title: String?) {
        if (!title.isNullOrEmpty()) {
            mainActivity().setTitle(title)
        }
    }

    fun isRunningActivity() = mainActivity().runningActivity

    fun getInterstitialAd() = mainActivity().mInterstitialAd

    open fun getIconBack(): Int? = R.drawable.ic_arrow_left

    open fun isCustomClickListener() = false

    open fun setCustomListener(): () -> Unit = {}

    open fun isShowToolbar() = true

    open fun isShowAvatarNavBar() = false

    open fun getAvatarNavBar() = -1

    fun setAvatarNavBar(avatar: Int) {
        mainActivity().setAvatarNavBar(true, avatar)
    }

    fun getRouter() = mainActivity().router

    open fun getMenuId() = R.menu.empty_menu

    open fun getMenuItemClickListener(): Toolbar.OnMenuItemClickListener =
        Toolbar.OnMenuItemClickListener {
            true
        }

    fun mainActivity() = activity as MainActivity

    abstract fun initView(view: View?)

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()

        firstCreate = false
        keyListener?.let {
            mainActivity().results.remove(it)
        }
    }

    protected fun hideSoftKeyboard(view: View?) {
        view?.let {
            val imm =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}