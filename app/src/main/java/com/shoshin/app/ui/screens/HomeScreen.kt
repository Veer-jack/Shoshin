package com.shoshin.app.ui.screens

import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.shoshin.app.data.db.AppDatabase
import com.shoshin.app.sync.*
import com.shoshin.app.navigation.ShoshinMainShell
import com.shoshin.app.data.user.UserRepository

@Composable
fun HomeScreen(
    navController: NavController,
    database: AppDatabase,
    syncManager: SyncManager,
    networkMonitor: NetworkStateMonitor,
    conflictResolver: ConflictResolver,
    userRepository: UserRepository,
    streakViewModel: com.shoshin.app.viewmodel.StreakViewModel
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
