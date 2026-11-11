package com.callnote.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhoneInTalk
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.callnote.app.ui.categories.CategoryScreen
import com.callnote.app.ui.home.HomeScreen
import com.callnote.app.ui.profile.ProfileScreen
import com.callnote.app.ui.records.RecordListScreen

object Routes {
    const val HOME = "home"
    const val RECORDS = "records"
    const val RECORDS_FILTERED = "$RECORDS?categoryId={categoryId}"
    const val CATEGORIES = "categories"
    const val PROFILE = "profile"
}

private data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(Routes.HOME, "首页", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Routes.RECORDS_FILTERED, "录音", Icons.Filled.PhoneInTalk, Icons.Outlined.PhoneInTalk),
    BottomNavItem(Routes.CATEGORIES, "分类", Icons.Filled.Category, Icons.Outlined.Category),
    BottomNavItem(Routes.PROFILE, "我的", Icons.Filled.Person, Icons.Outlined.Person)
)

@Composable
fun CallNoteApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == item.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            // 录音页带可选分类参数，点击底部导航时回到全部录音
                            val target =
                                if (item.route == Routes.RECORDS_FILTERED) Routes.RECORDS else item.route
                            navController.navigate(target) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onNavigateToCategories = {
                        navController.navigate(Routes.CATEGORIES) { launchSingleTop = true }
                    }
                )
            }
            composable(
                route = Routes.RECORDS_FILTERED,
                arguments = listOf(
                    navArgument("categoryId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) {
                RecordListScreen()
            }
            composable(Routes.CATEGORIES) {
                CategoryScreen(
                    onCategoryClick = { categoryId ->
                        navController.navigate("${Routes.RECORDS}?categoryId=$categoryId") {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Routes.PROFILE) {
                ProfileScreen()
            }
        }
    }
}
