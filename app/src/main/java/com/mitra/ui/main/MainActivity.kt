package com.mitra.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.material.tabs.TabLayout
import com.mitra.MyApplication
import com.mitra.NotificationChannelPreferences
import com.mitra.NotificationController
import com.mitra.R
import com.mitra.databinding.MainActivityBinding
import com.mitra.di.*
import com.mitra.network.SocketClient
import com.mitra.ui.Router


import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin


class MainActivity : AppCompatActivity() {

    var runningActivity = false
    var router: Router? = null
    private var isCustomClickListener: Boolean = false
    private var customListenerBack: (() -> Unit)? = null
    val socketClient: SocketClient by inject()
    val mInterstitialAd: InterstitialAd = InterstitialAd(this)
    private var listenerCloseAds: (() -> Unit)? = null
    private var isCustomBackPressed: Boolean = false
    private var customBackPressed: (() -> Unit)? = null
    val results = mutableMapOf<String, (Bundle?) -> Unit>()
    private val isRunningActivity: ProviderIsRunningActivity? =
        com.mitra.di.inject<ProviderIsRunningActivity>()

    private lateinit var binding: MainActivityBinding

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MobileAds.initialize(this) {}
        mInterstitialAd.adUnitId = "ca-app-pub-7321389691493527/1748286668"
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                mInterstitialAd.loadAd(AdRequest.Builder().build())
                listenerCloseAds?.invoke()
            }
        }
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        val provider = inject<ProviderAndroidId>().value
        provider.set(Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID))
        router = Router(supportFragmentManager, R.id.container_hueyner)
        socketClient.serverIsNotAvailable = {
            Toast.makeText(this, R.string.server_is_not_available, Toast.LENGTH_SHORT).show()
        }
        socketClient.needAuthorizedListener.add(listener)
        socketClient.disconnectListener = {
            Toast.makeText(this, R.string.server_is_not_available, Toast.LENGTH_SHORT).show()
            /*val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Log.d("disconnecting", "listenr")*/
        }
        binding.toolbar.setNavigationOnClickListener {
            if (isCustomClickListener) {
                customListenerBack?.invoke()
            } else {
                onBackPressed()
            }
        }
        val channelId = NotificationChannelPreferences(applicationContext).getChannelId()

        if (channelId.isEmpty()) {
            NotificationController.createNewNotificationChannel(applicationContext)
        }
    }

    fun showTabs() {
        binding.tabs.visibility = View.VISIBLE
    }

    fun getTabs() = binding.tabs

    fun hideTabs() {
        binding.tabs.visibility = View.GONE
    }

    fun runAppAgain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun showToolbar() {
        binding.toolbarContainer.visibility = View.VISIBLE
    }

    fun hideToolbar() {
        if (binding.toolbarContainer != null)
            binding.toolbarContainer.visibility = View.GONE
    }

    fun inflateMenu(menu: Int, menuItemClickListener: Toolbar.OnMenuItemClickListener?) {
        if (binding.toolbar != null) {
            binding.toolbar.menu.clear()
            binding.toolbar.inflateMenu(menu)
            binding.toolbar.setOnMenuItemClickListener(menuItemClickListener)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    fun setListenerCloseAds(listener: (() -> Unit)?) {
        this.listenerCloseAds = listener
    }

    private val listener: () -> Unit = {
        if (findNavController(R.id.my_nav_host_fragment)
                .currentDestination
                ?.label != "fragment_splash2"
        ) {
            stopKoin()
            startKoin {
                androidContext(applicationContext as MyApplication)
                modules(
                    listOf(
                        networkModule,
                        viewModelsModule,
                        dataModule
                    )
                )
            }
            finish()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    fun setTitle(title: String) {
        binding.navTitle.text = title
    }

    override fun onResume() {
        super.onResume()

        runningActivity = true
        isRunningActivity?.set(true)
    }

    override fun onPause() {
        super.onPause()

        runningActivity = false
        isRunningActivity?.set(false)
    }

    override fun onDestroy() {
        socketClient.needAuthorizedListener.remove(listener)
        socketClient.disconnect()

        super.onDestroy()
    }

    override fun onBackPressed() {
        if (!isCustomBackPressed) {
            super.onBackPressed()
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment)
            val backStackEntryCount =
                navHostFragment?.childFragmentManager?.backStackEntryCount ?: 0
            if (backStackEntryCount <= 0) {
                finish()
            }
        } else {
            customBackPressed?.invoke()
        }
    }

    fun setCustomBackPressed(isCustomBackPressed: Boolean, customBackPressed: () -> Unit) {
        this.isCustomBackPressed = isCustomBackPressed
        this.customBackPressed = customBackPressed
    }

    fun setCustomListenerBack(customListener: () -> Unit) {
        this.customListenerBack = customListener
    }

    fun useCustomListenerBack(isCustomClickListener: Boolean) {
        this.isCustomClickListener = isCustomClickListener
    }

    fun setNavigationIcon(iconBack: Int?) {
        if (binding.toolbar != null) {
            if (iconBack != null) {
                binding.toolbar.setNavigationIcon(iconBack)

            } else {
                binding.toolbar.navigationIcon = null
            }
        }
    }

    fun setAvatarNavBar(showAvatarNavBar: Boolean, avatarNavBar: Int) {
        if (binding.toolbar != null) {
            if (showAvatarNavBar) {
                binding.navBarAvatar.visibility = View.VISIBLE
                binding.navBarAvatar.setImageResource(avatarNavBar)
            } else {
                binding.navBarAvatar.visibility = View.GONE
            }
        }
    }
}