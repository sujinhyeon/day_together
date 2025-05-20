package com.example.daytogether.ui.gallery


import java.util.UUID
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.daytogether.R
import com.example.daytogether.ui.theme.*
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class PhotoItem(val id: String, val imageUrl: String, val date: String) // date는 "YYYY-MM-DD" 형식이어야 YearMonth.parse가 정확히 동작합니다.
data class MonthlyPhotoGroupData(val yearMonth: YearMonth, val photos: List<PhotoItem>)
data class MonthlyComment(val id: String = UUID.randomUUID().toString(), val author: String, val text: String, val timestamp: String) // ID 추가

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GalleryScreen(navController: NavController) {
    var currentDisplayYearMonth by remember { mutableStateOf(YearMonth.now()) }
    val yearMonthFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월", Locale.KOREAN)
    var showCommentBottomSheetFor by remember { mutableStateOf<YearMonth?>(null) }
    var showYearMonthPickerDialog by remember { mutableStateOf(false) }

    // 샘플 데이터 (날짜 형식을 YYYY-MM-DD로 가정, 실제 데이터 구조에 맞게 파싱 필요)
    val samplePhotos = listOf(
        PhotoItem("1", "https://picsum.photos/seed/picsum/200/300", "${YearMonth.now().atDay(1)}"),
        PhotoItem("2", "https://picsum.photos/seed/kotlin/200/300", "${YearMonth.now().atDay(3)}"),
        PhotoItem("3", "https://picsum.photos/seed/compose/200/300", "${YearMonth.now().atDay(5)}"),
        PhotoItem("4", "https://picsum.photos/seed/android/200/300", "${YearMonth.now().minusMonths(1).atDay(10)}"),
        PhotoItem("5", "https://picsum.photos/seed/google/200/300", "${YearMonth.now().minusMonths(1).atDay(12)}"),
        PhotoItem("6", "https://picsum.photos/seed/jetpack/200/300", "${YearMonth.now().minusMonths(2).atDay(15)}"),
        PhotoItem("7", "https://picsum.photos/seed/flower/200/300", "${YearMonth.now().minusMonths(2).atDay(20)}"),
    )

    val monthlyPhotoGroups = remember(currentDisplayYearMonth, samplePhotos) {
        samplePhotos
            .filter { YearMonth.from(LocalDate.parse(it.date)) == currentDisplayYearMonth } // LocalDate로 파싱 후 YearMonth 추출
            .groupBy { YearMonth.from(LocalDate.parse(it.date)) }
            .map { MonthlyPhotoGroupData(it.key, it.value) }
            .sortedByDescending { it.yearMonth } // 최신순 정렬
    }

    DaytogetherTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = currentDisplayYearMonth.format(yearMonthFormatter),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    },
                    actions = {
                        // 요청사항 3: 오른쪽 달력 아이콘 클릭 시 DatePickerDialog 표시
                        IconButton(onClick = { showYearMonthPickerDialog = true }) {
                            Icon(
                                imageVector = Icons.Filled.CalendarMonth,
                                contentDescription = "날짜 선택",
                                tint = TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ScreenBackground,
                        titleContentColor = TextPrimary
                    )
                )
            }
        ) { innerPadding ->
            if (monthlyPhotoGroups.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(ScreenBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "이 달에는 공유된 사진이 없어요.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary.copy(alpha = 0.7f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ScreenBackground)
                        .padding(innerPadding)
                        .padding(horizontal = 8.dp)
                ) {
                    items(monthlyPhotoGroups.size) { index ->
                        val group = monthlyPhotoGroups[index]
                        // 요청사항 1 & 2: 실제 MonthlyPhotoGroupItem 사용 (임시 텍스트 삭제, 아이콘 위치 변경은 MonthlyPhotoGroupItem 내부에서)
                        MonthlyPhotoGroupItem(
                            yearMonth = group.yearMonth,
                            photos = group.photos,
                            yearMonthFormatter = yearMonthFormatter,
                            onPhotoClick = { photoId -> /* TODO: 사진 클릭 시 상세 화면 등으로 이동 */ },
                            onCommentIconClick = { yearMonth -> showCommentBottomSheetFor = yearMonth }
                        )

                        if (index < monthlyPhotoGroups.lastIndex) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }

            // 요청사항 3: DatePickerDialog 표시 로직
            if (showYearMonthPickerDialog) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = currentDisplayYearMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                )
                DatePickerDialog(
                    onDismissRequest = { showYearMonthPickerDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val selectedLocalDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                                currentDisplayYearMonth = YearMonth.from(selectedLocalDate)
                            }
                            showYearMonthPickerDialog = false
                        }) {
                            Text("확인")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showYearMonthPickerDialog = false }) {
                            Text("취소")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            showCommentBottomSheetFor?.let { ym ->
                MonthlyCommentBottomSheet(
                    yearMonth = ym,
                    onDismiss = { showCommentBottomSheetFor = null }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyCommentBottomSheet(
    yearMonth: YearMonth,
    onDismiss: () -> Unit
) {
    // 요청사항 4: 메시지 입력 시 반영되도록 comments를 mutableStateListOf로 변경
    val comments = remember(yearMonth) { // yearMonth가 바뀌면 댓글 목록 초기화 (실제로는 ViewModel 필요)
        mutableStateListOf( // mutableStateListOf로 변경
            MonthlyComment(author = "가족1", text = "${yearMonth.monthValue}월 정말 즐거웠어요!", timestamp = "2시간 전"),
            MonthlyComment(author = "가족2", text = "사진 잘 나왔네!", timestamp = "1시간 전")
        )
    }
    var newCommentText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = ScreenBackground,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp) // 바닥 패딩을 조금 줄여서 키보드에 덜 가려지도록
                .imePadding() // 키보드가 올라올 때 UI를 밀어올림
        ) {
            Text(
                text = "${yearMonth.format(DateTimeFormatter.ofPattern("yyyy년 MM월", Locale.KOREAN))}의 한마디",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary),
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp)
            )

            if (comments.isEmpty()) {
                Text(
                    "아직 작성된 한마디가 없어요. 첫 번째 한마디를 남겨보세요!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f, fill = false) // 내용이 적을 때 너무 커지지 않도록
                        .heightIn(max = 200.dp) // 최대 높이 제한
                ) {
                    items(comments.size, key = { comments[it].id }) { index -> // key 추가
                        val comment = comments[index]
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(comment.author, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(comment.timestamp, style = MaterialTheme.typography.labelSmall, color = TextPrimary.copy(alpha = 0.6f))
                            }
                            Text(comment.text, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        }
                        if (index < comments.lastIndex) {
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newCommentText,
                    onValueChange = { newCommentText = it },
                    placeholder = {
                        Text(
                            "이번 달 우리 가족에게 남기는 한마디", // 플레이스홀더 간결하게
                            fontSize = 13.sp, // 기존 유지
                            color = TextPrimary.copy(alpha = 0.6f),
                            maxLines = 1, // 플레이스홀더도 한 줄로
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                    // 요청사항 5: 입력칸 한 줄 유지, 내용 길어지면 가로 스크롤
                    singleLine = true, // 한 줄 입력 필드로 만듦 (자동으로 가로 스크롤 지원)
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (newCommentText.isNotBlank()) {
                            // 요청사항 4: 새 댓글 추가 로직
                            comments.add(0, MonthlyComment(author = "나", text = newCommentText, timestamp = "방금 전")) // 맨 위에 추가
                            newCommentText = ""
                            // TODO: 실제로는 ViewModel을 통해 서버/DB에 저장 후 목록 갱신
                        }
                    }),
                    colors = TextFieldDefaults.colors( // Material 3 스타일
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = ButtonActiveBackground, // 테마 색상 활용
                        unfocusedIndicatorColor = TextPrimary.copy(alpha = 0.3f),
                        focusedLabelColor = TextPrimary,
                        cursorColor = ButtonActiveBackground, // 테마 색상 활용
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary.copy(alpha = 0.8f)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (newCommentText.isNotBlank()) {
                            // 요청사항 4: 새 댓글 추가 로직
                            comments.add(0, MonthlyComment(author = "나", text = newCommentText, timestamp = "방금 전")) // 맨 위에 추가
                            newCommentText = ""
                            // TODO: 실제로는 ViewModel을 통해 서버/DB에 저장 후 목록 갱신
                        }
                    },
                    enabled = newCommentText.isNotBlank()
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_send_arrow),
                        contentDescription = "댓글 전송",
                        tint = if (newCommentText.isNotBlank()) ButtonActiveBackground else TextPrimary.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@Composable
fun PhotoGridItem(photoItem: PhotoItem, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photoItem.imageUrl)
                .crossfade(true)
                .error(R.drawable.ic_placeholder_image) // drawable에 대체 이미지 필요
                .placeholder(R.drawable.ic_placeholder_image) // 로딩 중 표시할 이미지
                .build(),
            contentDescription = "갤러리 사진 ${photoItem.id}",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun MonthlyPhotoGroupItem(
    yearMonth: YearMonth,
    photos: List<PhotoItem>,
    yearMonthFormatter: DateTimeFormatter,
    onPhotoClick: (photoId: String) -> Unit,
    onCommentIconClick: (yearMonth: YearMonth) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        // 요청사항 1: 채팅 아이콘을 "0000년 00월" 텍스트 오른쪽으로 이동
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, start = 8.dp, end = 4.dp), // 아이콘 버튼 패딩 고려 end 4.dp
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                // 요청사항 1: "(N장)" 텍스트 표시 (사진 개수)
                text = "${yearMonth.format(yearMonthFormatter)} (${photos.size}장)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary),
                modifier = Modifier.weight(1f) // 텍스트가 남은 공간 차지
            )
            // Spacer(modifier = Modifier.weight(1f)) // 텍스트와 아이콘 사이 공간 채우기 (텍스트를 왼쪽, 아이콘을 오른쪽에 붙임)
            IconButton(onClick = { onCommentIconClick(yearMonth) }, modifier = Modifier.size(36.dp)) { // 아이콘 버튼 크기 약간 조절
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = "${yearMonth.format(yearMonthFormatter)} 댓글 보기",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp) // 아이콘 자체 크기 약간 조절
                )
            }
        }
        PhotoGrid(photos = photos, onPhotoClick = onPhotoClick)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoGrid(photos: List<PhotoItem>, onPhotoClick: (photoId: String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            // 사진 개수에 따라 높이가 유동적으로 변하도록 heightIn 제거 또는 최대 높이만 설정
            // 또는 각 행의 높이를 고정값으로 계산. 예: 130.dp는 한 행의 높이
            .height(((photos.size + 2) / 3 * 130).dp) // 행 수 * 행 높이 (패딩/간격 고려하여 조절 필요)
            // .heightIn(min = 130.dp) // 최소 한 줄 높이
            .padding(horizontal = 4.dp), // Grid 전체의 좌우 패딩
        contentPadding = PaddingValues(0.dp), // 아이템 간 간격은 Arrangement로 조절
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(photos, key = { it.id }) { photo ->
            PhotoGridItem(photoItem = photo, onClick = { onPhotoClick(photo.id) })
        }
    }
}



@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun GalleryScreenPreview() {
    DaytogetherTheme {
        GalleryScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
fun MonthlyPhotoGroupItemPreview() {
    DaytogetherTheme {
        val samplePhotos = listOf(
            PhotoItem("1", "https://picsum.photos/200", "2025-05-01"),
            PhotoItem("2", "https://picsum.photos/201", "2025-05-03"),
            PhotoItem("3", "https://picsum.photos/202", "2025-05-05")
        )
        MonthlyPhotoGroupItem(
            yearMonth = YearMonth.of(2025, 5),
            photos = samplePhotos,
            yearMonthFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월", Locale.KOREAN),
            onPhotoClick = {},
            onCommentIconClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MonthlyCommentBottomSheetPreview() {
    DaytogetherTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.weight(1f))
            MonthlyCommentBottomSheet(yearMonth = YearMonth.now(), onDismiss = {})
        }
    }
}