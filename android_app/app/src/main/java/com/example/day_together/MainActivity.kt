package com.example.day_together



import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.day_together.ui.navigation.Routes
import com.example.day_together.ui.home.HomeScreen
import com.example.day_together.navigation.AppNavigation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.day_together.ui.theme.Day_togetherTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Day_togetherTheme {
                AppNavigation(
                )
            }
        }
    }
}



@Composable
fun AppNavigationHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            HomeScreen(appNavController = navController)
        }
        composable(Routes.MESSAGE) {
            Text("메세지 화면입니다.")
        }
        composable(Routes.GALLERY) {
            Text("갤러리 화면입니다.")
        }
        composable(Routes.SETTINGS) {
            Text("설정 화면입니다.")

        }
    }
}