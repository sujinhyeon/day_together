package com.example.daytogether.ui.home.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.daytogether.data.model.CalendarEvent
import com.example.daytogether.ui.theme.TextPrimary // 실제 사용하는 테마 경로로 수정 필요
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// EVENT_ITEM_APPROX_HEIGHT와 DIVIDER_AND_PADDING_HEIGHT는 필요시 유지하거나,
// weight 기반 레이아웃에서는 덜 중요해질 수 있습니다.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateEventsBottomSheet(
    visible: Boolean,
    targetDate: LocalDate,
    events: List<CalendarEvent>,
    onDismiss: () -> Unit,
    onAddNewEventClick: () -> Unit,
    onEditEvent: (CalendarEvent) -> Unit,
    onDeleteEvent: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier // 이 modifier는 HomeScreen.kt에서 ModalBottomSheetLayout의 sheetContent에 적용될 때 사용될 수 있습니다.
) {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E)", Locale.KOREAN)
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(animationSpec = tween(durationMillis = 250), initialOffsetY = { it }),
        exit = slideOutVertically(animationSpec = tween(durationMillis = 200), targetOffsetY = { it }),
        modifier = modifier.fillMaxWidth() // BottomSheet는 보통 화면 너비를 꽉 채웁니다.
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                // 최소 화면 높이의 40% (예시), 최대 화면 높이의 90%를 차지하도록 설정
                // 내용이 적어도 최소 높이를 보장하고, 내용이 많으면 최대 90%까지 커짐
                .heightIn(min = screenHeight * 0.4f, max = screenHeight * 0.9f),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // 또는 원하는 배경색
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight() // Card가 확보한 높이를 Column이 모두 사용하도록 설정
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
            ) {
                // --- 헤더: 날짜 및 닫기 버튼 --- (고정 높이 영역)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = targetDate.format(dateFormatter),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "닫기", tint = TextPrimary.copy(alpha = 0.7f))
                    }
                }
                HorizontalDivider(color = TextPrimary.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(8.dp))

                // --- 내용: 이벤트 목록 또는 "이벤트 없음" 안내 --- (가변 높이, 남은 공간 차지)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // << 핵심! 이 Box가 Column 내에서 사용 가능한 모든 수직 공간을 차지하도록 함
                ) {
                    if (events.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(), // 부모 Box(위의 Box)의 크기를 따름
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "등록된 일정이 없습니다.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextPrimary.copy(alpha = 0.7f)
                            )
                        }
                    } else {
                        LazyColumn(
                            // fillMaxSize()를 사용하면 부모 Box의 크기만큼 차지
                            modifier = Modifier.fillMaxSize()
                        ) {
                            itemsIndexed(items = events, key = { _, event -> event.id }) { index, event ->
                                EventListItem( // EventListItem 컴포저블이 있다고 가정
                                    event = event,
                                    onEditClick = { onEditEvent(event) },
                                    onDeleteClick = { onDeleteEvent(event) }
                                )
                                if (index < events.lastIndex) {
                                    HorizontalDivider(color = TextPrimary.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp)) // 내용과 하단 버튼 사이 간격

                // --- 하단 고정 버튼: 새 일정 만들기 --- (고정 높이 영역)
                Button(
                    onClick = onAddNewEventClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = TextPrimary), // 투명 배경 버튼 예시
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "새 일정 만들기 아이콘")
                    Spacer(Modifier.width(8.dp))
                    Text("새 일정 만들기", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

// EventListItem 컴포저블의 예시 (실제 구현에 맞게 수정 필요)
@Composable
fun EventListItem(event: CalendarEvent, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(event.description, modifier = Modifier.weight(1f))
        // 예시로 수정/삭제 버튼 대신 더보기 메뉴 등을 사용할 수 있습니다.
        Row {
            TextButton(onClick = onEditClick) { Text("수정") }
            TextButton(onClick = onDeleteClick) { Text("삭제") }
        }
    }
}