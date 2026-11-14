package com.playwithme.assistant.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.playwithme.assistant.feature.analytics.AnalyticsScreen
import com.playwithme.assistant.feature.assistant.AssistantScreen
import com.playwithme.assistant.feature.build.BuildRecommendationScreen
import com.playwithme.assistant.feature.hero.HeroDetailScreen
import com.playwithme.assistant.feature.hero.HeroListScreen
import com.playwithme.assistant.feature.home.HomeScreen
import com.playwithme.assistant.feature.overlay.OverlayControlScreen
import com.playwithme.assistant.feature.settings.SettingsScreen

/**
 * Root navigation graph for PlayWitMe.
 *
 * All screens are wired here. ViewModels are injected via hiltViewModel()
 * inside each screen composable.
 */
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToHeroes = { navController.navigate(Screen.HeroList.route) },
                onNavigateToAssistant = { navController.navigate(Screen.Assistant.route) },
                onNavigateToBuild = { navController.navigate(Screen.Build.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToOverlay = { navController.navigate(Screen.OverlayControl.route) },
                onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) },
                onNavigateToScannedHeroes = { navController.navigate(Screen.ScannedHeroes.route) }
            )
        }

        composable(Screen.HeroList.route) {
            HeroListScreen(
                onHeroClick = { heroId ->
                    navController.navigate(Screen.HeroDetail.createRoute(heroId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.HeroDetail.route,
            arguments = listOf(navArgument(Screen.HeroDetail.ARG_HERO_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val heroId = backStackEntry.arguments?.getString(Screen.HeroDetail.ARG_HERO_ID) ?: return@composable
            HeroDetailScreen(
                heroId = heroId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Assistant.route) {
            AssistantScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Build.route) {
            BuildRecommendationScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.OverlayControl.route) {
            OverlayControlScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Analytics.route) {
            AnalyticsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.ScannedHeroes.route) {
            com.playwithme.assistant.feature.hero.ScannedHeroesScreen(onBack = { navController.popBackStack() })
        }
    }
}
