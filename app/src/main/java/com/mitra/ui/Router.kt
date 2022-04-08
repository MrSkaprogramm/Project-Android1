package com.mitra.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class Router(private val fragmentManager: FragmentManager, private val idContainer: Int) {

    /*fun goToSplash(activity: Context) {
        routeFragment(SplashFragment(), SPLASH_SCREEN)
    }

    fun goToFilter() {
        replaceFragment(FilterFragment(), FILTER_SCREEN)
    }

    fun goToPrivacy() {
        routeFragment(PrivacyFragment(), PRIVACY_SCREEN)
    }

    fun goToBanned() {
        routeFragment(BannedCurrentUserDialog(), BANNED_SCREEN)
    }

    fun goToSearchView() {
        routeFragment(SearchCompanionDialogFragment(), SEARCH_SCREEN)
    }

    fun goToReportedCurrentUser(bundle: Bundle) {
        routeFragment(ReportedCurrentUserDialog(), REPORTED_CURRENT_SCREEN)
    }

    fun goToSettings() {
        routeFragment(SettingsFragment(), SETTINGS_SCREEN)
    }

    fun goToAvatar() {
        routeFragment(AvatarFragment(), AVATAR_SCREEN)
    }

    fun back() {
        fragmentManager.popBackStack()
    }

    fun back(mark: String) {
        fragmentManager.popBackStack(mark, 0)
    }*/

    fun routeFragment(fragment: Fragment, tag: String? = null) {
        fragmentManager
            .beginTransaction()
            .replace(idContainer, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

    fun replaceFragment(fragment: Fragment, tag: String? = null) {
        fragmentManager.popBackStack()
        fragmentManager
            .beginTransaction()
            .replace(idContainer, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

    companion object {
        const val SPLASH_SCREEN = "splash"
        const val FILTER_SCREEN = "filter"
        const val PRIVACY_SCREEN = "privacy"
        const val BANNED_SCREEN = "banned"
        const val SEARCH_SCREEN = "search"
        const val REPORTED_CURRENT_SCREEN = "reported_current"
        const val SETTINGS_SCREEN = "settings"
        const val AVATAR_SCREEN = "avatar"
    }
}