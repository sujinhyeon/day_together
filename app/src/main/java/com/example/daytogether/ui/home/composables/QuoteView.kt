package com.example.daytogether.ui.home.composables

import androidx.compose.foundation.layout.fillMaxWidth
// import androidx.compose.foundation.layout.padding // 필요시 사용
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.daytogether.ui.theme.TextPrimary // 테마 색상 경로 확인

@Composable
fun QuoteView(quote: String, modifier: Modifier = Modifier) {
    Text(
        text = quote,
        color = TextPrimary.copy(alpha = 0.7f),
        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()

    )
}