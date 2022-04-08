package com.mitra.ui.screens.avatar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.observe
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.mitra.R
import com.mitra.data.AvatarArray
import com.mitra.databinding.FragmentAvatarBinding
import com.mitra.ui.base.BaseFragment
import org.koin.android.ext.android.inject

class AvatarFragment : BaseFragment<FragmentAvatarBinding>(), ItemClickListener {

    private val viewModel by inject<AvatarViewModel>()
    private val adapter: ViewPagerAdapter = ViewPagerAdapter(this)

    override fun bindView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAvatarBinding = FragmentAvatarBinding.inflate(inflater, container, false)

    override fun firstInit(view: View?) {
        viewModel.subscribeChoosenAvatarId().observe(this as LifecycleOwner) {
            adapter.setSelected(it.first, it.second)
            _binding?.avatarPager?.post {
                val tabs = getTabs()
                tabs.setScrollPosition(it.first, 0f, true)
                _binding?.avatarPager?.setCurrentItem(it.first, false)
            }
        }
        viewModel.subscribeListAvatars().observe(this as LifecycleOwner) {
            adapter.setItems(it.map { avatarList ->
                avatarList.listAvatars
            })
        }
    }

    override fun isShowTabs() = true

    private val listener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(p0: TabLayout.Tab?) {
            _binding?.avatarPager?.setCurrentItem(p0?.position ?: 0, true)
        }

        override fun onTabUnselected(p0: TabLayout.Tab?) {}

        override fun onTabReselected(p0: TabLayout.Tab?) {}
    }

    override fun initView(view: View?) {
        _binding?.avatarPager?.adapter = adapter
        viewModel.updateView()
        val tabs = getTabs()
        tabs.removeAllTabs()
        AvatarArray().array.forEach {
            tabs.addTab(
                tabs
                    .newTab()
                    .setText(it.stringId)
            )
        }
        tabs.addOnTabSelectedListener(listener)
        _binding?.avatarPager
            ?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    tabs.removeOnTabSelectedListener(listener)
                    tabs.getTabAt(position)?.select()
                    tabs.addOnTabSelectedListener(listener)
                }
            })
        setTitle(getString(R.string.avatar_title))
    }

    override fun onClick(id: Int) {
        viewModel.updateId(id)
    }
}