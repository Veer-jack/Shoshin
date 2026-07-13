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
import com.example.shoshinapp.data.ShoshinRepository
import com.example.shoshinapp.data.user.UserRepository
import com.example.shoshinapp.GoogleAuthManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.example.shoshinapp.viewmodel.*
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
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val firebaseAuth = remember { FirebaseAuth.getInstance() }
    val firestore = remember { FirebaseFirestore.getInstance() }
    val storage = remember { FirebaseStorage.getInstance() }
    val googleAuthManager = remember { GoogleAuthManager(context, firebaseAuth) }
    
    val userRepository = remember { UserRepository(database.userDao(), firestore, storage, firebaseAuth) }
    
    var isGoogleLoading by remember { mutableStateOf(false) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        googleAuthManager.handleSignInResult(
            task = task,
            onSuccess = {
                val account = task.result
                scope.launch {
                    shoshinRepository.saveUser(
                        name = account?.displayName ?: "User",
                        email = account?.email ?: ""
                    )
                    isGoogleLoading = false
                    navController.navigate(ShRoutes.onboarding(0)) {
                        popUpTo(ShRoutes.AUTH) { inclusive = true }
                    }
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
            !hasCompletedOnboarding   -> ShRoutes.ONBOARDING.replace("{pageIndex}", "0")
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
                onPhoneContinue = { phone ->
                    navController.navigate(ShRoutes.otpPhone(phone))
                },
                onEmailContinue = { email, pass ->
                    navController.navigate(ShRoutes.otpEmail(email) + "?pass=$pass")
                },
                onGoogleSignIn = {
                    isGoogleLoading = true
                    googleSignInLauncher.launch(googleAuthManager.getSignInIntent())
                },
                onPrivacyClick = { navController.navigate(ShRoutes.PRIVACY) },
                onTermsClick = { navController.navigate(ShRoutes.TERMS) },
                isGoogleLoading = isGoogleLoading
            )
        }

        // ── Phone OTP ────────────────────────────────────────
        composable(
            route     = ShRoutes.OTP_PHONE,
            arguments = listOf(navArgument("phoneNumber") { type = NavType.StringType }),
            enterTransition  = { slideInHorizontally(tween(320)) { it } },
            exitTransition   = { slideOutHorizontally(tween(320)) { -it } },
        ) { back ->
            val phone = back.arguments?.getString("phoneNumber") ?: ""
            OTPVerifyScreen(
                navController = navController,
                shoshinRepository = shoshinRepository,
                phone = phone,
                mode = OtpMode.Phone
            )
        }

        // ── Email OTP ────────────────────────────────────────
        composable(
            route     = ShRoutes.OTP_EMAIL + "?pass={pass}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("pass") { type = NavType.StringType; defaultValue = "" }
            ),
            enterTransition  = { slideInHorizontally(tween(320)) { it } },
            exitTransition   = { slideOutHorizontally(tween(320)) { -it } },
        ) { back ->
            val email = back.arguments?.getString("email") ?: ""
            val pass = back.arguments?.getString("pass") ?: ""
            OTPVerifyScreen(
                navController = navController,
                shoshinRepository = shoshinRepository,
                email = email,
                password = pass,
                mode = OtpMode.Email
            )
        }

        composable(
            route     = ShRoutes.ONBOARDING,
            arguments = listOf(navArgument("pageIndex") { type = NavType.IntType }),
            enterTransition  = { slideInHorizontally(tween(320)) { it } },
            exitTransition   = { slideOutHorizontally(tween(320)) { -it } },
        ) { back ->
            val idx = back.arguments?.getInt("pageIndex") ?: 0
            OnboardingScreen(
                index  = idx,
                onNext = {
                    if (idx < 2) navController.navigate(ShRoutes.onboarding(idx + 1))
                    else navController.navigate(ShRoutes.PERMISSIONS) {
                        popUpTo(ShRoutes.onboarding(0)) { inclusive = true }
                    }
                },
                onSkip = {
                    navController.navigate(ShRoutes.PERMISSIONS) {
                        popUpTo(ShRoutes.onboarding(0)) { inclusive = true }
                    }
                },
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
                userRepository = userRepository
            )
        }

        // ── Profile ──────────────────────────────────────────
        composable(ShRoutes.PROFILE) {
            val profileViewModel: ProfileViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(userRepository) as T
                }
            })
            ProfileScreen(navController = navController, viewModel = profileViewModel)
        }

        // ── Edit Profile ─────────────────────────────────────
        composable(ShRoutes.EDIT_PROFILE) {
            val profileViewModel: ProfileViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(userRepository) as T
                }
            })
            EditProfileScreen(navController = navController, viewModel = profileViewModel)
        }

        // ── Settings ─────────────────────────────────────────
        composable(ShRoutes.SETTINGS) {
            val settingsViewModel: SettingsViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
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
            val clockViewModel: BackwardsClockViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
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
            CreateGroupScreen(navController = navController)
        }

        // ── Group Detail ─────────────────────────────────────
        composable(
            route = ShRoutes.GROUP_DETAIL,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType }),
            enterTransition  = { slideInHorizontally(tween(320)) { it } },
            exitTransition   = { slideOutHorizontally(tween(320)) { -it } },
        ) { back ->
            val groupId = back.arguments?.getString("groupId") ?: ""
            GroupDetailScreen(navController = navController, groupId = groupId)
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
            ),
            enterTransition = { fadeIn(tween(400)) },
            exitTransition  = { fadeOut(tween(300)) },
        ) { back ->
            val idx   = back.arguments?.getInt("checkpointIndex") ?: 0
            val label = back.arguments?.getString("checkpointLabel") ?: ""
            CameraVerificationScreen(
                checkpointIndex = idx,
                label           = label,
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
                onPhotoRequired = { idx, label ->
                    navController.navigate(ShRoutes.cameraVerify(idx, label))
                },
                onComplete = {
                    navController.navigate(ShRoutes.MORNING_COMPLETE) {
                        popUpTo(ShRoutes.CHECKPOINT) { inclusive = true }
                    }
                },
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
            )
        }
    }
}
