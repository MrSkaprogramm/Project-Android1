package com.mitra.di

import androidx.room.Room
import com.mitra.ApiRetrofit
import com.mitra.FirstChatPreferences
import com.mitra.Preferences
import com.mitra.SettingsPreferences
import com.mitra.data.AppDatabase
import com.mitra.network.SocketClient
import com.mitra.ui.screens.auto_leave_chat_dialog.AutoLeaveChatViewModel
import com.mitra.ui.screens.avatar.AvatarViewModel
import com.mitra.ui.screens.banned_current_user.BannedCurrentUserViewModel
import com.mitra.ui.screens.chat.ChatViewModel
import com.mitra.ui.screens.exit_chat_dialog_screen.ExitChatViewModel
import com.mitra.ui.screens.filter.FilterViewModel
import com.mitra.ui.screens.no_result_dialog.NoResultViewModel
import com.mitra.ui.screens.report.ReportViewModel
import com.mitra.ui.screens.report_other.ReportOtherViewModel
import com.mitra.ui.screens.reported.ReportedViewModel
import com.mitra.ui.screens.reported_current_user.ReportedCurrentUserViewModel
import com.mitra.ui.screens.search_dialog.SearchCompanionViewModel
import com.mitra.ui.screens.settings.SettingsViewModel
import com.mitra.ui.screens.splash.SplashViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val networkModule = module {
    single { SocketClient(get()) }
    single { ApiRetrofit() }
}

val dataModule = module {
    single {
        Room
            .databaseBuilder(get(), AppDatabase::class.java, "message-database")
            .fallbackToDestructiveMigration()
            .build()
    }
    single { Preferences(get()) }
    single { SettingsPreferences(get()) }
    single { FirstChatPreferences(get()) }
    single { ProviderAndroidId() }
    single { ProviderIsRunningActivity() }
}

val viewModelsModule = module {
    viewModel { FilterViewModel(get(), get()) }
    viewModel { ChatViewModel(get(), get(), get()) }
    viewModel { SplashViewModel(get(), get(), get()) }
    viewModel { AvatarViewModel(get()) }
    viewModel { SearchCompanionViewModel(get(), get()) }
    viewModel { ExitChatViewModel(get(), get()) }
    viewModel { AutoLeaveChatViewModel(get(), get()) }
    viewModel { ReportViewModel(get()) }
    viewModel { ReportOtherViewModel(get()) }
    viewModel { ReportedViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { ReportedCurrentUserViewModel(get(), get()) }
    viewModel { BannedCurrentUserViewModel(get()) }
    viewModel { NoResultViewModel(get()) }
}