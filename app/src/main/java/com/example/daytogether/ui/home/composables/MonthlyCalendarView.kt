package com.example.daytogether.ui.home.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday // 달력 아이콘은 Material Icon 유지
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.daytogether.ui.theme.TextPrimary
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.ui.res.painterResource
import com.example.daytogether.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items // LazyColumn 용 items (EventDetailsDialog 내부)
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.daytogether.data.model.CalendarEvent // CalendarEvent 모델 경로
import com.example.daytogether.ui.theme.* // 앱 테마 및 색상 경로
import java.time.LocalDate
import java.time.DayOfWeek as JavaDayOfWeek // DayOfWeek 이름 충돌 방지

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.remember



@Composable
internal fun MonthlyCalendarHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onTitleClick: () -> Unit,           // 년월 텍스트 클릭 시 (뷰 토글)
    onCalendarIconClick: () -> Unit, // 왼쪽 달력 아이콘 클릭 시 (타임피커 열기)
    onTodayHeaderButtonClick: () -> Unit, // 새로 추가된 "오늘" 버튼 클릭 시
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월", Locale.KOREAN)
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 왼쪽 달력 아이콘: 클릭 시 onCalendarIconClick (타임피커) 호출
        IconButton(onClick = onCalendarIconClick, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.Filled.CalendarToday, // Material Icon 사용
                contentDescription = "년/월 선택",
                tint = TextPrimary
            )
        }

        // 이전 달, 년월 텍스트, 다음 달 부분
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onPreviousMonth, modifier = Modifier.size(36.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_custom_arrow_left),
                    contentDescription = "이전 달",
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = currentMonth.format(formatter),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 23.sp),
                color = TextPrimary,
                modifier = Modifier
                    .clickable { onTitleClick() } // 년월 텍스트 클릭 시 뷰 토글
                    .padding(horizontal = 4.dp)
            )
            IconButton(onClick = onNextMonth, modifier = Modifier.size(36.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_custom_arrow_right),
                    contentDescription = "다음 달",
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // "오늘" TextButton 추가
        TextButton(onClick = onTodayHeaderButtonClick, modifier = Modifier.height(36.dp)) {
            Text("오늘", color = TextPrimary, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
        }
    }
}


// 월간 캘린더의 각 날짜 셀에 표시될 정보를 담는 데이터 클래스
data class MonthlyCalendarCellData(
    val date: LocalDate?,
    val isCurrentMonth: Boolean,
    var events: List<CalendarEvent> = emptyList()
)

