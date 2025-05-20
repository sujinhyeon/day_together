package com.example.daytogether.data.model

import java.time.LocalDate // LocalDate를 사용하기 위해 import 합니다.
import java.util.UUID

data class CalendarEvent(
    val id: String = UUID.randomUUID().toString(),
    val description: String,
    val date: LocalDate // 홈 화면에서 사용하는 날짜 정보를 저장할 필드
    // 기타 필요한 필드들
)