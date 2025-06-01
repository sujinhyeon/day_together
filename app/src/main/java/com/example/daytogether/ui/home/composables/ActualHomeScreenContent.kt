package com.example.daytogether.ui.home.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.daytogether.data.model.CalendarEvent
import com.example.daytogether.data.model.WeeklyCalendarDay
import com.example.daytogether.ui.theme.ScreenBackground // 테마 색상
import com.example.daytogether.ui.theme.TextPrimary // 테마 색상
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.runtime.remember

// LocalDate.yearMonth 확장 프로퍼티 (필요하다면 유지)
val LocalDate.yearMonth: YearMonth
    get() = YearMonth.of(this.year, this.month)

@OptIn(ExperimentalFoundationApi::class) // WeeklyCalendarView 등 내부 Composable에서 필요할 수 있음
@Composable
fun ActualHomeScreenContent(
    upcomingAnniversaryText: String,
    dDayText: String,
    dDayTitle: String,
    randomCloudResIds: List<Int>,
    currentYearMonth: YearMonth,
    isMonthlyView: Boolean,
    selectedDateForDetails: LocalDate?,
    dateForBorderOnly: LocalDate?,
    eventsByDate: Map<LocalDate, List<CalendarEvent>>,
    weeklyCalendarData: List<WeeklyCalendarDay>,
    isQuestionAnsweredByAll: Boolean,
    aiQuestion: String,
    familyQuote: String,
    showAddEventInputScreen: Boolean,
    isBottomBarVisible: Boolean,
    onMonthChange: (YearMonth) -> Unit,
    onDateClick: (LocalDate?) -> Unit,
    onToggleCalendarView: () -> Unit, // 주간/월간 뷰 전환 (주로 주간 뷰에서 월간으로 갈 때 사용)
    onMonthlyCalendarHeaderTitleClick: () -> Unit, // 월간 뷰 헤더 클릭 (주간으로 전환)
    onMonthlyCalendarHeaderIconClick: () -> Unit, // 월간 뷰 타임피커 아이콘 클릭
    onRefreshQuestionClicked: () -> Unit,
    onMonthlyTodayButtonClick: () -> Unit, // 월간 뷰 '오늘' 버튼 클릭
    onEditEventRequest: (LocalDate, CalendarEvent) -> Unit,
    onDeleteEventRequest: (LocalDate, CalendarEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val displayYearMonth = if (isMonthlyView) currentYearMonth else YearMonth.now()
    val displayYearMonthFormatted = remember(displayYearMonth) {
        DateTimeFormatter.ofPattern("yyyy년 MM월", Locale.KOREAN).format(displayYearMonth)
    }

    Box(modifier = modifier.fillMaxSize()) { // 전체 화면을 채우는 Box
        Column(
            modifier = Modifier
                .fillMaxSize() // Box의 크기를 모두 사용
                .background(ScreenBackground)
        ) {
            // --- 상단 고정 영역 ---
            AnniversaryBoard(text = upcomingAnniversaryText)
            Spacer(modifier = Modifier.height(24.dp))
            DDaySectionView(
                dDayText = dDayText,
                dDayTitle = dDayTitle,
                cloudImageResList = randomCloudResIds,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // --- 중간 영역: 캘린더 또는 주간 뷰 (이 영역이 남은 공간을 차지) ---
            if (isMonthlyView) {
                // MonthlyCalendarView는 여기서 직접 weight를 받아 확장됩니다.
                // 년월 표시 헤더는 MonthlyCalendarView 내부에 있으므로 별도 Spacer나 Text 불필요.
                MonthlyCalendarView(
                    currentMonth = currentYearMonth,
                    onMonthChange = onMonthChange,
                    onDateClick = onDateClick,
                    eventsByDate = eventsByDate,
                    selectedDateForDetails = selectedDateForDetails,
                    dateForBorderOnly = dateForBorderOnly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // << 핵심: 사용 가능한 수직 공간을 모두 차지
                        .padding(horizontal = 20.dp), // 캘린더 좌우 패딩은 여기에 적용
                    onEditEventRequest = onEditEventRequest,
                    onDeleteEventRequest = onDeleteEventRequest,
                    onTitleClick = onMonthlyCalendarHeaderTitleClick,
                    onCalendarIconClick = onMonthlyCalendarHeaderIconClick,
                    onTodayHeaderButtonClick = onMonthlyTodayButtonClick
                )
            } else { // 주간 뷰
                Column(
                    modifier = Modifier
                        .weight(1f) // 주간 뷰 영역도 남은 공간을 차지하도록 설정 (필요에 따라 조절)
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = displayYearMonthFormatted,
                        color = TextPrimary,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 23.sp),
                        modifier = Modifier
                            .align(Alignment.Start)
                            .clickable { onToggleCalendarView() }
                            .padding(bottom = 16.dp)
                    )
                    WeeklyCalendarView(
                        weeklyCalendarData = weeklyCalendarData,
                        modifier = Modifier.fillMaxWidth() // 주간 뷰 자체는 내용만큼의 높이
                    )
                }
            }

            // --- 하단 고정 영역 (캘린더 또는 주간 뷰 아래, 명언 위) ---
            Column(modifier = Modifier.padding(horizontal = 20.dp)) { // 하단 UI들을 그룹화
                Spacer(modifier = Modifier.height(24.dp))
                TodayQuestionHeaderWithAlert(isAnsweredByAll = isQuestionAnsweredByAll)
                Spacer(modifier = Modifier.height(12.dp))
                TodayQuestionContentCard(questionText = aiQuestion)
                Spacer(modifier = Modifier.height(18.dp))
                RefreshQuestionButton(
                    isAnsweredByAll = isQuestionAnsweredByAll,
                    onRefreshQuestionClicked = onRefreshQuestionClicked,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // --- 최하단 명언 뷰 ---
            val showQuote = !isMonthlyView && !isBottomBarVisible && !showAddEventInputScreen
            if (showQuote) {
                QuoteView(
                    quote = familyQuote,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(20.dp + (MaterialTheme.typography.bodyMedium.fontSize.value.dp * 2)))
            }
        } // End of main content Column
    } // End of Box
}