// 주어진 년도와 월에 대한 캘린더 그리드 데이터를 생성하는 함수
private fun getDaysForMonthlyCalendarGrid(
    yearMonth: YearMonth,
    allEvents: Map<LocalDate, List<CalendarEvent>>
): List<MonthlyCalendarCellData?> {
    val firstDayOfMonth = yearMonth.atDay(1) // 해당 달의 1일
    val daysInMonth = yearMonth.lengthOfMonth()

    // --- "일요일 시작" 기준으로 daysToPrepend 계산 ---
    // JavaDayOfWeek enum: MONDAY(1), TUESDAY(2), ..., SATURDAY(6), SUNDAY(7)
    // 우리 그리드의 첫 번째 열은 일요일 (인덱스 0)입니다.
    // firstDayOfMonth.dayOfWeek.value가 SUNDAY(7)이면, daysToPrepend는 0이 되어야 합니다.
    // firstDayOfMonth.dayOfWeek.value가 MONDAY(1)이면, daysToPrepend는 1이 되어야 합니다 (일요일 칸 1개 채움).
    // ...
    // firstDayOfMonth.dayOfWeek.value가 SATURDAY(6)이면, daysToPrepend는 6이 되어야 합니다.
    // 즉, (요일값 % 7)을 사용합니다. (일요일 7 % 7 = 0, 월요일 1 % 7 = 1, ..., 토요일 6 % 7 = 6)
    val daysToPrepend = firstDayOfMonth.dayOfWeek.value % 7
    // --- 계산 수정 완료 ---

    val calendarDays = mutableListOf<MonthlyCalendarCellData?>()

    // 이전 달의 날짜들 추가
    if (daysToPrepend > 0) { // 1일이 일요일이라 앞에 채울 날짜가 없는 경우(daysToPrepend == 0)는 이 루프를 실행하지 않음.
        val prevMonth = yearMonth.minusMonths(1)
        val daysInPrevMonth = prevMonth.lengthOfMonth()
        for (i in 0 until daysToPrepend) {
            // 이전 달의 마지막 날부터 거꾸로 채우는 것이 아니라,
            // 이전 달의 (마지막 날 - daysToPrepend + 1 + i) 번째 날짜를 가져옵니다.
            val date = prevMonth.atDay(daysInPrevMonth - daysToPrepend + 1 + i)
            calendarDays.add(MonthlyCalendarCellData(date, false, allEvents[date] ?: emptyList()))
        }
    }

    // 현재 달의 날짜들 추가
    for (day in 1..daysInMonth) {
        val date = yearMonth.atDay(day)
        calendarDays.add(MonthlyCalendarCellData(date, true, allEvents[date] ?: emptyList()))
    }

    // 다음 달의 날짜들 추가 (사용자님 요청대로 35칸 고정)
    val totalCellsToDisplay = 35
    val currentCellCount = calendarDays.size
    if (currentCellCount < totalCellsToDisplay) {
        val nextMonth = yearMonth.plusMonths(1)
        for (day in 1..(totalCellsToDisplay - currentCellCount)) {
            val date = nextMonth.atDay(day)
            calendarDays.add(MonthlyCalendarCellData(date, false, allEvents[date] ?: emptyList()))
        }
    }
    // 만약 정확히 35칸을 보장하고 싶다면, 마지막에 take(totalCellsToDisplay)를 할 수 있지만,
    // 위 로직은 이미 35칸을 목표로 채우므로 초과하거나 부족한 경우는 거의 없습니다.
    // 하지만 안전하게 하려면 return calendarDays.take(totalCellsToDisplay)
    return calendarDays
}

