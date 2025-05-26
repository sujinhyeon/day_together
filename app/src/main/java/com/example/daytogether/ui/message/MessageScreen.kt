package com.example.daytogether.ui.message

import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.daytogether.R
import com.example.daytogether.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// --- MessageScreen: 메인 컴포저블 ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var showSearchBar by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showAttachmentOptions by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("") }
    var searchText by remember { mutableStateOf("") }

    val currentCalendar = Calendar.getInstance()
    var selectedYear by remember { mutableStateOf(currentCalendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(currentCalendar.get(Calendar.MONTH)) } // 0-11
    var selectedDate by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            MessageTopBar(
                showSearchBar = showSearchBar,
                searchText = searchText,
                onSearchTextChanged = { searchText = it },
                onToggleSearchBar = { showSearchBar = !showSearchBar; if(showSearchBar) showDatePicker = false },
                onBack = { navController.popBackStack() },
                onCalendarClick = { showDatePicker = !showDatePicker; if(showDatePicker) showSearchBar = false },
                onMoreOptionsClick = { /* TODO: 더보기 메뉴 로직 */ }
            )
        },
        containerColor = ScreenBackground
    ) { innerPadding ->
        Box(modifier = modifier.padding(innerPadding).fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                PinnedChatMessage()
                ChatMessagesList(
                    modifier = Modifier.weight(1f),
                    messages = sampleMessages
                )
                MessageInputArea(
                    text = messageText,
                    onTextChanged = { messageText = it },
                    onClipClick = { showAttachmentOptions = !showAttachmentOptions },
                    onSendClick = {
                        if (messageText.isNotBlank()) {
                            // TODO: 메시지 전송 로직
                            messageText = ""
                        }
                    }
                )
            }

            if (showDatePicker) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable { showDatePicker = false }
                ) {
                    MessageDatePicker(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 56.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                            .background(ScreenBackground)
                            .padding(16.dp),
                        currentYear = selectedYear,
                        currentMonth = selectedMonth,
                        selectedDate = selectedDate,
                        onDateSelected = { year, month, day ->
                            selectedDate = day
                            showDatePicker = false
                        },
                        onMonthChange = { year, month ->
                            selectedYear = year
                            selectedMonth = month
                        },
                        onDismiss = { showDatePicker = false }
                    )
                }
            }

            if (showAttachmentOptions) {
                AttachmentOptionsPanel(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    onDismiss = { showAttachmentOptions = false },
                    onAlbumClick = { /* TODO: 앨범 */ },
                    onCameraClick = { /* TODO: 카메라 */ },
                    onFileClick = { /* TODO: 파일 */ },
                    onVoiceMessageClick = { /* TODO: 음성메시지 */ }
                )
            }
        }
    }
}

// --- TopBar ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageTopBar(
    showSearchBar: Boolean,
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onToggleSearchBar: () -> Unit,
    onBack: () -> Unit,
    onCalendarClick: () -> Unit,
    onMoreOptionsClick: () -> Unit
) {
    TopAppBar(
        title = { /* 제목 없음 */ },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }
        },
        actions = {
            if (showSearchBar) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = onSearchTextChanged,
                    placeholder = { Text("내용 검색하기", style = TextStyle(fontSize = 13.sp, color = TextPrimary.copy(alpha = 0.7f), fontFamily = GothicA1)) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ScreenBackground.copy(alpha = 0.5f),
                        unfocusedContainerColor = ScreenBackground.copy(alpha = 0.5f),
                        disabledContainerColor = ScreenBackground.copy(alpha = 0.3f),
                        errorContainerColor = ScreenBackground.copy(alpha = 0.5f),

                        focusedBorderColor  = TextPrimary,
                        unfocusedBorderColor= NavIconUnselected,

                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        disabledTextColor = TextPrimary.copy(alpha = 0.5f),

                        cursorColor = TextPrimary
                    ),
                    textStyle = TextStyle(fontSize = 14.sp, color = TextPrimary, fontFamily = GothicA1),
                    singleLine = true
                )
                TextButton(onClick = {
                    onToggleSearchBar()
                    // TODO: 검색 실행 로직
                }) {
                    Text("확인", color = TextPrimary, style = TextStyle(fontSize = 14.sp, fontFamily = GothicA1, fontWeight = FontWeight.Medium))
                }
            } else {
                IconButton(onClick = onCalendarClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar),
                        contentDescription = "Calendar",
                        tint = TextPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
                IconButton(onClick = onToggleSearchBar) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Search",
                        tint = TextPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
                IconButton(onClick = onMoreOptionsClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_more_options),
                        contentDescription = "More Options",
                        tint = TextPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = ScreenBackground)
    )
}

