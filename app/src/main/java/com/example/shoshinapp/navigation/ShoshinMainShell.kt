package com.example.shoshinapp.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.shoshinapp.R
import com.example.shoshinapp.data.db.AppDatabase
import com.example.shoshinapp.sync.*
import com.example.shoshinapp.ui.screens.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.ProfileViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ShoshinMainShell(
    rootNavController: NavHostController,
    database: AppDatabase,
    syncManager: SyncManager,
    networkMonitor: NetworkStateMonitor,
    conflictResolver: ConflictResolver,
    userRepository: com.example.shoshinapp.data.user.UserRepository
) {
    val innerNav = rememberNavController()
    val currentBackStack by innerNav.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    Scaffold(
        containerColor = ShPaper,
        bottomBar = {
            ShoshinBottomBar(
                currentRoute = currentRoute,
                onTabSelected = { tab ->
                    innerNav.navigate(tab.route) {
                        popUpTo(innerNav.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onFabClick = {
                    rootNavController.navigate(ShRoutes.ACTIVATION)
                }
            )
        }
    ) { padding ->
        NavHost(
            navController = innerNav,
            startDestination = ShRoutes.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(ShRoutes.HOME) {
                DashboardTab(
                    navController = rootNavController,
                    syncManager = syncManager,
                    networkMonitor = networkMonitor,
                    conflictResolver = conflictResolver
                )
            }

            composable(ShRoutes.CONSISTENCY) {
                ConsistencyScreen(navController = rootNavController)
            }

            composable(ShRoutes.GROUPS) {
                GroupsScreen(navController = rootNavController)
            }

            composable(ShRoutes.PROFILE) {
                val profileViewModel: ProfileViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return ProfileViewModel(userRepository) as T
                    }
                })
                ProfileScreen(navController = rootNavController, viewModel = profileViewModel)
            }
        }
    }
}

@Composable
fun ShoshinBottomBar(
    currentRoute: String?,
    onTabSelected: (ShTab) -> Unit,
    onFabClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        // Custom Bar Surface - Design Spec: rgba(250,249,246,0.86)
        Surface(
            color = ShPaper.copy(alpha = 0.86f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(80.dp), // Design Spec: ~80dp
            tonalElevation = 0.dp
        ) {
            Column {
                HorizontalDivider(color = ShLine, thickness = 1.dp)
                Row(
                    modifier = Modifier.fillMaxSize().navigationBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val tabs = ShTab.entries
                    
                    // Left 2 tabs: Home, Progress
                    tabs.take(2).forEach { tab ->
                        BottomNavItem(
                            tab = tab,
                            isSelected = currentRoute == tab.route,
                            onClick = { onTabSelected(tab) }
                        )
                    }

                    // Space for FAB
                    Spacer(modifier = Modifier.width(72.dp))

                    // Right 2 tabs: Groups, You
                    tabs.drop(2).forEach { tab ->
                        BottomNavItem(
                            tab = tab,
                            isSelected = currentRoute == tab.route,
                            onClick = { onTabSelected(tab) }
                        )
                    }
                }
            }
        }

        // Floating FAB - Design Spec: 56dp circle, ShVermillion, margin-top: -26dp
        FloatingActionButton(
            onClick = onFabClick,
            containerColor = ShVermillion,
            contentColor = Color.White,
            shape = androidx.compose.foundation.shape.CircleShape,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-26).dp)
                .size(56.dp),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 4.dp
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_bolt_heavy),
                contentDescription = "Begin morning",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun BottomNavItem(
    tab: ShTab,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .width(64.dp)
    ) {
        Icon(
            painter = painterResource(if (isSelected) tab.activeIconRes else tab.iconRes),
            contentDescription = tab.label,
            tint = if (isSelected) ShInk else ShFog2,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = tab.label,
            style = ShKickerStyle.copy(
                fontSize = 10.sp,
                letterSpacing = 1.sp, // Reduced letter spacing for small labels
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) ShInk else ShFog2
            )
        )
    }
}

enum class ShTab(val route: String, val iconRes: Int, val activeIconRes: Int, val label: String) {
    Home(ShRoutes.HOME, R.drawable.ic_home, R.drawable.ic_home_heavy, "Home"),
    Progress(ShRoutes.CONSISTENCY, R.drawable.ic_pulse, R.drawable.ic_pulse_heavy, "Progress"),
    Groups(ShRoutes.GROUPS, R.drawable.ic_groups, R.drawable.ic_groups_heavy, "Groups"),
    You(ShRoutes.PROFILE, R.drawable.ic_user, R.drawable.ic_user_heavy, "You")
}