@OptIn(ExperimentalMaterial3Api::class) // MonthlyCalendarView에 필요할 수 있음
@Composable
fun MonthlyCalendarView(
    currentMonth: YearMonth,
    onMonthChange: (YearMonth) -> Unit,
    onDateClick: (LocalDate?) -> Unit, // HomeScreen에서 수정된 로직을 가진 콜백
    eventsByDate: Map<LocalDate, List<CalendarEvent>>,
    selectedDateForDetails: LocalDate?, // 팝업을 위한 선택 날짜 (HomeScreen에서 관리)
    dateForBorderOnly: LocalDate?,      // '테두리만 표시'를 위한 날짜 (HomeScreen에서 관리, 새로 추가)
    modifier: Modifier = Modifier,
    onEditEventRequest: (LocalDate, CalendarEvent) -> Unit,
    onDeleteEventRequest: (LocalDate, CalendarEvent) -> Unit,
    onTitleClick: () -> Unit,
    onCalendarIconClick: () -> Unit,
    onTodayHeaderButtonClick: () -> Unit // HomeScreen에서 수정된 로직을 가진 콜백
) {
    val today = LocalDate.now()
    // currentMonth나 eventsByDate가 변경될 때만 daysInGrid를 다시 계산
    val daysInGrid: List<MonthlyCalendarCellData?> = remember(currentMonth, eventsByDate.toMap()) {
        println("Recalculating daysInGrid for $currentMonth")
        getDaysForMonthlyCalendarGrid(currentMonth, eventsByDate)
    }

    Column(
        modifier = modifier
    ) {
        MonthlyCalendarHeader(
            currentMonth = currentMonth,
            onPreviousMonth = {
                val newMonth = currentMonth.minusMonths(1)
                onMonthChange(newMonth)
                // onDateClick(null) // 달 변경 시 선택 상태 초기화는 HomeScreen의 onMonthChange에서 처리
            },
            onNextMonth = {
                val newMonth = currentMonth.plusMonths(1)
                onMonthChange(newMonth)
                // onDateClick(null) // 달 변경 시 선택 상태 초기화는 HomeScreen의 onMonthChange에서 처리
            },
            onTitleClick = onTitleClick,
            onCalendarIconClick = onCalendarIconClick,
            onTodayHeaderButtonClick = onTodayHeaderButtonClick,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        DayOfWeekHeaderMonthly(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(6.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Absolute.spacedBy(0.dp), // 셀 사이 간격 없음
            verticalArrangement = Arrangement.spacedBy(4.dp) // 주(행) 사이 간격
        ) {
            items(daysInGrid.size) { index ->
                val dayInfo = daysInGrid[index]
                MonthlyDayCell(
                    date = dayInfo?.date,
                    isCurrentMonth = dayInfo?.isCurrentMonth ?: false,
                    isToday = dayInfo?.date == today,
                    // MonthlyDayCell에 전달할 Boolean 값들
                    showPopupHighlight = dayInfo?.date != null && dayInfo.date == selectedDateForDetails,
                    showBorderOnlyHighlight = dayInfo?.date != null && dayInfo.date == dateForBorderOnly,
                    events = dayInfo?.events ?: emptyList(),
                    onClick = {
                        // 현재 달의 날짜만 클릭 가능하도록 유지
                        if (dayInfo?.date != null && dayInfo.isCurrentMonth) {
                            onDateClick(dayInfo.date)
                        }
                    }
                )
            }
        }
    }
}



@Composable
private fun MonthlyCalendarHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onTitleClick: () -> Unit,
    onCalendarIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월", Locale.KOREAN)
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onCalendarIconClick, modifier = Modifier.size(36.dp)) {
            // Unresolved reference 오류를 피하기 위해 Material Icon으로 변경
            // 사용자 정의 SVG 'ic_calendar_today'가 있다면 아래 painterResource 코드를 사용하세요.
            Icon(
                imageVector = Icons.Filled.CalendarToday,
                contentDescription = "오늘 날짜 달로 이동",
                tint = TextPrimary
            )

        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onPreviousMonth, modifier = Modifier.size(36.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_custom_arrow_left), // 사용자 SVG
                    contentDescription = "이전 달",
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = currentMonth.format(formatter),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 23.sp),
                color = TextPrimary,
                modifier = Modifier
                    .clickable { onTitleClick() }
                    .padding(horizontal = 4.dp)
            )
            IconButton(onClick = onNextMonth, modifier = Modifier.size(36.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_custom_arrow_right), // 사용자 SVG
                    contentDescription = "다음 달",
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Box(modifier = Modifier.size(36.dp)) // "오늘" 버튼 자리 유지용 빈 박스
    }
}


@Composable
private fun DayOfWeekHeaderMonthly(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp) // 전체 헤더의 좌우 패딩
            .padding(vertical = 4.dp),    // 전체 헤더의 상하 패딩
        horizontalArrangement = Arrangement.SpaceAround // 각 요일 Text를 균등하게 배치 (weight(1f)와 함께 사용 시 효과)
        // 또는 Arrangement.Start를 사용해도 weight 때문에 동일 효과
    ) {
        listOf("일", "월", "화", "수", "목", "금", "토").forEach { day ->
            Text(
                text = day,
                textAlign = TextAlign.Center, // <<< 요일 텍스트는 각 셀 내에서 가운데 정렬
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                modifier = Modifier.weight(1f) // 각 요일이 동일한 너비를 차지
            )
        }
    }
}

