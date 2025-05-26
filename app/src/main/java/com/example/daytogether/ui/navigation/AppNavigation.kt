package com.example.daytogether.navigation

import com.example.daytogether.ui.auth.EditProfileScreen
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.daytogether.ui.auth.FindAccountScreen
import com.example.daytogether.ui.auth.LoginScreen
import com.example.daytogether.ui.auth.SignUpScreen
import com.example.daytogether.ui.home.HomeScreen
import com.example.daytogether.ui.onboarding.OnboardingScreen
import com.example.daytogether.ui.splash.SplashScreen


object AppDestinations {
    const val SPLASH_ROUTE = "splash"
    const val ONBOARDING_ROUTE = "onboarding"
    const val LOGIN_ROUTE = "login"
    const val MAIN_ROUTE = "main_graph"
    const val SIGNUP_ROUTE = "signup"
    const val FIND_ACCOUNT_ROUTE = "find_account"
    const val EDIT_PROFILE_ROUTE = "edit_profile"
}

@Composable
fun AppNavigation(

) {
    val navController = rememberNavController()

    // TODO: DataStore 또는 SharedPreferences를 사용하여 실제 '최초 실행 여부'를 관리해야 합니다.
    // 현재는 테스트를 위해 'true'로 고정하여 항상 온보딩이 보이도록 합니다.
    val isFirstLaunch by remember { mutableStateOf(true) }
    Log.d("AppNavigation", "isFirstLaunch 값: $isFirstLaunch")

    // --- UI 개발 중 임시로 메인 화면으로 바로 시작 ---
    val startDestination = AppDestinations.MAIN_ROUTE
    Log.d("AppNavigation", "임시 UI 개발 모드: 시작 지점 = $startDestination")
    // --- 원래 로직 (주석 처리) ---
    // val isFirstLaunch by remember { mutableStateOf(true) }
    // Log.d("AppNavigation", "isFirstLaunch 값: $isFirstLaunch")
    // val startDestination = AppDestinations.SPLASH_ROUTE
    // Log.d("AppNavigation", "AppNavigation NavHost startDestination: $startDestination")
    // --- 원래 로직 끝 ---


    NavHost(navController = navController, startDestination = startDestination) {
        composable(AppDestinations.SPLASH_ROUTE) {
            Log.d("AppNavigation", "Current Route: ${AppDestinations.SPLASH_ROUTE}")
            SplashScreen(
                onTimeout = {
                    // --- UI 개발 중 임시 로직 ---
                    navController.navigate(AppDestinations.MAIN_ROUTE) {
                        popUpTo(AppDestinations.SPLASH_ROUTE) { inclusive = true }
                    }
                    // --- 원래 로직 (주석 처리) ---
                    // val route = if (isFirstLaunch) {
                    //     Log.d("AppNavigation", "SplashScreen Timeout: Navigating to ONBOARDING_ROUTE")
                    //     AppDestinations.ONBOARDING_ROUTE
                    // } else {
                    //     Log.d("AppNavigation", "SplashScreen Timeout: Navigating to MAIN_ROUTE")
                    //     AppDestinations.MAIN_ROUTE
                    // }
                    // navController.navigate(route) {
                    //     popUpTo(AppDestinations.SPLASH_ROUTE) { inclusive = true }
                    // }
                    // --- 원래 로직 끝 ---
                }
            )
        }

        composable(AppDestinations.ONBOARDING_ROUTE) {
            Log.d("AppNavigation", "Current Route: ${AppDestinations.ONBOARDING_ROUTE}")
            OnboardingScreen(navController = navController)
            // 온보딩 완료 후 (예: LoginScreen에서 로그인 성공 시) isFirstLaunch 값을 false로 업데이트하는 로직 필요
            // 그리고 MAIN_ROUTE로 navigate 해야 함. (예: LoginScreen 내부에서 로그인 성공 시)
            // navController.navigate(AppDestinations.MAIN_ROUTE) {
            //     popUpTo(AppDestinations.ONBOARDING_ROUTE) { inclusive = true }
            // }
        }

        // 온보딩 페이저 내의 LoginScreen 외에, 다른 경로로 직접 LoginScreen에 접근해야 할 경우를 위한 라우트
        composable(AppDestinations.LOGIN_ROUTE) {
            Log.d("AppNavigation", "Current Route: ${AppDestinations.LOGIN_ROUTE}")
            LoginScreen(navController = navController, fromOnboarding = false)
        }

        composable(AppDestinations.SIGNUP_ROUTE) {
            Log.d("AppNavigation", "Current Route: ${AppDestinations.SIGNUP_ROUTE}")
            // SignUpScreen Composable 호출
            SignUpScreen(navController = navController)
        }

        composable(AppDestinations.FIND_ACCOUNT_ROUTE) {
            Log.d("AppNavigation", "Current Route: ${AppDestinations.FIND_ACCOUNT_ROUTE}")
            // FindAccountScreen Composable 호출
            FindAccountScreen(navController = navController)
        }

        composable(AppDestinations.MAIN_ROUTE) {
            Log.d("AppNavigation", "Current Route: ${AppDestinations.MAIN_ROUTE}")
            // MainScreen이 아니라 HomeScreen을 호출 (사용자 요청 반영)
            // HomeScreen은 내부에 Scaffold와 BottomNavigation, 그리고 자체 NavHost를 가질 수 있습니다.
            // 또는, HomeScreen 자체가 메인 화면의 "내용"만 담당하고,
            // 하단 네비게이션 바를 포함한 전체적인 프레임은 별도의 Composable(예: MainScaffoldScreen)에서 관리할 수도 있습니다.
            // 현재 MainActivity.kt의 구조를 보면 HomeScreen이 이 역할을 해야 할 것 같습니다.
            HomeScreen(appNavController = navController) // MainActivity의 AppNavigationHost와 유사한 역할을 여기서 해야 함
            // 또는 MainScreen.kt 파일을 만들고 그 안에서 HomeScreen 등을 내부 화면으로 관리
        }
    }
}