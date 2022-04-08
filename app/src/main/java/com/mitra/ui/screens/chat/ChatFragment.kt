package com.mitra.ui.screens.chat

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitra.R
import com.mitra.data.AvatarArray
import com.mitra.data.Typing
import com.mitra.databinding.FragmentChatBinding
import com.mitra.ui.base.BaseFragment
import com.mitra.utils.NameAdapter
import com.mitra.utils.trimNewLines
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.concurrent.timer

class ChatFragment : BaseFragment<FragmentChatBinding>() {
    var androidId = ""
    val viewModel by viewModel<ChatViewModel>()
    var adapter: ChatAdapter? = null
    var timer: Timer? = null
    var typing = false

    override fun bindView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChatBinding = FragmentChatBinding.inflate(inflater, container, false)

    override fun isCustomBackPressed() = true

    override fun customBackPressed(): () -> Unit = {
        activity?.finish()
    }

    override fun isCustomClickListener() = true

    override fun setCustomListener(): () -> Unit = {
        hideSoftKeyboard(this.view)
        this.view?.findNavController()?.navigate(R.id.action_chatFragment_to_exitChatDialog)
    }


    override fun firstInit(view: View?) {
        if (!viewModel.subscribeMessage().hasObservers()) {
            viewModel.subscribeMessage().observe(this as LifecycleOwner) { msg ->
                adapter?.updateList(msg)
                adapter?.itemCount?.let {
                    _binding?.list?.scrollToPosition(if (it > 0) it - 1 else 0)
                }
            }
        }
        if (!viewModel.subscribeTypingLiveData().hasObservers()) {
            viewModel.subscribeTypingLiveData().observe(this as LifecycleOwner) {
                if (androidId != it.deviceId) {
                    if (it.typing) {
                        adapter?.addTyping()
                        adapter?.itemCount?.let { position ->
                            _binding?.list?.scrollToPosition(if (position > 0) position - 1 else 0)
                        }
                    } else {
                        adapter?.deleteTyping()
                    }
                }
            }
        }

        viewModel.subscribeUpdateRoomData().observe(this as LifecycleOwner) {
            it?.let { room ->
                AvatarArray().findAvatar(room.companionAvatar)?.let { avatar ->
                    setAvatarNavBar(avatar.resourceId)
                }
                setTitle("${NameAdapter.decodeName(room.companionName)}, ${room.companionAge}")
            }
        }

        viewModel.subscribeBanUser().observe(this as LifecycleOwner) {
            if (it) {
                hideSoftKeyboard(this.view)
                this.view?.findNavController()?.navigate(R.id.banned_current_user)
            }
        }

        viewModel.subscribeReportUser().observe(this as LifecycleOwner) {
            if (it) {
                hideSoftKeyboard(this.view)
                this.view?.findNavController()?.navigate(R.id.reported_current_user)
            }
        }

        if (!viewModel.subscribeLeaveRoom().hasObservers()) {
            viewModel.subscribeLeaveRoom().observe(this as LifecycleOwner) {
                hideSoftKeyboard(this.view)
                findNavController().navigate(R.id.autoLeaveChatDialog)
            }
        }

        viewModel.subscribeLeaveHostRoom().observe(this as LifecycleOwner) {
            hideSoftKeyboard(this.view)
            this.view?.findNavController()
                ?.navigate(R.id.autoLeaveChatDialog)
        }
    }

    @SuppressLint("HardwareIds")
    override fun initView(view: View?) {
        androidId = Settings.Secure.getString(
            context?.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        _binding?.sendMessage?.setOnClickListener {
            _binding?.emojiEditText?.text.toString().let {
                _binding?.emojiEditText?.setText("")
                viewModel.sendMessage(it)
            }
        }
        _binding?.sendMessageIcon?.isEnabled = false
        _binding?.list?.layoutManager = LinearLayoutManager(activity)
        context?.let {
            _binding?.list?.addItemDecoration(DifferentMessageSpaceDecorator())
        }
        adapter = ChatAdapter()
        Log.d("chat", "onCreate")
        _binding?.list?.adapter = adapter

        _binding?.emojiEditText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                _binding?.sendMessageIcon?.isEnabled =
                    s?.trimNewLines()?.isNotEmpty() == true

                if (s?.trimNewLines()?.isNotEmpty() == true) {
                    timer?.cancel()
                    if (typing) {
                        timer = timer(period = 5000L, action = {
                            Handler(Looper.getMainLooper()).post {
                                viewModel.typingMessage(Typing(androidId, false))
                                typing = false
                                timer?.cancel()
                            }
                        }, initialDelay = 5000L)
                    } else {
                        typing = true
                        viewModel.typingMessage(Typing(androidId, true))
                        timer = timer(period = 5000L, action = {
                            Handler(Looper.getMainLooper()).post {
                                viewModel.typingMessage(Typing(androidId, false))
                                typing = false
                                timer?.cancel()
                            }
                        }, initialDelay = 5000L)
                    }
                } else {
                    viewModel.typingMessage(Typing(androidId, false))
                    typing = false
                }
            }
        })

        val roomId = arguments?.getString("roomId", "") ?: ""
        viewModel.roomId = roomId
        viewModel.connectToRoom()
        viewModel.getRoom()?.let { room ->
            AvatarArray().findAvatar(room.companionAvatar)?.let { avatar ->
                setAvatarNavBar(avatar.resourceId)
            }
            setTitle("${NameAdapter.decodeName(room.companionName)}, ${room.companionAge}")
        } ?: run {
            val name = arguments?.getString("name", "") ?: ""
            val age = arguments?.getInt("age") ?: 0
            setTitle("${NameAdapter.decodeName(name)}, $age")
        }

        val emojiIcon = EmojIconActions(context, view, _binding?.emojiEditText, _binding?.emojiIcon)
        emojiIcon.setIconsIds(R.drawable.ic_keyboard, R.drawable.ic_smile)
        _binding?.emojiIcon?.setImageResource(R.drawable.ic_smile)
        emojiIcon.ShowEmojIcon()
        emojiIcon.setKeyboardListener(object : EmojIconActions.KeyboardListener {
            override fun onKeyboardOpen() {
                adapter?.itemCount?.let {
                    _binding?.list?.scrollToPosition(if (it > 0) it - 1 else 0)
                }
            }

            override fun onKeyboardClose() {

            }
        })

        viewModel.getCacheMessages {
            adapter?.items = it.toMutableList()
            adapter?.notifyDataSetChanged()
            adapter?.itemCount?.let { position ->
                _binding?.list?.scrollToPosition(if (position > 0) position - 1 else 0)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        hideSoftKeyboard(this.view)
    }

    override fun getMenuId() = R.menu.chat_menu

    override fun getMenuItemClickListener(): Toolbar.OnMenuItemClickListener =
        Toolbar.OnMenuItemClickListener {
            if (it.itemId == R.id.report) {
                this.view?.findNavController()?.navigate(R.id.action_chatFragment_to_report)
            }

            true
        }

    override fun getIconBack() = R.drawable.ic_close
}