@Composable
private fun MonthlyDayCell(
    date: LocalDate?,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    showPopupHighlight: Boolean,
    showBorderOnlyHighlight: Boolean,
    events: List<CalendarEvent>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cellHeight = 90.dp
    val textColor = if (!isCurrentMonth) OtherMonthDayText else TextPrimary

    val actualBorderColor: Color
    val actualBorderWidth: Dp
    val actualBackgroundColor: Color

    if (showBorderOnlyHighlight) {
        actualBorderColor = SelectedMonthlyBorder
        actualBorderWidth = 1.5.dp
        actualBackgroundColor = if (isToday && isCurrentMonth) TodayMonthlyBackground else Color.Transparent
    } else if (showPopupHighlight) {
        actualBorderColor = SelectedMonthlyBorder
        actualBorderWidth = 1.5.dp
        actualBackgroundColor = SelectedMonthlyBorder.copy(alpha = 0.2f)
    } else {
        actualBorderColor = Color.Transparent
        actualBorderWidth = 0.dp
        actualBackgroundColor = if (isToday && isCurrentMonth) TodayMonthlyBackground else Color.Transparent
    }

    Box(
        modifier = modifier
            .height(cellHeight)
            .clip(RoundedCornerShape(4.dp))
            .background(actualBackgroundColor)
            .border(BorderStroke(actualBorderWidth, actualBorderColor), RoundedCornerShape(4.dp))
            .clickable(enabled = date != null && isCurrentMonth) { onClick() }
            .padding(horizontal = 3.dp, vertical = 4.dp),
        contentAlignment = Alignment.TopStart
    ) {
        if (date != null) {
            Column(
                modifier = Modifier.fillMaxHeight()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (isToday && isCurrentMonth) FontWeight.ExtraBold else FontWeight.Normal,
                            fontSize = 12.sp
                        ),
                        color = if (isToday && isCurrentMonth) TextPrimary else textColor,
                        modifier = Modifier
                    )
                    // ★ 2. 이벤트가 4개 초과 시 +N 표시 ★
                    if (isCurrentMonth && events.size > 4) { // 3에서 4로 변경
                        Text(
                            text = "+${events.size - 4}", // 3에서 4로 변경
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.sp),
                            textAlign = TextAlign.End
                        )
                    }
                }

                if (isCurrentMonth) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        // ★ 2. 최대 4개의 이벤트 표시 ★
                        events.take(4).forEach { event ->
                            Text(
                                text = event.description,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(2.dp))
                                    .padding(horizontal = 2.dp, vertical = 0.5.dp)
                            )
                        }
                    }
                } else {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

// 상세 일정 목록 Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsDialog(
    date: LocalDate,
    events: List<CalendarEvent>,
    onDismiss: () -> Unit,
    onEditEvent: (CalendarEvent) -> Unit,
    onDeleteEvent: (CalendarEvent) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E)", Locale.KOREAN)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                date.format(formatter),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            if (events.isEmpty()) {
                Text("등록된 일정이 없습니다.", modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth(), textAlign = TextAlign.Center)
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 240.dp)) { // 일정이 많을 경우 스크롤
                    items(items = events, key = { event -> event.id }) { event ->
                        var showMenu by remember { mutableStateOf(false) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* 각 이벤트 클릭 시 동작 정의 가능 */ }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(event.description, modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Box {
                                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(36.dp)) {
                                    Icon(Icons.Filled.MoreVert, contentDescription = "더보기", tint = TextPrimary)
                                }
                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("수정") },
                                        onClick = {
                                            onEditEvent(event)
                                            showMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("삭제") },
                                        onClick = {
                                            onDeleteEvent(event)
                                            showMenu = false
                                        }
                                    )
                                }
                            }
                        }
                        HorizontalDivider(color = TextPrimary.copy(alpha = 0.1f))
                    }
                }
            }
        },
        confirmButton = { /* 비워둠 (타이틀과 내용만 표시) */ },
        dismissButton = { /* 비워둠 */ }
    )
}


