package com.example.daytogether.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.daytogether.ui.theme.DaytogetherTheme
import kotlinx.coroutines.delay

const val SPLASH_TIMEOUT = 2000L // 스플래시 화면 지속 시간 (2초)

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    DaytogetherTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "하루\n함께", // Figma 스플래시 화면 참조
                color = MaterialTheme.colorScheme.primary, // TextPrimary 색상 사용 (Theme.kt에서 primary로 매핑됨)
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayLarge // Type.kt에서 정의한 displayLarge 스타일
            )
        }
    }

    LaunchedEffect(Unit) {
        delay(SPLASH_TIMEOUT)
        onTimeout()
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun SplashScreenPreview() {
    DaytogetherTheme {
        SplashScreen(onTimeout = {})
    }
}