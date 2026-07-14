package com.example.shoshinapp.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.example.shoshinapp.data.db.AppDatabase
import com.example.shoshinapp.sync.*
import com.example.shoshinapp.ui.screens.*

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import com.example.shoshinapp.data.ShoshinRepository
import com.example.shoshinapp.data.AuthRepository
import com.example.shoshinapp.data.BadgeRepository
import com.example.shoshinapp.data.FriendRepository
import com.example.shoshinapp.data.ReferralRepository
import com.example.shoshinapp.data.UserLimitsRepository
import com.example.shoshinapp.data.user.UserRepository
import com.example.shoshinapp.GoogleAuthManager
import com.example.shoshinapp.utils.AnalyticsManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.example.shoshinapp.viewmodel.*
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
    val badgeRepository = remember { BadgeRepository(database.badgeDao()) }
    val friendRepository = remember { FriendRepository(database.friendDao(), firestore) }
    val referralRepository = remember { ReferralRepository(database.userLimitsDao(), firestore) }
    val limitsRepository = remember { UserLimitsRepository(database.userLimitsDao(), firestore) }
    val contactsRepository = remember { com.example.shoshinapp.data.ContactsRepository(context) }
    val groupRepository = remember { com.example.shoshinapp.data.groups.GroupRepository(database.groupDao(), database.groupMemberDao()) }
    
    val onboardingViewModel = viewModel<OnboardingViewModel>(factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return OnboardingViewModel(userRepository, shoshinRepository) as T
        }
    })
    
    val streakViewModel = viewModel<StreakViewModel>(factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StreakViewModel(userRepository, badgeRepository) as T
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
            return GroupStatsViewModel(database.groupDao()) as T
        }
    })

    val groupViewModel = viewModel<GroupViewModel>(factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GroupViewModel(groupRepository) as T
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
            if (navController.currentDestination?.route != ShRoutes.ONBOARDING &&
                navController.currentDestination?.route != ShRoutes.PERMISSIONS &&
                navController.currentDestination?.route != ShRoutes.GOAL_SELECTION &&
                navController.currentDestination?.route != ShRoutes.ROUTINE_TEMPLATE) {
                navController.navigate(ShRoutes.ONBOARDING) {
                    popUpTo(0) { inclusive = true }
                }
            }
        } else {
            // Logged in and onboarding done
            if (navController.currentDestination?.route == ShRoutes.SPLASH ||
                navController.currentDestination?.route == ShRoutes.AUTH ||
                navController.currentDestination?.route == ShRoutes.ONBOARDING) {
                navController.navigate(ShRoutes.MAIN) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    // Milestone Auto-Trigger
    val lastMilestone by streakViewModel.lastMilestoneReached.collectAsState()
    LaunchedEffect(lastMilestone) {
        lastMilestone?.let { milestone ->
            val templateKey = shoshinRepository.template.first()
            val habitName = when(templateKey) {
                "study" -> "Deep Study"
                "gym" -> "Strength"
                else -> "Morning Walk"
            }
            val user = streakViewModel.user.value
            navController.navigate(
                ShRoutes.streakShare(
                    streak = milestone,
                    habit = habitName,
                    start = user?.streakStartDate ?: 0L
                )
            )
            streakViewModel.clearMilestone()
        }
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        googleAuthManager.handleSignInResult(
            task = task,
            onSuccess = {
                val account = task.result
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                scope.launch {
                    handleNewUser(
                        userId = userId,
                        displayName = account?.displayName ?: "User",
                        phone = null,
                        email = account?.email,
                        referralCode = null, // Google sign in doesn't have a field for this yet in current UI
                        referralRepository = referralRepository,
                        userRepository = userRepository,
                        shoshinRepository = shoshinRepository,
                        database = database,
                        navController = navController
                    )
                }
            },
            onError = { e ->
                android.util.Log.e("Auth", "Google sign in failed", e)
                isGoogleLoading = false
            }
        )
    }

    NavHost(
        navController  = navController,
        startDestination = when {
            !isLoggedIn               -> ShRoutes.SPLASH
            !hasCompletedOnboarding   -> ShRoutes.ONBOARDING
            else                      -> ShRoutes.MAIN
        },
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
                onEmailContinue = { email, pass, code ->
                    navController.navigate(ShRoutes.otpEmail(email, pass, code))
                },
                onGoogleSignIn = {
                    isGoogleLoading = true
                    googleSignInLauncher.launch(googleAuthManager.getSignInIntent())
                },
                onPrivacyClick = { navController.navigate(ShRoutes.PRIVACY) },
                onTermsClick = { navController.navigate(ShRoutes.TERMS) },
                isGoogleLoading = isGoogleLoading,
                initialReferralCode = deepLinkCode
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
                        handleNewUser(userId, "User", contact, null, code, referralRepository, userRepository, shoshinRepository, database, navController)
                    }
                }
            )
        }

        // ── Email OTP ────────────────────────────────────────
        composable(
            route     = ShRoutes.OTP_EMAIL + "?pass={pass}&code={code}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("pass") { type = NavType.StringType; defaultValue = "" },
                navArgument("code") { type = NavType.StringType; nullable = true; defaultValue = null }
            ),
            enterTransition  = { slideInHorizontally(tween(320)) { it } },
            exitTransition   = { slideOutHorizontally(tween(320)) { -it } },
        ) { back ->
            val email = back.arguments?.getString("email") ?: ""
            val pass = back.arguments?.getString("pass") ?: ""
            val referralCode = back.arguments?.getString("code")
            OTPVerifyScreen(
                navController = navController,
                shoshinRepository = shoshinRepository,
                email = email,
                password = pass,
                mode = OtpMode.Email,
                referralCode = referralCode,
                onSuccess = { userId, contact, code ->
                    scope.launch {
                        handleNewUser(userId, "User", null, contact, code, referralRepository, userRepository, shoshinRepository, database, navController)
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
                    navController.navigate(ShRoutes.routineTemplate(goalKey))
                },
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
                groupViewModel = groupViewModel
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
                    return ShareViewModel(context) as T
                }
            })
            
            ShareScreen(
                navController = navController,
                viewModel = shareViewModel,
                streak = streak,
                habitName = habitName,
                startDate = startDate
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
            EditorScreen(navController = navController, database = database)
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
            GroupInviteScreen(navController = navController, groupId = groupId)
        }

        // ── Group Preview ────────────────────────────────────
        composable(
            route = ShRoutes.GROUP_PREVIEW,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { back ->
            val groupId = back.arguments?.getString("groupId") ?: ""
            GroupPreviewScreen(navController = navController, groupId = groupId)
        }

        // ── Group Leaderboard ────────────────────────────────
        composable(
            route = ShRoutes.GROUP_LEADERBOARD,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { back ->
            val groupId = back.arguments?.getString("groupId") ?: ""
            GroupLeaderboardScreen(navController = navController, groupId = groupId)
        }

        // ── Support ──────────────────────────────────────────
        composable(ShRoutes.SUPPORT) {
            SupportScreen(navController = navController)
        }

        // ── Notifications ────────────────────────────────────
        composable(ShRoutes.NOTIFICATIONS) {
            NotificationsScreen(navController = navController)
        }

        // ── Data Privacy ─────────────────────────────────────
        composable(ShRoutes.DATA_PRIVACY) {
            DataPrivacyScreen(navController = navController)
        }

        // ── Badge Unlock ─────────────────────────────────────
        composable(
            route = ShRoutes.BADGE_UNLOCK,
            arguments = listOf(navArgument("badgeId") { type = NavType.StringType })
        ) { back ->
            val badgeId = back.arguments?.getString("badgeId") ?: ""
            BadgeUnlockScreen(navController = navController, viewModel = badgeViewModel, badgeId = badgeId)
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
                onSkip    = { navController.popBackStack() },
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
    navController: NavHostController
) {
    try {
        android.util.Log.d("Auth", "handleNewUser: userId=$userId, name=$displayName")
        
        // 1. Basic user save to DataStore
        shoshinRepository.saveUser(name = displayName, email = email ?: "", phone = phone ?: "")
        
        // 2. Create or Update UserEntity in local DB and Firestore
        val existingUser = userRepository.getUser(userId)
        val newUser = if (existingUser == null) {
            com.example.shoshinapp.data.db.entities.UserEntity(
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

        // 3. Add Welcome Notifications
        try {
            val notifications = listOf(
                com.example.shoshinapp.data.db.entities.NotificationEntity(
                    notificationId = java.util.UUID.randomUUID().toString(),
                    userId = userId,
                    type = "welcome",
                    title = "Welcome to Shoshin",
                    body = "Begin your morning practice today. Start with intention.",
                    iconRes = com.example.shoshinapp.R.drawable.ic_sun,
                    timestamp = System.currentTimeMillis()
                ),
                com.example.shoshinapp.data.db.entities.NotificationEntity(
                    notificationId = java.util.UUID.randomUUID().toString(),
                    userId = userId,
                    type = "achievement",
                    title = "First Step Taken",
                    body = "You've successfully created your account. The journey begins.",
                    iconRes = com.example.shoshinapp.R.drawable.ic_bolt_heavy,
                    timestamp = System.currentTimeMillis() - 1000
                )
            )
            notifications.forEach { database.notificationDao().insertNotification(it) }
        } catch (e: Exception) {
            android.util.Log.e("Auth", "Failed to insert welcome notifications", e)
        }

        // 4. Generate referral code for new user
        try {
            referralRepository.generateAndSaveReferralCode(userId, displayName)
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
        with(kotlinx.coroutines.Dispatchers.Main) {
            if (navController.currentDestination?.route != ShRoutes.ONBOARDING) {
                navController.navigate(ShRoutes.ONBOARDING) {
                    popUpTo(ShRoutes.AUTH) { inclusive = true }
                }
            }
        }
    } catch (e: Exception) {
        android.util.Log.e("Auth", "Critical error in handleNewUser", e)
    }
}
