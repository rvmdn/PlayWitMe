package com.playwithme.assistant.app.navigation

import com.playwithme.assistant.core.common.Constants

/**
 * Sealed class defining all navigation routes in the app.
 *
 * Usage in NavGraph:
 * ```kotlin
 * composable(Screen.Home.route) { HomeScreen() }
 * navController.navigate(Screen.HeroDetail.createRoute("hero_123"))
 * ```
 */
sealed class Screen(val route: String) {
    data object Home : Screen(Constants.ROUTE_HOME)
    data object HeroList : Screen(Constants.ROUTE_HERO_LIST)
    data object HeroDetail : Screen(Constants.ROUTE_HERO_DETAIL) {
        fun createRoute(heroId: String) = "hero_detail/$heroId"
        const val ARG_HERO_ID = "heroId"
    }
    data object Assistant : Screen(Constants.ROUTE_ASSISTANT)
    data object Build : Screen(Constants.ROUTE_BUILD)
    data object OverlayControl : Screen(Constants.ROUTE_OVERLAY_CONTROL)
    data object Settings : Screen(Constants.ROUTE_SETTINGS)
    data object Analytics : Screen(Constants.ROUTE_ANALYTICS)
    data object Draft : Screen(Constants.ROUTE_DRAFT)
    data object ScannedHeroes : Screen("scanned_heroes")
}