@Preview(showBackground = true, name = "월간 캘린더 뷰 (전체)", widthDp = 390)
@Composable
fun MonthlyCalendarViewFullPreview() {
    DaytogetherTheme {
        val today = LocalDate.now()
        // 각 CalendarEvent 생성 시 'date' 파라미터에 해당 날짜를 전달합니다.
        val dummyEvents = mapOf(
            today.plusDays(1) to listOf(
                CalendarEvent(description = "회의", date = today.plusDays(1)),
                CalendarEvent(description = "점심 약속", date = today.plusDays(1)),
                CalendarEvent(description = "저녁 식사", date = today.plusDays(1)),
                CalendarEvent(description = "추가 일정", date = today.plusDays(1))
            ),
            today.plusDays(2) to listOf(CalendarEvent(description = "발표 준비 데드라인", date = today.plusDays(2))),
            today.plusDays(3) to listOf(
                CalendarEvent(description = "스터디", date = today.plusDays(3)),
                CalendarEvent(description = "운동", date = today.plusDays(3)),
                CalendarEvent(description = "장보기 목록 작성", date = today.plusDays(3)),
                CalendarEvent(description = "친구와 저녁 약속", date = today.plusDays(3)),
                CalendarEvent(description = "도서관 책 반납", date = today.plusDays(3))
            ),
            today to listOf(
                CalendarEvent(description = "오늘의 할일 1", date = today),
                CalendarEvent(description = "오늘의 할일 2", date = today),
                CalendarEvent(description = "오늘의 할일 3", date = today),
                CalendarEvent(description = "오늘의 할일 4 (길게 써보기 테스트으으으으으으으으으)", date = today),
                CalendarEvent(description = "오늘의 할일 5", date = today)
            ),
            today.minusDays(2) to listOf(CalendarEvent(description = "지난 일정", date = today.minusDays(2)))
        )

        var currentMonthPreview by remember { mutableStateOf(YearMonth.now()) }
        var selectedDateForDetailsPreview by remember { mutableStateOf<LocalDate?>(null) }
        var dateForBorderOnlyPreview by remember { mutableStateOf<LocalDate?>(null) }

        Column(Modifier.background(Color.White)) { // 예시로 ScreenBackground 대신 Color.White 사용
            MonthlyCalendarView(
                currentMonth = currentMonthPreview,
                onMonthChange = { newMonth ->
                    currentMonthPreview = newMonth
                    selectedDateForDetailsPreview = null
                    dateForBorderOnlyPreview = null
                    println("Month changed to: $newMonth")
                },
                onDateClick = { clickedDate ->
                    if (clickedDate == null) return@MonthlyCalendarView
                    if (clickedDate == today) {
                        if (dateForBorderOnlyPreview == today) {
                            selectedDateForDetailsPreview = today
                            dateForBorderOnlyPreview = null
                        } else {
                            selectedDateForDetailsPreview = null
                            dateForBorderOnlyPreview = today
                        }
                    } else {
                        selectedDateForDetailsPreview = clickedDate
                        dateForBorderOnlyPreview = null
                    }
                    println("Date clicked: $clickedDate, BorderFor: $dateForBorderOnlyPreview, PopupFor: $selectedDateForDetailsPreview")
                },
                eventsByDate = dummyEvents,
                selectedDateForDetails = selectedDateForDetailsPreview,
                dateForBorderOnly = dateForBorderOnlyPreview,
                onEditEventRequest = { date: LocalDate, event: CalendarEvent ->
                    println("Edit event $event on $date requested")
                },
                onDeleteEventRequest = { date: LocalDate, event: CalendarEvent ->
                    println("Delete event $event on $date requested")
                },
                onTitleClick = { println("Month Title clicked (for view toggle)") },
                onCalendarIconClick = { println("Calendar Icon clicked (for time picker)") },
                onTodayHeaderButtonClick = {
                    currentMonthPreview = YearMonth.now()
                    selectedDateForDetailsPreview = null
                    dateForBorderOnlyPreview = LocalDate.now()
                    println("Today Header Button Clicked. BorderFor: $dateForBorderOnlyPreview, PopupFor: $selectedDateForDetailsPreview")
                }
            )
            Text("Selected for Details (Popup): ${selectedDateForDetailsPreview?.toString() ?: "None"}")
            Text("Border Only For (Today first click): ${dateForBorderOnlyPreview?.toString() ?: "None"}")
        }
    }
}