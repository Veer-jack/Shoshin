package com.example.shoshinapp.ui.screens

import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.example.shoshinapp.data.db.AppDatabase
import com.example.shoshinapp.sync.*
import com.example.shoshinapp.navigation.ShoshinMainShell
import com.example.shoshinapp.data.user.UserRepository

@Composable
fun HomeScreen(
    navController: NavController,
    database: AppDatabase,
    syncManager: SyncManager,
    networkMonitor: NetworkStateMonitor,
    conflictResolver: ConflictResolver,
    userRepository: UserRepository
) {
    // HomeScreen now just delegates to ShoshinMainShell which handles the tabs
    ShoshinMainShell(
        rootNavController = navController as androidx.navigation.NavHostController,
        database = database,
        syncManager = syncManager,
        networkMonitor = networkMonitor,
        conflictResolver = conflictResolver,
        userRepository = userRepository
    )
}
