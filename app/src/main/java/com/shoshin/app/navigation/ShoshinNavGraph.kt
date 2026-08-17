package com.Shoshin.app.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.Shoshin.app.data.db.AppDatabase
import com.Shoshin.app.sync.*
import com.Shoshin.app.ui.screens.*

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import com.Shoshin.app.data.ShoshinRepository
import com.Shoshin.app.data.AuthRepository
import com.Shoshin.app.data.BadgeRepository
import com.Shoshin.app.data.FriendRepository
import com.Shoshin.app.data.ReferralRepository
import com.Shoshin.app.data.RoutineRepository
import com.Shoshin.app.data.UserLimitsRepository
import com.Shoshin.app.data.user.UserRepository
import com.Shoshin.app.GoogleAuthManager
import com.Shoshin.app.utils.AnalyticsManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.Shoshin.app.viewmodel.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext

@Composable
fun ShoshinNavGraph(
    navController: NavHostController,
    database: AppDatabase,
    shoshinRepository: ShoshinRepository,
    syncManager: SyncManager,
    networkMonitor: NetworkStateMonitor,
    conflictResolver: ConflictResolver,
    isLoggedIn: Boolean,
    hasCompletedOnboarding: Boolean,
    deepLinkCode: String? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val firebaseAuth = remember { FirebaseAuth.getInstance() }
    val firestore = remember { FirebaseFirestore.getInstance() }
    val storage = remember { FirebaseStorage.getInstance() }
    val googleAuthManager = remember { GoogleAuthManager(context, firebaseAuth) }
    
    val userRepository = remember { UserRepository(database.userDao(), firestore, storage, firebaseAuth) }
    val authRepository = remember { AuthRepository(firebaseAuth) }
    val badgeRepository = remember { BadgeRepository(database.badgeDao(), userRepository) }
    val friendRepository = remember { FriendRepository(database.friendDao(), firestore) }
    val referralRepository = remember { ReferralRepository(database.userLimitsDao(), firestore) }
    val limitsRepository = remember { UserLimitsRepository(database.userLimitsDao(), firestore) }
    val contactsRepository = remember { com.Shoshin.app.data.ContactsRepository(context) }
    val groupRepository = remember { com.Shoshin.app.data.groups.GroupRepository(database.groupDao(), database.groupMemberDao(), database.notificationDao()) }
    val routineRepository = remember { RoutineRepository(database.routineCheckpointDao(), firestore) }
    val feedbackRepository = remember { com.Shoshin.app.data.FeedbackRepository(database.feedbackDao(), firestore) }

    val onboardingViewModel = viewModel<OnboardingViewModel>(factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return OnboardingViewModel(userRepository, shoshinRepository) as T
        }
    })
    
    val streakViewModel = viewModel<StreakViewModel>(factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StreakViewModel(userRepository, badgeRepository, database.streakDao(), groupRepository) as T
        }
    })

    val badgeViewModel = viewModel<BadgeViewModel>(factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BadgeViewModel(badgeRepository, userRepository) as T
        }
    })

    val friendViewModel = viewModel<FriendStreaksViewModel>(factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FriendStreaksViewModel(friendRepository, userRepository) as T
        }
    })

    val groupStatsViewModel = viewModel<GroupStatsViewModel>(factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GroupStatsViewModel(groupRepository) as T
        }
    })

    val groupViewModel = viewModel<GroupViewModel>(factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GroupViewModel(groupRepository, userRepository) as T
        }
    })

    val inviteViewModel = viewModel<InviteViewModel>(factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return InviteViewModel(userRepository, contactsRepository) as T
        }
    })

    val referralViewModel = viewModel<ReferralViewModel>(factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReferralViewModel(userRepository, referralRepository, limitsRepository) as T
        }
    })

    val statsViewModel = viewModel<StatsViewModel>(factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StatsViewModel(database.statsDao(), database.userDao(), database.badgeDao(), userRepository) as T
        }
    })
    
    var isGoogleLoading by remember { mutableStateOf(false) }
    var googleAuthError by remember { mutableStateOf<String?>(null) }
    var returningUserLastOpenDate by remember { mutableStateOf<Long?>(null) }

    // Dynamic Navigation based on Auth/Onboarding state
    LaunchedEffect(isLoggedIn, hasCompletedOnboarding) {
        if (!isLoggedIn) {
            if (navController.currentDestination?.route != ShRoutes.SPLASH && 
                navController.currentDestination?.route != ShRoutes.AUTH) {
                navController.navigate(ShRoutes.SPLASH) {
                    popUpTo(0) { inclusive = true }
                }
            }
        } else if (!hasCompletedOnboarding) {
            // During onboarding, we stay on the current onboarding sub-step (Onboarding, Permissions, Goal, etc.)
            // We don't force-reset to ShRoutes.ONBOARDING if we're already in that flow.
        } else {
            // Logged in and onboarding done
            if (navController.currentDestination?.route == ShRoutes.SPLASH ||
                navController.currentDestination?.route == ShRoutes.AUTH ||
                navController.currentDestination?.route == ShRoutes.ONBOARDING) {
                val pendingReturn = returningUserLastOpenDate
                if (pendingReturn != null) {
                    returningUserLastOpenDate = null
                    navController.navigate(ShRoutes.returningUser(pendingReturn)) {
                        popUpTo(0) { inclusive = true }
                    }
                } else {
                    navController.navigate(ShRoutes.MAIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        googleAuthManager.handleSignInResult(
            task = task,
            onSuccess = { userId ->
                android.util.Log.d("Auth", "Google sign-in success, UID: $userId")
                scope.launch {
                    val account = GoogleSignIn.getLastSignedInAccount(context)
                    handleNewUser(
                        userId = userId,
                        displayName = account?.displayName ?: "User",
                        phone = null,
                        email = account?.email,
                        referralCode = null,
                        referralRepository = referralRepository,
                        userRepository = userRepository,
                        shoshinRepository = shoshinRepository,
                        database = database,
                        navController = navController,
                        onExistingUser = { returningUserLastOpenDate = it }
                    )
                    isGoogleLoading = false
                }
            },
            onError = { error ->
                android.util.Log.e("Auth", "Google sign in failed: $error")
                googleAuthError = error
                isGoogleLoading = false
            }
        )
    }

    // Remembered deliberately. NavHost builds its graph with remember(startDestination), and
    // assigning a new graph resets the back stack onto the new start destination — so leaving
    // this reactive means every mid-session flag change yanks the user to a different screen.
    // MainActivity withholds composition until DataStore has answered, so the first value is
    // already the correct cold-start route; every later transition is an explicit navigate()
    // or the LaunchedEffect above.
    val startDestination = remember {
        when {
            !isLoggedIn               -> ShRoutes.SPLASH
            !hasCompletedOnboarding   -> ShRoutes.ONBOARDING
            else                      -> ShRoutes.MAIN
        }
    }

    NavHost(
        navController  = navController,
        startDestination = startDestination,
    ) {

        // ── Splash ──────────────────────────────────────────
        composable(
            route = ShRoutes.SPLASH,
            enterTransition  = { fadeIn(tween(300)) },
            exitTransition   = { fadeOut(tween(300)) },
        ) {
            SplashScreen(navController = navController)
        }

        // ── Auth ─────────────────────────────────────────────
        composable(
            route = ShRoutes.AUTH,
            enterTransition  = { slideInHorizontally(tween(320)) { it } },
            exitTransition   = { slideOutHorizontally(tween(320)) { -it } },
            popEnterTransition = { slideInHorizontally(tween(320)) { -it } },
            popExitTransition  = { slideOutHorizontally(tween(320)) { it } },
        ) {
            AuthScreen(
                onPhoneContinue = { phone, code ->
                    navController.navigate(ShRoutes.otpPhone(phone, code))
                },
                onGoogleSignIn = {
                    isGoogleLoading = true
                    googleSignInLauncher.launch(googleAuthManager.getSignInIntent())
                },
                onPrivacyClick = { navController.navigate(ShRoutes.PRIVACY) },
                onTermsClick = { navController.navigate(ShRoutes.TERMS) },
                isGoogleLoading = isGoogleLoading,
                initialReferralCode = deepLinkCode,
                externalError = googleAuthError,
                onClearError = { googleAuthError = null }
            )
        }

        // ── Onboarding ───────────────────────────────────────
        composable(ShRoutes.ONBOARDING) {
            OnboardingScreen(
                viewModel = onboardingViewModel,
                onComplete = {
                    navController.navigate(ShRoutes.PERMISSIONS) {
                        popUpTo(ShRoutes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        // ── Phone OTP ────────────────────────────────────────
        composable(
            route     = ShRoutes.OTP_PHONE,
            arguments = listOf(
                navArgument("phoneNumber") { type = NavType.StringType },
                navArgument("code") { type = NavType.StringType; nullable = true; defaultValue = null }
            ),
            enterTransition  = { slideInHorizontally(tween(320)) { it } },
            exitTransition   = { slideOutHorizontally(tween(320)) { -it } },
        ) { back ->
            val phone = back.arguments?.getString("phoneNumber") ?: ""
            val referralCode = back.arguments?.getString("code")
            OTPVerifyScreen(
                navController = navController,
                shoshinRepository = shoshinRepository,
                phone = phone,
                mode = OtpMode.Phone,
                referralCode = referralCode,
                onSuccess = { userId, contact, code ->
                    scope.launch {
                        handleNewUser(
                            userId, "User", contact, null, code,
                            referralRepository, userRepository, shoshinRepository, database, navController,
                            onExistingUser = { returningUserLastOpenDate = it }
                        )
                    }
                }
            )
        }


        // ── Permissions ──────────────────────────────────────
        composable(
            route = ShRoutes.PERMISSIONS,
            enterTransition  = { slideInHorizontally(tween(320)) { it } },
            exitTransition   = { slideOutHorizontally(tween(320)) { -it } },
        ) {
            PermissionsScreen(
                onContinue = {
                    navController.navigate(ShRoutes.GOAL_SELECTION) {
                        popUpTo(ShRoutes.PERMISSIONS) { inclusive = true }
                    }
                },
            )
        }

        // ── Goal Selection ───────────────────────────────────
        composable(
            route = ShRoutes.GOAL_SELECTION,
            enterTransition  = { slideInHorizontally(tween(320)) { it } },
            exitTransition   = { slideOutHorizontally(tween(320)) { -it } },
            popEnterTransition = { slideInHorizontally(tween(320)) { -it } },
            popExitTransition  = { slideOutHorizontally(tween(320)) { it } },
        ) {
            GoalSelectionScreen(
                onContinue = { goalKey ->
                    if (goalKey == "custom") {
                        navController.navigate(ShRoutes.BUILD_PATH)
                    } else {
                        navController.navigate(ShRoutes.routineTemplate(goalKey))
                    }
                },
            )
        }

        // ── Build Path (Custom Goal) ─────────────────────────
        composable(
            route = ShRoutes.BUILD_PATH,
            enterTransition  = { slideInHorizontally(tween(320)) { it } },
            exitTransition   = { slideOutHorizontally(tween(320)) { -it } },
        ) {
            BuildPathScreen(
                navController = navController,
                onComplete = { name, list ->
                    // Logic to save custom path
                    scope.launch {
                        onboardingViewModel.completeOnboarding("06:00", "22:00")
                        navController.navigate(ShRoutes.MAIN) {
                            popUpTo(ShRoutes.SPLASH) { inclusive = true }
                        }
                    }
                }
            )
        }

        // ── Routine Template ─────────────────────────────────
        composable(
            route     = ShRoutes.ROUTINE_TEMPLATE,
            arguments = listOf(navArgument("goalKey") { type = NavType.StringType }),
            enterTransition  = { slideInHorizontally(tween(320)) { it } },
            exitTransition   = { slideOutHorizontally(tween(320)) { -it } },
        ) { back ->
            val goal = back.arguments?.getString("goalKey") ?: "walk"
            RoutineTemplateScreen(
                goalKey    = goal,
                onContinue = { selectedTemplate ->
                    navController.navigate(ShRoutes.MAIN) {
                        popUpTo(ShRoutes.SPLASH) { inclusive = true }
                    }
                },
            )
        }

        // ── Main shell (bottom nav) ──────────────────────────
        composable(
            route = ShRoutes.MAIN,
            enterTransition = { fadeIn(tween(300)) },
            exitTransition  = { fadeOut(tween(300)) },
        ) {
            ShoshinMainShell(
                rootNavController = navController,
                database = database,
                syncManager = syncManager,
                networkMonitor = networkMonitor,
                conflictResolver = conflictResolver,
                userRepository = userRepository,
                streakViewModel = streakViewModel,
                friendViewModel = friendViewModel,
                referralViewModel = referralViewModel,
                groupViewModel = groupViewModel,
                badgeViewModel = badgeViewModel
            )
        }

        // ── Streak Details ───────────────────────────────────
        composable(ShRoutes.STREAK_DETAILS) {
            StreakDetailsScreen(navController = navController, viewModel = streakViewModel)
        }

        // ── Streak Share ─────────────────────────────────────
        composable(
            route = ShRoutes.STREAK_SHARE,
            arguments = listOf(
                navArgument("streak") { type = NavType.IntType },
                navArgument("habitName") { type = NavType.StringType },
                navArgument("startDate") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val streak = backStackEntry.arguments?.getInt("streak") ?: 0
            val habitName = backStackEntry.arguments?.getString("habitName") ?: "Morning Routine"
            val startDate = backStackEntry.arguments?.getLong("startDate") ?: 0L

            val shareViewModel = viewModel<ShareViewModel>(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ShareViewModel(context.applicationContext as android.app.Application) as T
                }
            })

            val shareUser = streakViewModel.user.value
            val consistencyPercent = if (shareUser != null && shareUser.totalActivations > 0) {
                val daysSinceCreation = ((System.currentTimeMillis() - shareUser.createdAt) / (1000 * 60 * 60 * 24)).coerceAtLeast(1)
                ((shareUser.totalActivations.toFloat() / daysSinceCreation.toFloat()) * 100).toInt().coerceIn(0, 100)
            } else {
                0
            }

            val referralLimits by referralViewModel.limits.collectAsState()

            ShareScreen(
                navController = navController,
                viewModel = shareViewModel,
                streak = streak,
                habitName = habitName,
                startDate = startDate,
                consistencyPercent = consistencyPercent,
                referralCode = referralLimits?.referralCode ?: ""
            )
        }

        // ── Badges ───────────────────────────────────────────
        composable(ShRoutes.BADGES) {
            BadgeScreen(navController = navController, viewModel = badgeViewModel)
        }

        // ── Badge Detail ─────────────────────────────────────
        composable(
            route = ShRoutes.BADGE_DETAIL,
            arguments = listOf(navArgument("badgeId") { type = NavType.StringType })
        ) { back ->
            val badgeId = back.arguments?.getString("badgeId") ?: ""
            BadgeDetailScreen(navController = navController, viewModel = badgeViewModel, badgeId = badgeId)
        }

        // ── All Friends ──────────────────────────────────────
        composable(ShRoutes.ALL_FRIENDS) {
            AllFriendsScreen(navController = navController, viewModel = friendViewModel)
        }

        // ── Friend Profile ───────────────────────────────────
        composable(
            route = ShRoutes.FRIEND_PROFILE,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { back ->
            val userId = back.arguments?.getString("userId") ?: ""
            FriendProfileScreen(navController = navController, viewModel = friendViewModel, friendUserId = userId)
        }

        // ── Invite ───────────────────────────────────────────
        composable(ShRoutes.INVITE) {
            InviteScreen(navController = navController, viewModel = inviteViewModel)
        }

        // ── Referrals ────────────────────────────────────────
        composable(ShRoutes.REFERRALS) {
            ReferralScreen(navController = navController, viewModel = referralViewModel)
        }

        // ── Stats ────────────────────────────────────────────
        composable(ShRoutes.STATS) {
            StatsScreen(navController = navController, viewModel = statsViewModel)
        }

        // ── Profile ──────────────────────────────────────────
        composable(ShRoutes.PROFILE) {
            val profileViewModel = viewModel<ProfileViewModel>(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(userRepository) as T
                }
            })
            ProfileScreen(
                navController = navController, 
                viewModel = profileViewModel,
                badgeViewModel = badgeViewModel
            )
        }

        // ── Edit Profile ─────────────────────────────────────
        composable(ShRoutes.EDIT_PROFILE) {
            val profileViewModel = viewModel<ProfileViewModel>(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(userRepository) as T
                }
            })
            EditProfileScreen(navController = navController, viewModel = profileViewModel)
        }

        // ── Settings ─────────────────────────────────────────
        composable(ShRoutes.SETTINGS) {
            val settingsViewModel = viewModel<SettingsViewModel>(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettingsViewModel(userRepository, shoshinRepository, firebaseAuth) as T
                }
            })
            SettingsScreen(navController = navController, viewModel = settingsViewModel)
        }

        // ── Alarm Setup ──────────────────────────────────────
        composable(
            route = ShRoutes.ALARM_SETUP,
            enterTransition  = { slideInHorizontally(tween(320)) { it } },
            exitTransition   = { slideOutHorizontally(tween(320)) { -it } },
        ) {
            AlarmScreen(navController = navController)
        }

        // ── Backwards Clock ──────────────────────────────────
        composable(
            route = ShRoutes.CLOCK,
            enterTransition  = { slideInHorizontally(tween(320)) { it } },
            exitTransition   = { slideOutHorizontally(tween(320)) { -it } },
        ) {
            val clockViewModel = viewModel<BackwardsClockViewModel>(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BackwardsClockViewModel(userRepository) as T
                }
            })
            BackwardsClockScreen(navController = navController, viewModel = clockViewModel)
        }

        // ── Routine Editor ───────────────────────────────────
        composable(
            route = ShRoutes.ROUTINE_EDITOR,
            enterTransition  = { slideInHorizontally(tween(320)) { it } },
            exitTransition   = { slideOutHorizontally(tween(320)) { -it } },
        ) {
            val routineEditorViewModel = viewModel<RoutineEditorViewModel>(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RoutineEditorViewModel(routineRepository, shoshinRepository, userRepository.userId) as T
                }
            })
            RoutineEditorScreen(navController = navController, viewModel = routineEditorViewModel)
        }

        // ── Sound Picker ──────────────────────────────────────
        composable(
            route = ShRoutes.SOUND_PICKER,
            enterTransition  = { slideInHorizontally(tween(320)) { it } },
            exitTransition   = { slideOutHorizontally(tween(320)) { -it } },
        ) {
            SoundPickerScreen(navController = navController)
        }

        // ── History ──────────────────────────────────────────
        composable(
            route = ShRoutes.HISTORY,
            enterTransition  = { slideInHorizontally(tween(320)) { it } },
            exitTransition   = { slideOutHorizontally(tween(320)) { -it } },
        ) {
            HistoryScreen(navController = navController, streakViewModel = streakViewModel)
        }

        // ── Broken Streak ─────────────────────────────────────
        composable(ShRoutes.BROKEN_STREAK) {
            BrokenStreakScreen(navController = navController, streakViewModel = streakViewModel)
        }

        // ── Returning User ────────────────────────────────────
        composable(
            route = ShRoutes.RETURNING_USER,
            arguments = listOf(navArgument("lastOpenDate") { type = NavType.LongType })
        ) { back ->
            val lastOpenDate = back.arguments?.getLong("lastOpenDate") ?: 0L
            ReturningUserScreen(navController = navController, streakViewModel = streakViewModel, lastOpenDate = lastOpenDate)
        }

        // ── Create Group ─────────────────────────────────────
        composable(
            route = ShRoutes.CREATE_GROUP,
            enterTransition  = { slideInHorizontally(tween(320)) { it } },
            exitTransition   = { slideOutHorizontally(tween(320)) { -it } },
        ) {
            CreateGroupScreen(navController = navController, viewModel = groupViewModel)
        }

        // ── Group Detail ─────────────────────────────────────
        composable(
            route = ShRoutes.GROUP_DETAIL,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType }),
            enterTransition  = { slideInHorizontally(tween(320)) { it } },
            exitTransition   = { slideOutHorizontally(tween(320)) { -it } },
        ) { back ->
            val groupId = back.arguments?.getString("groupId") ?: ""
            GroupDetailScreen(
                navController = navController, 
                groupId = groupId, 
                viewModel = groupViewModel,
                statsViewModel = groupStatsViewModel
            )
        }

        // ── Group Invite ─────────────────────────────────────
        composable(
            route = ShRoutes.GROUP_INVITE,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { back ->
            val groupId = back.arguments?.getString("groupId") ?: ""
            GroupInviteScreen(navController = navController, groupId = groupId, viewModel = groupViewModel)
        }

        // ── Group Preview ────────────────────────────────────
        composable(
            route = ShRoutes.GROUP_PREVIEW,
            arguments = listOf(navArgument("inviteCode") { type = NavType.StringType })
        ) { back ->
            val inviteCode = back.arguments?.getString("inviteCode") ?: ""
            GroupPreviewScreen(navController = navController, inviteCode = inviteCode, viewModel = groupViewModel)
        }

        // ── Group Leaderboard ────────────────────────────────
        composable(
            route = ShRoutes.GROUP_LEADERBOARD,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { back ->
            val groupId = back.arguments?.getString("groupId") ?: ""
            GroupLeaderboardScreen(navController = navController, groupId = groupId, viewModel = groupViewModel, networkMonitor = networkMonitor)
        }

        // ── Group Stats ──────────────────────────────────────
        composable(
            route = ShRoutes.GROUP_STATS,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { back ->
            val groupId = back.arguments?.getString("groupId") ?: ""
            GroupStatsScreen(navController = navController, groupId = groupId, viewModel = groupStatsViewModel)
        }

        // ── Support ──────────────────────────────────────────
        composable(ShRoutes.SUPPORT) {
            val supportViewModel = viewModel<SupportViewModel>(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SupportViewModel(feedbackRepository) as T
                }
            })
            SupportScreen(navController = navController, viewModel = supportViewModel)
        }

        // ── Notifications ────────────────────────────────────
        composable(ShRoutes.NOTIFICATIONS) {
            NotificationsScreen(navController = navController, networkMonitor = networkMonitor)
        }

        // ── Data Privacy ─────────────────────────────────────
        composable(ShRoutes.DATA_PRIVACY) {
            val settingsViewModelForPrivacy = viewModel<SettingsViewModel>(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettingsViewModel(userRepository, shoshinRepository, firebaseAuth) as T
                }
            })
            DataPrivacyScreen(navController = navController, viewModel = settingsViewModelForPrivacy)
        }

        // ── Legal Screens ─────────────────────────────────────
        composable(ShRoutes.PRIVACY) {
            LegalScreen(navController, "legal/privacy.md", "Privacy Policy")
        }
        composable(ShRoutes.TERMS) {
            LegalScreen(navController, "legal/terms.md", "Terms & Conditions")
        }

        // ── Morning Activation (FAB / full-screen alarm) ─────
        composable(
            route = ShRoutes.ACTIVATION,
            enterTransition = { fadeIn(tween(500)) },
            exitTransition  = { fadeOut(tween(300)) },
        ) {
            MorningActivationScreen(
                onBegin = {
                    // "Solve to silence" — the challenge is passed, so stop the looping tone.
                    com.Shoshin.app.alarm.AlarmService.stop(context)
                    navController.navigate(ShRoutes.CHECKPOINT) {
                        popUpTo(ShRoutes.ACTIVATION) { inclusive = true }
                    }
                },
            )
        }

        // ── Camera Verification (mid-checkpoint) ─────────────
        composable(
            route     = ShRoutes.CAMERA_VERIFY,
            arguments = listOf(
                navArgument("checkpointIndex") { type = NavType.IntType },
                navArgument("checkpointLabel") { type = NavType.StringType },
                navArgument("targets") { type = NavType.StringType; defaultValue = "" }
            ),
            enterTransition = { fadeIn(tween(400)) },
            exitTransition  = { fadeOut(tween(300)) },
        ) { back ->
            val idx   = back.arguments?.getInt("checkpointIndex") ?: 0
            val label = back.arguments?.getString("checkpointLabel") ?: ""
            val targets = back.arguments?.getString("targets")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
            CameraVerificationScreen(
                checkpointIndex = idx,
                label           = label,
                targetLabels    = targets,
                onCapture = { navController.popBackStack() }, // return to Checkpoint
                navController = navController,
                database = database
            )
        }

        // ── Checkpoint Flow ──────────────────────────────────
        composable(
            route = ShRoutes.CHECKPOINT,
            enterTransition  = { slideInHorizontally(tween(320)) { it } },
            exitTransition   = { slideOutHorizontally(tween(320)) { -it } },
        ) {
            CheckpointCompletionScreen(
                onPhotoRequired = { idx, label, targets ->
                    navController.navigate(ShRoutes.cameraVerify(idx, label, targets))
                },
                onComplete = {
                    navController.navigate(ShRoutes.MORNING_COMPLETE) {
                        popUpTo(ShRoutes.CHECKPOINT) { inclusive = true }
                    }
                },
                onForfeit = {
                    navController.navigate(ShRoutes.MAIN) {
                        popUpTo(ShRoutes.CHECKPOINT) { inclusive = true }
                    }
                },
                navController = navController,
                database = database,
                streakViewModel = streakViewModel
            )
        }

        // ── Morning Complete ─────────────────────────────────
        composable(
            route = ShRoutes.MORNING_COMPLETE,
            enterTransition = { fadeIn(tween(600)) },
            exitTransition  = { fadeOut(tween(300)) },
        ) {
            MorningCompleteScreen(
                onClose = {
                    navController.navigate(ShRoutes.MAIN) {
                        popUpTo(ShRoutes.MORNING_COMPLETE) { inclusive = true }
                    }
                },
                onShare = {
                    val user = streakViewModel.user.value
                    // Fetch habit name from repository or state
                    scope.launch {
                        val templateKey = shoshinRepository.template.first()
                        val habitName = when(templateKey) {
                            "study" -> "Deep Study"
                            "gym" -> "Strength"
                            else -> "Morning Walk"
                        }
                        navController.navigate(
                            ShRoutes.streakShare(
                                streak = user?.currentStreak ?: 0,
                                habit = habitName,
                                start = user?.streakStartDate ?: 0L
                            )
                        )
                    }
                }
            )
        }
    }
}