// --- 고정 챗봇 메시지 ---
@Composable
fun PinnedChatMessage() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp)
            .background(AnniversaryBoardBackground.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_cloud_happy),
            contentDescription = "Chatbot Avatar",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "챗봇",
                style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontFamily = GothicA1)
            )
            Text(
                text = "오늘은 아빠의 생일! 오늘 뭐 할거야?",
                color = TextPrimary,
                style = TextStyle(fontSize = 14.sp, fontFamily = GothicA1),
                modifier = Modifier.wrapContentHeight(align = Alignment.CenterVertically)
            )
        }
        Text(
            text = "19:05",
            color = TextPrimary.copy(alpha = 0.7f),
            style = TextStyle(fontSize = 11.sp, fontFamily = GothicA1),
        )
    }
}

// --- 채팅 메시지 데이터 클래스 및 샘플 ---
data class MessageItem(val id: Int, val text: String, val time: String, val isSentByMe: Boolean, val senderName: String = "사용자")

val sampleMessages = listOf(
    MessageItem(1, "여보 생일 축하해 ~~ 저녁에 맛있는거 먹자!", "20:00", false, "챗봇 아님"),
    MessageItem(2, "아빠 생일 축하해~~", "20:05", true),
    MessageItem(3, "고마워! 저녁 기대할게!", "20:05", false, "챗봇 아님"),
    MessageItem(4, "케이크도 사갈까?", "20:05", true)
)

// --- 채팅 메시지 목록 ---
@Composable
fun ChatMessagesList(modifier: Modifier = Modifier, messages: List<MessageItem>) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        reverseLayout = true,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(messages.reversed()) { message ->
            ChatMessageBubble(message)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

// --- 개별 채팅 버블 ---
@Composable
fun ChatMessageBubble(message: MessageItem) {
    val horizontalAlignment = if (message.isSentByMe) Alignment.End else Alignment.Start
    val bubbleColor = if (message.isSentByMe) ButtonActiveBackground else AnniversaryBoardBackground.copy(alpha = 0.7f)
    val textColor = if (message.isSentByMe) ButtonActiveText else TextPrimary
    val timeColor = TextPrimary.copy(alpha = 0.7f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isSentByMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (message.isSentByMe) {
            Text(
                text = message.time,
                style = TextStyle(fontSize = 10.sp, color = timeColor, fontFamily = GothicA1),
                modifier = Modifier.padding(end = 6.dp)
            )
        }
        if (!message.isSentByMe) {
            Image(
                painter = painterResource(id = R.drawable.ic_profile_placeholder),
                contentDescription = "${message.senderName} Avatar",
                modifier = Modifier.size(30.dp).clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column {
            if (!message.isSentByMe) {
                Text(
                    text = message.senderName,
                    style = TextStyle(fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium, fontFamily = GothicA1),
                    modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
                )
            }
            Box(
                modifier = Modifier
                    .background(bubbleColor, RoundedCornerShape(
                        topStart = if (message.isSentByMe) 16.dp else 4.dp,
                        topEnd = if (message.isSentByMe) 4.dp else 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    ))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .widthIn(max = LocalConfiguration.current.screenWidthDp.dp * 0.7f)
            ) {
                Text(text = message.text, style = TextStyle(fontSize = 14.sp, color = textColor, fontFamily = GothicA1))
            }
        }

        if (!message.isSentByMe) {
            Text(
                text = message.time,
                style = TextStyle(fontSize = 10.sp, color = timeColor, fontFamily = GothicA1),
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}


// --- 메시지 입력창 ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInputArea(
    text: String,
    onTextChanged: (String) -> Unit,
    onClipClick: () -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ScreenBackground,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClipClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_clip_attach),
                    contentDescription = "Attach file",
                    tint = TextPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
            OutlinedTextField(
                value = text,
                onValueChange = onTextChanged,
                modifier = Modifier.weight(1f).heightIn(min = 48.dp, max = 120.dp),
                placeholder = { Text("메시지 입력", style = TextStyle(fontSize = 14.sp, color = NavIconUnselected, fontFamily = GothicA1)) },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Brown100.copy(alpha = 0.5f),
                    unfocusedContainerColor = Brown100.copy(alpha = 0.5f),
                    disabledContainerColor = Brown100.copy(alpha = 0.3f),
                    errorContainerColor = Brown100.copy(alpha = 0.5f),

                    focusedBorderColor = TextPrimary,
                    unfocusedBorderColor = Color.Transparent,

                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    disabledTextColor = TextPrimary.copy(alpha = 0.5f),

                    cursorColor = TextPrimary,

                ),
                textStyle = TextStyle(fontSize = 14.sp, color = TextPrimary, fontFamily = GothicA1)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onSendClick, enabled = text.isNotBlank()) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_send_arrow),
                    contentDescription = "Send message",
                    tint = if (text.isNotBlank()) ButtonActiveBackground else NavIconUnselected,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

// --- 첨부파일 옵션 패널 ---
@Composable
fun AttachmentOptionsPanel(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onAlbumClick: () -> Unit,
    onCameraClick: () -> Unit,
    onFileClick: () -> Unit,
    onVoiceMessageClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .background(ScreenBackground),
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.width(40.dp).height(4.dp).background(NavIconUnselected, CircleShape))
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AttachmentOptionItem(iconResId = R.drawable.ic_gallery, label = "앨범", onClick = onAlbumClick)
                AttachmentOptionItem(iconResId = R.drawable.ic_camera, label = "카메라", onClick = onCameraClick)
                AttachmentOptionItem(iconResId = R.drawable.ic_file, label = "파일", onClick = onFileClick)
                AttachmentOptionItem(iconResId = R.drawable.ic_mic, label = "음성메시지", onClick = onVoiceMessageClick)
            }
        }
    }
}

