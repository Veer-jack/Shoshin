package com.Shoshin.app.ui.screens

import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.Shoshin.app.data.db.AppDatabase
import com.Shoshin.app.sync.*
import com.Shoshin.app.navigation.ShoshinMainShell
import com.Shoshin.app.data.user.UserRepository

@Composable
fun HomeScreen(
    navController: NavController,
    database: AppDatabase,
    syncManager: SyncManager,
    networkMonitor: NetworkStateMonitor,
    conflictResolver: ConflictResolver,
    userRepository: UserRepository,
    streakViewModel: com.Shoshin.app.viewmodel.StreakViewModel
) {
    // HomeScreen now just delegates to ShoshinMainShell which handles the tabs
    ShoshinMainShell(
        rootNavController = navController as androidx.navigation.NavHostController,
        database = database,
        syncManager = syncManager,
        networkMonitor = networkMonitor,
        conflictResolver = conflictResolver,
        userRepository = userRepository,
        streakViewModel = streakViewModel
    )
}