private suspend fun handleNewUser(
    userId: String,
    displayName: String,
    phone: String?,
    email: String?,
    referralCode: String?,
    referralRepository: ReferralRepository,
    userRepository: UserRepository,
    shoshinRepository: ShoshinRepository,
    database: AppDatabase,
    navController: NavHostController,
    onExistingUser: (Long) -> Unit = {}
) {
    try {
        android.util.Log.d("Auth", "handleNewUser STARTED: userId=$userId, name=$displayName")

        // 1. Resolve who this is *before* touching DataStore. Onboarding is a first-registration
        // flow, and UserEntity.onboardingCompleted is the durable per-user answer (local Room,
        // backed by Firestore); the DataStore flag is only a device-local cache that logout()
        // wipes, so it has to be re-derived here on every login.
        val existingUser = userRepository.getUser(userId)
        android.util.Log.d("Auth", "handleNewUser: Fetched existing user: ${existingUser != null}")
        if (existingUser != null) {
            onExistingUser(existingUser.lastOpenDate)
        }
        val onboardedUser = existingUser?.takeIf { it.onboardingCompleted }

        // 2. Save to DataStore. Both flags go in one write so the nav graph never observes a
        // logged-in-but-not-onboarded returning user and swing through the onboarding screen.
        shoshinRepository.saveUser(
            name = displayName,
            email = email ?: "",
            phone = phone ?: "",
            onboardingDone = onboardedUser != null
        )
        android.util.Log.d("Auth", "handleNewUser: DataStore saved (onboarded=${onboardedUser != null})")

        val newUser = if (existingUser == null) {
            com.Shoshin.app.data.db.entities.UserEntity(
                userId = userId,
                displayName = displayName,
                email = email,
                phone = phone,
                photoUrl = null
            )
        } else {
            existingUser.copy(
                displayName = if (existingUser.displayName == "New User" || existingUser.displayName == "User") displayName else existingUser.displayName,
                email = email ?: existingUser.email,
                phone = phone ?: existingUser.phone,
                lastUpdated = System.currentTimeMillis()
            )
        }
        userRepository.updateUser(newUser)
        android.util.Log.d("Auth", "handleNewUser: UserRepository updated (Firestore sync initiated)")

        // 3. Add Welcome Notifications
        try {
            val notifications = listOf(
                com.Shoshin.app.data.db.entities.NotificationEntity(
                    notificationId = java.util.UUID.randomUUID().toString(),
                    userId = userId,
                    type = "welcome",
                    title = "Welcome to Shoshin",
                    body = "Begin your morning practice today. Start with intention.",
                    iconRes = com.Shoshin.app.R.drawable.ic_sun,
                    timestamp = System.currentTimeMillis()
                ),
                com.Shoshin.app.data.db.entities.NotificationEntity(
                    notificationId = java.util.UUID.randomUUID().toString(),
                    userId = userId,
                    type = "achievement",
                    title = "First Step Taken",
                    body = "You've successfully created your account. The journey begins.",
                    iconRes = com.Shoshin.app.R.drawable.ic_bolt_heavy,
                    timestamp = System.currentTimeMillis() - 1000
                )
            )
            notifications.forEach { database.notificationDao().insertNotification(it) }
            android.util.Log.d("Auth", "handleNewUser: Welcome notifications inserted")
        } catch (e: Exception) {
            android.util.Log.e("Auth", "Failed to insert welcome notifications", e)
        }

        // 4. Generate referral code for new user
        try {
            referralRepository.generateAndSaveReferralCode(userId, displayName)
            android.util.Log.d("Auth", "handleNewUser: Referral code generated")
        } catch (e: Exception) {
            android.util.Log.e("Auth", "Failed to generate referral code", e)
        }
        
        // 5. Process entered referral code
        if (referralCode != null) {
            try {
                val referrerId = referralRepository.validateReferralCode(referralCode)
                if (referrerId != null && referrerId != userId) {
                    referralRepository.deliverReferralReward(referrerId, userId)
                    AnalyticsManager.logSignupCompleted(method = if (email != null) "email" else "phone", hadReferral = true)
                } else {
                    AnalyticsManager.logSignupCompleted(method = if (email != null) "email" else "phone", hadReferral = false)
                }
            } catch (e: Exception) {
                android.util.Log.e("Auth", "Failed to process referral code", e)
            }
        } else {
            AnalyticsManager.logSignupCompleted(method = if (email != null) "email" else "phone", hadReferral = false)
        }

        AnalyticsManager.setUserProperties(userType = "professional", signupMethod = if (email != null) "email" else "phone", hasReferral = referralCode != null)
        
        // 6. Navigate (Safety: check if already navigating)
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
            if (onboardedUser != null) {
                android.util.Log.d("Auth", "handleNewUser: Returning user, skipping ONBOARDING")
                // The LaunchedEffect above may already have routed here off the DataStore write;
                // compare against the route *pattern*, which is what currentDestination holds.
                if (navController.currentDestination?.route != ShRoutes.RETURNING_USER) {
                    navController.navigate(ShRoutes.returningUser(onboardedUser.lastOpenDate)) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            } else {
                android.util.Log.d("Auth", "handleNewUser: Navigating to ONBOARDING")
                if (navController.currentDestination?.route != ShRoutes.ONBOARDING) {
                    navController.navigate(ShRoutes.ONBOARDING) {
                        popUpTo(ShRoutes.AUTH) { inclusive = true }
                    }
                }
            }
        }

    } catch (e: Exception) {
        android.util.Log.e("Auth", "Critical error in handleNewUser: ${e.message}", e)
    }
}