@Composable
fun AttachmentOptionItem(iconResId: Int, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(AnniversaryBoardBackground.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(painter = painterResource(id = iconResId), contentDescription = label, tint = TextPrimary, modifier = Modifier.size(30.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, style = TextStyle(fontSize = 12.sp, color = TextPrimary, fontFamily = GothicA1))
    }
}


// --- 날짜 선택기 ---
@Composable
fun MessageDatePicker(
    modifier: Modifier = Modifier,
    currentYear: Int,
    currentMonth: Int,
    selectedDate: Int?,
    onDateSelected: (Int, Int, Int) -> Unit,
    onMonthChange: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = remember { Calendar.getInstance() }
    LaunchedEffect(currentYear, currentMonth) {
        calendar.set(Calendar.YEAR, currentYear)
        calendar.set(Calendar.MONTH, currentMonth)
    }

    val monthName = SimpleDateFormat("MMMM", Locale.KOREAN).format(calendar.time)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeekInMonth = calendar.get(Calendar.DAY_OF_WEEK)
    val emptySlots = (firstDayOfWeekInMonth - Calendar.SUNDAY + 7) % 7

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = ScreenBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    calendar.add(Calendar.MONTH, -1)
                    onMonthChange(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))
                }) {
                    Icon(painterResource(id = R.drawable.ic_custom_arrow_left), "Previous Month", tint = TextPrimary)
                }
                Text("${currentYear}년 $monthName", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = GothicA1)) // 수정됨
                IconButton(onClick = {
                    calendar.add(Calendar.MONTH, 1)
                    onMonthChange(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))
                }) {
                    Icon(painterResource(id = R.drawable.ic_custom_arrow_right), "Next Month", tint = TextPrimary)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                listOf("일", "월", "화", "수", "목", "금", "토").forEach { dayLabel ->
                    Text(dayLabel, style = TextStyle(fontSize = 12.sp, color = TextPrimary, fontFamily = GothicA1, textAlign = TextAlign.Center), modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            val totalSlots = daysInMonth + emptySlots
            val numRows = (totalSlots + 6) / 7

            for (week in 0 until numRows) {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceAround) {
                    for (dayPositionInWeek in 0 until 7) {
                        val dayIndex = week * 7 + dayPositionInWeek
                        if (dayIndex >= emptySlots && dayIndex < totalSlots) {
                            val day = dayIndex - emptySlots + 1
                            val isSelected = day == selectedDate
                            val hasConversation = (day % 3 == 0)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSelected -> SelectedMonthlyBorder
                                            else -> Color.Transparent // androidx.compose.ui.graphics.Color 사용
                                        }
                                    )
                                    .border(
                                        width = if (isSelected) 0.dp else if (hasConversation) 1.5.dp else 0.dp,
                                        color = if (hasConversation && !isSelected) SelectedMonthlyBorder.copy(alpha=0.7f) else Color.Transparent, // androidx.compose.ui.graphics.Color 사용
                                        shape = CircleShape
                                    )
                                    .clickable(enabled = hasConversation) { onDateSelected(currentYear, currentMonth, day) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.toString(),
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        fontFamily = GothicA1,
                                        color = when {
                                            isSelected -> ScreenBackground
                                            hasConversation -> TextPrimary
                                            else -> OtherMonthDayText
                                        },
                                        fontWeight = if (isSelected || hasConversation) FontWeight.Medium else FontWeight.Normal
                                    )
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonActiveBackground),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("확인", color = ButtonActiveText, style = TextStyle(fontFamily = GothicA1, fontSize = 14.sp, fontWeight = FontWeight.Medium))
            }
        }
    }
}

// --- Preview ---
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun MessageScreenPreview() {
    DaytogetherTheme {
        val navController = rememberNavController()
        MessageScreen(navController = navController)
    }
}