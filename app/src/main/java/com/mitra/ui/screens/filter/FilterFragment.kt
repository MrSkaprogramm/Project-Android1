package com.mitra.ui.screens.filter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.mitra.R
import com.mitra.databinding.FragmentLoginBinding
import com.mitra.di.ProviderAndroidId
import com.mitra.di.inject
import com.mitra.ui.base.BaseFragment
import com.mitra.utils.NameAdapter
import com.mitra.utils.setOnClickThrottleListener
import com.mitra.view.ClickStateListener
import com.mitra.view.OnRangeSeekbarChangeListener
import com.mitra.view.State
import org.koin.android.viewmodel.ext.android.viewModel

class FilterFragment : BaseFragment<FragmentLoginBinding>() {

    val viewModel by viewModel<FilterViewModel>()

    override fun bindView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false)

    override fun firstInit(view: View?) {
        /*viewModel.subscribeRoomConnect().observe(this as LifecycleOwner) {
            findNavController().navigate(
                R.id.action_loginFragment_to_chatFragment,
                bundleOf(
                    "roomId" to it.room,
                    "name" to it.companionName,
                    "age" to it.companionAge
                )
            )
        }*/
        viewModel.nameLiveData.observe(this as LifecycleOwner) {
            _binding?.login?.setText(NameAdapter.decodeName(it))
        }
        viewModel.ageLiveData.observe(this as LifecycleOwner) {
            val age = if (it > 0) it else 18
            _binding?.age?.setText(age.toString())
        }
        viewModel.genderLiveData.observe(this as LifecycleOwner) {
            _binding?.gender?.chooseStateByIndex(it)
        }
        viewModel.showMeLiveData.observe(this as LifecycleOwner) {
            _binding?.companionGender?.chooseStateByIndex(it)
        }
        viewModel.rangeAgeCompanionLiveData.observe(this as LifecycleOwner) {
            if (_binding?.range?.getOnRangeSeekbarChangeListener() == null) {
                _binding?.range?.setOnRangeSeekbarChangeListener(object :
                    OnRangeSeekbarChangeListener {
                    override fun valueChanged(minValue: Number?, maxValue: Number?) {
                        viewModel.setAgeMin(minValue?.toInt() ?: 0)
                        viewModel.setAgeMax(maxValue?.toInt() ?: 0)
                    }
                })
            }

            _binding?.range
                ?.setMinStartValue(it.first.toFloat())
                ?.setMaxStartValue(it.second.toFloat())
                ?.apply()
        }
        viewModel.subscribeCountClients().observe(this as LifecycleOwner) { count ->
            val text = getString(R.string.available_clients, count)
            val availableText: Spannable = SpannableString(text)

            availableText.setSpan(
                ForegroundColorSpan(Color.BLACK),
                24,
                text.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            _binding?.availableClients?.text = availableText
        }
        viewModel.avatarMutableLiveData.observe(this as LifecycleOwner) { resId ->
            val tempContext = context
            tempContext?.let {
                _binding?.avatar?.setImageResource(resId ?: R.drawable.ic_man_1)
            }
        }
        viewModel.startFindCompanion.observe(this as LifecycleOwner) {
            if (it) {
                _binding?.sendLogin?.performClick()
            }
        }
        viewModel.startChatCompanion.observe(this as LifecycleOwner) {
            if (it) {
                this.view?.findNavController()?.navigate(R.id.chatFragment)
            }
        }
        viewModel.reInit.observe(this) {
            viewModel.getCountClients()
            viewModel.updateUserInfo()
        }
        getInterstitialAd()
    }

    override fun initView(view: View?) {
        if (arguments?.containsKey("show_chat") == true) {
            arguments = null
            findNavController().navigate(R.id.chatFragment)
        }

        setTitle(getString(R.string.filter_title))
        _binding?.sendLogin?.setOnClickThrottleListener {
            prepareData {
                this.view?.findNavController()?.navigate(R.id.action_loginFragment_to_dialog_search)
            }
            hideSoftKeyboard(_binding?.login)
        }

        _binding?.login?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                _binding?.sendLogin?.performClick()

                true
            } else {
                false
            }
        }

        _binding?.avatar?.setOnClickListener { v ->
            prepareData(null)
            hideSoftKeyboard(v)
            this.view?.findNavController()?.navigate(R.id.action_loginFragment_to_avatarFragment)
        }

        _binding?.gender
            ?.addState(
                State(
                    getString(R.string.male_gender),
                    GenderStates.Male,
                    R.drawable.ic_male
                )
            )
            ?.addState(
                State(
                    getString(R.string.female_gender),
                    GenderStates.Female,
                    R.drawable.ic_female
                )
            )


        _binding?.gender?.listener = object : ClickStateListener {
            override fun onClickState(index: Int, data: Any?) {
                viewModel.setGender(index)
                val indexForCompanion = if (index == 0) 1 else 0
                _binding?.companionGender?.chooseStateByIndex(indexForCompanion)
                viewModel.setShowMe(indexForCompanion)
            }
        }

        _binding?.companionGender
            ?.addState(
                State(
                    getString(R.string.boys_gender),
                    GenderStates.Male,
                    R.drawable.ic_male
                )
            )
            ?.addState(
                State(
                    getString(R.string.girl_gender),
                    GenderStates.Female,
                    R.drawable.ic_female
                )
            )

        _binding?.companionGender?.listener = object :
            ClickStateListener {
            override fun onClickState(index: Int, data: Any?) {
                viewModel.setShowMe(index)
            }
        }

        viewModel.getCountClients()
        viewModel.updateUserInfo()
    }

    private fun prepareData(callback: (() -> Unit)?) {
        _binding?.login?.text?.let { name ->
            if (name.isNotEmpty()) {
                _binding?.age?.text?.let { age ->
                    if (age.isNotEmpty() && age.isDigitsOnly()) {
                        if (age.toString().toInt() >= 18) {
                            viewModel.sendFilter(
                                NameAdapter.encodeName(name.toString()),
                                age.toString().toInt()
                            )
                            callback?.invoke()
                        } else {
                            Toast
                                .makeText(context, R.string.age_lower_18, Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast
                            .makeText(context, getString(R.string.fill_age), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    /*override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }*/

    override fun onStart() {
        super.onStart()

        _binding?.also {
            it.login.clearFocus()
            it.age.clearFocus()
        }
    }

    override fun onResume() {
        super.onResume()

        _binding?.also {
            it.login.clearFocus()
            it.age.clearFocus()
        }
    }

    /*override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
    }*/


    override fun getIconBack(): Nothing? = null

    override fun getMenuId() = R.menu.login_menu

    override fun getMenuItemClickListener(): Toolbar.OnMenuItemClickListener =
        Toolbar.OnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.settings) {
                this.view?.findNavController()
                    ?.navigate(R.id.action_loginFragment_to_settingsFragment)
                //getRouter()?.goToSettings()
            }
            if (menuItem.itemId == R.id.dev_menu) {
                val clipboard =
                    activity?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                val clip: ClipData =
                    ClipData.newPlainText("Mitra device ID", inject<ProviderAndroidId>()?.get())
                clipboard?.setPrimaryClip(clip)
                activity?.let {
                    Toast.makeText(it, "deviceId скопирован в буфер", Toast.LENGTH_LONG).show()
                }
            }

            true
        }
}

enum class GenderStates {
    Male, Female, Both
}