package com.example.daytogether.ui.theme

import androidx.compose.ui.graphics.Color


val ScreenBackground = Color(0xFFFFF3D9)
val AnniversaryBoardBackground = Color(0xFFCFBA94)
val TextPrimary = Color(0xFF533A28) // 글자 기본 색상 (온보딩 제목, 스플래시 제목 등)

// 버튼 색상 - 활성화
val ButtonActiveText = Color(0xFFFFF3D9)
val ButtonActiveBackground = Color(0xFF533A28)

// 버튼 색상 - 비활성화
val ButtonDisabledText = Color(0xFF747474)
val ButtonDisabledBackground = Color(0xFFDADADA)

// 네비게이션 아이콘 색상
val NavIconSelected = Color(0xFF533A28)
val NavIconUnselected = Color(0xFFCFBA94)

val ErrorRed = Color(0xFFFB1F1F)

// 캘린더 관련 색상
val TodayMonthlyBackground = AnniversaryBoardBackground.copy(alpha = 0.2f)
val SelectedMonthlyBorder = Color(0xFF533A28)
val OtherMonthDayText = Color(0xFFBFBFBF)

val Brown100 = AnniversaryBoardBackground.copy(alpha = 0.1f) // 매우 연한 갈색 배경 등

// 온보딩 페이지
val PagerIndicatorActive = TextPrimary // 활성 상태 점 색상 (예: 짙은 갈색)
val PagerIndicatorInactive = AnniversaryBoardBackground // 비활성 상태 점 색상 (예: 연한 갈색)


