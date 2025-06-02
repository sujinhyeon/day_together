package com.example.daytogether.ui.home

import com.example.daytogether.ui.message.MessageScreen
import androidx.compose.foundation.layout.Arrangement
import com.example.daytogether.ui.gallery.GalleryScreen
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.daytogether.R
import com.example.daytogether.ui.navigation.BottomNavItem
import com.example.daytogether.ui.settings.SettingsScreen
import com.example.daytogether.ui.theme.DaytogetherTheme
import com.example.daytogether.ui.theme.NavIconSelected
import com.example.daytogether.ui.theme.NavIconUnselected
import com.example.daytogether.ui.theme.TextPrimary
import com.example.daytogether.data.model.CalendarEvent
import com.example.daytogether.data.model.WeeklyCalendarDay
import com.example.daytogether.ui.home.composables.ActualHomeScreenContent
import com.example.daytogether.ui.home.composables.AddEventInputView
import com.example.daytogether.ui.home.composables.DateEventsBottomSheet
import com.example.daytogether.ui.message.ChatInfoScreen
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import java.util.UUID
import java.time.DayOfWeek as JavaDayOfWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(appNavController: NavController) {
    val mainNavController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Message,
        BottomNavItem.Gallery,
        BottomNavItem.Settings
    )

    var upcomingAnniversaryText by remember { mutableStateOf("D-3 샘플 기념일!") }
    var dDayTextState by remember { mutableStateOf("D-3") }
    var dDayTitleState by remember { mutableStateOf("샘플 기념일") }
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
    var isMonthlyView by remember { mutableStateOf(false) }
    var selectedDateForDetails by remember { mutableStateOf<LocalDate?>(null) }
    var dateForBorderOnly by remember { mutableStateOf<LocalDate?>(null) }
    val today = LocalDate.now()
    val eventsByDateState = remember { mutableStateMapOf<LocalDate, List<CalendarEvent>>() }
    val weeklyCalendarDataState = remember(today, eventsByDateState, isMonthlyView) {
        if(!isMonthlyView) {
            val firstDayOfRelevantWeek = today.with(JavaDayOfWeek.MONDAY)
            (0 until 7).map { dayOffset ->
                val date = firstDayOfRelevantWeek.plusDays(dayOffset.toLong())
                WeeklyCalendarDay(
                    date = date.dayOfMonth.toString(),
                    dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN),
                    isToday = date.isEqual(today),
                    events = eventsByDateState[date] ?: emptyList()
                )
            }
        } else {
            emptyList()
        }
    }
    var isQuestionAnsweredByAllState by remember { mutableStateOf(false) }
    var aiQuestionState by remember { mutableStateOf("AI 질문 예시입니다.") }
    var familyQuoteState by remember { mutableStateOf("\"가족 명언 예시입니다.\"") }
    val randomCloudResIds = remember { listOf(R.drawable.cloud1, R.drawable.cloud2) }

    var showAddEventSheet by remember { mutableStateOf(false) }
    var eventToEdit by remember { mutableStateOf<CalendarEvent?>(null) }
    var dateForNewEvent by remember { mutableStateOf<LocalDate?>(null) }
    var showHomeDatePickerDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val sampleDate1 = LocalDate.now().plusDays(1)
        eventsByDateState[sampleDate1] = listOf(
            CalendarEvent(id = "1", description = "점심 약속 🍔", date = sampleDate1),
            CalendarEvent(id = "2", description = "프로젝트 회의 💻", date = sampleDate1)
        )
        val sampleDate2 = LocalDate.now()
        eventsByDateState[sampleDate2] = listOf(
            CalendarEvent(id = "3", description = "오늘 할 일!", date = sampleDate2)
        )
    }

    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != BottomNavItem.Message.route && currentRoute != "chat_info_screen_route") {
                Column {
                    Divider(color = TextPrimary.copy(alpha = 0.2f), thickness = 0.5.dp)
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                        val currentDestination = navBackStackEntry?.destination
                        bottomNavItems.forEach { screen ->
                            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                            NavigationBarItem(
                                icon = { Icon(imageVector = ImageVector.vectorResource(id = screen.iconResId), contentDescription = screen.label, tint = if (isSelected) NavIconSelected else NavIconUnselected) },
                                selected = isSelected,
                                onClick = {
                                    mainNavController.navigate(screen.route) {
                                        popUpTo(mainNavController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                alwaysShowLabel = false,
                                colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = mainNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                Box(modifier = Modifier.fillMaxSize()) {
                    ActualHomeScreenContent(
                        upcomingAnniversaryText = upcomingAnniversaryText,
                        dDayText = dDayTextState,
                        dDayTitle = dDayTitleState,
                        randomCloudResIds = randomCloudResIds,
                        currentYearMonth = currentYearMonth,
                        isMonthlyView = isMonthlyView,
                        selectedDateForDetails = selectedDateForDetails,
                        dateForBorderOnly = dateForBorderOnly,
                        eventsByDate = eventsByDateState,
                        weeklyCalendarData = weeklyCalendarDataState,
                        isQuestionAnsweredByAll = isQuestionAnsweredByAllState,
                        aiQuestion = aiQuestionState,
                        familyQuote = familyQuoteState,
                        showAddEventInputScreen = showAddEventSheet,
                        isBottomBarVisible = !showAddEventSheet && selectedDateForDetails == null,
                        onMonthChange = { newMonth -> currentYearMonth = newMonth },
                        onDateClick = { dateClicked ->
                            if (dateClicked != null) {
                                selectedDateForDetails = dateClicked
                                dateForBorderOnly = dateClicked
                                showAddEventSheet = false
                                eventToEdit = null
                                dateForNewEvent = null
                            } else {
                                selectedDateForDetails = null
                                dateForBorderOnly = null
                            }
                        },
                        onToggleCalendarView = { isMonthlyView = !isMonthlyView },
                        onMonthlyCalendarHeaderTitleClick = { isMonthlyView = false },
                        onMonthlyCalendarHeaderIconClick = {
                            if(isMonthlyView) {
                                showHomeDatePickerDialog = true
                            }
                        },
                        onRefreshQuestionClicked = { /* TODO: ViewModel */ },
                        onMonthlyTodayButtonClick = {
                            val todayDate = LocalDate.now()
                            currentYearMonth = YearMonth.from(todayDate)
                            selectedDateForDetails = todayDate
                            dateForBorderOnly = todayDate
                            showAddEventSheet = false
                        },
                        onEditEventRequest = { date, event ->
                            dateForNewEvent = date
                            eventToEdit = event
                            showAddEventSheet = true
                            selectedDateForDetails = null
                        },
                        onDeleteEventRequest = { date, event ->
                            eventsByDateState[date] = eventsByDateState[date]?.filterNot { it.id == event.id } ?: emptyList()
                            println("캘린더에서 직접 삭제 요청: $event on $date")
                        }
                    )

                    if (selectedDateForDetails != null && !showAddEventSheet) {
                        DateEventsBottomSheet(
                            visible = true,
                            targetDate = selectedDateForDetails!!,
                            events = eventsByDateState[selectedDateForDetails!!] ?: emptyList(),
                            onDismiss = {
                                selectedDateForDetails = null
                                dateForBorderOnly = null
                            },
                            onAddNewEventClick = {
                                dateForNewEvent = selectedDateForDetails
                                eventToEdit = null
                                showAddEventSheet = true
                                selectedDateForDetails = null
                                dateForBorderOnly = null
                            },
                            onEditEvent = { eventToEditFromSheet ->
                                dateForNewEvent = selectedDateForDetails
                                eventToEdit = eventToEditFromSheet
                                showAddEventSheet = true
                                selectedDateForDetails = null
                                dateForBorderOnly = null
                            },
                            onDeleteEvent = { eventToDelete ->
                                val currentEvents = eventsByDateState[selectedDateForDetails!!]?.toMutableList()
                                currentEvents?.remove(eventToDelete)
                                if (currentEvents != null) {
                                    eventsByDateState[selectedDateForDetails!!] = currentEvents
                                }
                                println("바텀시트에서 삭제: $eventToDelete")
                                if (eventsByDateState[selectedDateForDetails!!].isNullOrEmpty()) {
                                    selectedDateForDetails = null
                                    dateForBorderOnly = null
                                }
                            }
                        )
                    }

                    if (showAddEventSheet && dateForNewEvent != null) {
                        AddEventInputView(
                            visible = true,
                            targetDate = dateForNewEvent!!,
                            eventDescription = eventToEdit?.description ?: "",
                            isEditing = eventToEdit != null,
                            onDescriptionChange = { newDescription ->
                                println("입력값 변경: $newDescription")
                            },
                            onSave = {
                                val newDescription = eventToEdit?.description ?: "새로운 일정 내용"
                                if (eventToEdit != null) {
                                    println("수정 저장: ${eventToEdit!!.id} - $newDescription")
                                    val ScurrentEvents = eventsByDateState[dateForNewEvent!!]?.toMutableList()
                                    val index = ScurrentEvents?.indexOfFirst { it.id == eventToEdit!!.id }
                                    if (index != null && index != -1) {
                                        ScurrentEvents?.set(index, eventToEdit!!.copy(description = newDescription))
                                        eventsByDateState[dateForNewEvent!!] = ScurrentEvents!!
                                    }
                                } else {
                                    val newEvent = CalendarEvent(
                                        id = UUID.randomUUID().toString(),
                                        description = newDescription,
                                        date = dateForNewEvent!!
                                    )
                                    val currentEvents = eventsByDateState[dateForNewEvent!!]?.toMutableList() ?: mutableListOf()
                                    currentEvents.add(newEvent)
                                    eventsByDateState[dateForNewEvent!!] = currentEvents
                                    println("새 일정 저장: $newEvent")
                                }
                                showAddEventSheet = false
                                eventToEdit = null
                                dateForNewEvent = null
                            },
                            onCancel = {
                                showAddEventSheet = false
                                eventToEdit = null
                                dateForNewEvent = null
                            }
                        )
                    }

                    if (showHomeDatePickerDialog) {
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = currentYearMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        )
                        DatePickerDialog(
                            onDismissRequest = { showHomeDatePickerDialog = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        val selectedLocalDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                                        currentYearMonth = YearMonth.from(selectedLocalDate)
                                        selectedDateForDetails = null
                                        dateForBorderOnly = null
                                    }
                                    showHomeDatePickerDialog = false
                                }) { Text("확인") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showHomeDatePickerDialog = false }) { Text("취소") }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }
                }
            }
            composable(BottomNavItem.Message.route) {
                MessageScreen(navController = mainNavController)
            }
            composable(BottomNavItem.Gallery.route) {
                GalleryScreen(navController = mainNavController)
            }
            composable(BottomNavItem.Settings.route) {
                SettingsScreen(navController = appNavController)
            }
            composable("chat_info_screen_route") {
                ChatInfoScreen(navController = mainNavController)
            }
        }
    }
}

@Preview(showBackground = true, name = "전체 홈 화면 (Scaffold 포함)", widthDp = 390, heightDp = 844)
@Composable
fun FullHomeScreenPreview() {
    DaytogetherTheme {
        HomeScreen(appNavController = rememberNavController())
    }
}