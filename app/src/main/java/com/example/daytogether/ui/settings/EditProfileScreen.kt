package com.example.daytogether.ui.settings // 또는 ui.settings 등 적절한 패키지

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.daytogether.R
import com.example.daytogether.ui.theme.* // 테마 전체 임포트
import com.example.daytogether.ui.auth.FamilyMemberSelection

// EditProfileScreen 상태 Preview용 데이터 클래스
data class EditProfileScreenState(
    // 기존 사용자 정보 (읽기 전용 또는 초기값)
    val currentName: String = "홍길동",
    val currentBirthDate: String = "20000101",
    val currentIsLunar: Boolean = false,
    val currentEmail: String = "user@example.com", // 수정 불가
    val currentFamilySelections: Map<String, Boolean> = mapOf("아버지" to true),
    val currentOtherFamilyText: String = "",

    // 변경할 값들
    var newName: String = currentName,
    var newBirthDate: String = currentBirthDate,
    var newIsLunar: Boolean = currentIsLunar,
    var oldPassword: String = "",
    var newPassword: String = "",
    var confirmNewPassword: String = "",
    var newFamilySelections: Map<String, Boolean> = currentFamilySelections,
    var newOtherFamilyText: String = currentOtherFamilyText,

    val simulateChangesMade: Boolean = false // 변경사항 발생 시뮬레이션
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    // 실제로는 ViewModel에서 사용자 정보를 가져와 initialState를 구성해야 함
    initialState: EditProfileScreenState = EditProfileScreenState()
) {
    // 수정 가능한 상태들
    var name by remember { mutableStateOf(initialState.newName) }
    var birthDate by remember { mutableStateOf(initialState.newBirthDate) }
    var isLunarCalendar by remember { mutableStateOf(initialState.newIsLunar) }
    // 이메일은 일반적으로 수정 불가로 표시
    val email = initialState.currentEmail

    var oldPassword by remember { mutableStateOf(initialState.oldPassword) }
    var newPassword by remember { mutableStateOf(initialState.newPassword) }
    var confirmNewPassword by remember { mutableStateOf(initialState.confirmNewPassword) }

    val familyMembers = listOf("할아버지", "할머니", "아버지", "어머니", "아들", "딸")
    var familyMemberSelections by remember { mutableStateOf(initialState.newFamilySelections.ifEmpty { familyMembers.associateWith { false } }) }
    var otherFamilyMemberChecked by remember { mutableStateOf(initialState.newOtherFamilyText.isNotBlank()) }
    var otherFamilyMemberText by remember { mutableStateOf(initialState.newOtherFamilyText) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    LaunchedEffect(otherFamilyMemberChecked) {
        if (!otherFamilyMemberChecked) {
            otherFamilyMemberText = ""
        }
    }

    val isFamilyMemberSelected = familyMemberSelections.values.any { it } || (otherFamilyMemberChecked && otherFamilyMemberText.isNotBlank())

    // "완료" 버튼 활성화 조건:
    // 1. 어떤 정보든 변경되었거나
    // 2. 비밀번호 변경 시도가 있고 (세 필드 모두 입력), 새 비밀번호와 확인이 일치하는 경우
    val hasInfoChanged = name != initialState.currentName ||
            birthDate != initialState.currentBirthDate ||
            isLunarCalendar != initialState.currentIsLunar ||
            familyMemberSelections != initialState.currentFamilySelections ||
            (otherFamilyMemberChecked && otherFamilyMemberText != initialState.currentOtherFamilyText) ||
            (!otherFamilyMemberChecked && initialState.currentOtherFamilyText.isNotBlank())


    val isPasswordChangeAttemptValid = oldPassword.isNotBlank() &&
            newPassword.length >= 8 &&
            confirmNewPassword == newPassword

    val isPasswordChangeAttemptInvalid = (oldPassword.isNotBlank() || newPassword.isNotBlank() || confirmNewPassword.isNotBlank()) && !isPasswordChangeAttemptValid


    val isCompleteButtonEnabled = (hasInfoChanged || (oldPassword.isNotBlank() && isPasswordChangeAttemptValid)) &&
            name.isNotBlank() && birthDate.length == 8 && isFamilyMemberSelected &&
            !isPasswordChangeAttemptInvalid // 비밀번호 변경 시도 중 유효하지 않으면 버튼 비활성화


    DaytogetherTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { /* 제목 없음 */ },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "뒤로가기", tint = TextPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = ScreenBackground)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ScreenBackground)
                    .padding(innerPadding)
                    .padding(horizontal = 32.dp, vertical = 16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_profile_placeholder),
                    contentDescription = "프로필 이미지",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable { /* TODO: 프로필 이미지 변경 로직 */ }
                )
                Spacer(modifier = Modifier.height(32.dp))

                // 입력 필드들
                EditProfileTextField(label = "이름", value = name, onValueChange = { name = it }, imeAction = ImeAction.Next, focusManager = focusManager)

                // 생년월일 (SignUpScreen과 동일 UI)
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("생년월일", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium, fontSize = 13.sp), color = TextPrimary, modifier = Modifier.padding(end = 12.dp))
                        SolarLunarCheckbox(text = "양력", checked = !isLunarCalendar, onCheckedChange = { if (it) isLunarCalendar = false })
                        Spacer(modifier = Modifier.width(16.dp))
                        SolarLunarCheckbox(text = "음력", checked = isLunarCalendar, onCheckedChange = { if (it) isLunarCalendar = true })
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = birthDate, onValueChange = { if (it.length <= 8) birthDate = it },
                        placeholder = { Text("ex)20040506", color = TextPrimary.copy(alpha = 0.6f), fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth().height(52.dp), singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary, fontSize = 15.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors( /* ... */ )
                    )
                }

                EditProfileTextField(label = "ID(Email)", value = email, onValueChange = { /* 이메일은 수정 불가 */ }, imeAction = ImeAction.Next, focusManager = focusManager, readOnly = true) // 읽기 전용
                EditProfileTextField(label = "기존 Password", value = oldPassword, onValueChange = { oldPassword = it }, keyboardType = KeyboardType.Password, isPassword = true, imeAction = ImeAction.Next, focusManager = focusManager, placeholder = "변경 시에만 입력")
                EditProfileTextField(label = "변경할 Password", value = newPassword, onValueChange = { newPassword = it }, keyboardType = KeyboardType.Password, isPassword = true, imeAction = ImeAction.Next, focusManager = focusManager, placeholder = "영문,숫자,특수기호 포함 8자 이상")
                EditProfileTextField(label = "변경할 Password 확인", value = confirmNewPassword, onValueChange = { confirmNewPassword = it }, keyboardType = KeyboardType.Password, isPassword = true, imeAction = ImeAction.Done, focusManager = focusManager, onDone = { focusManager.clearFocus() })

                Spacer(modifier = Modifier.height(24.dp))

                // 가족 구성원 선택 (SignUpScreen과 동일한 Composable 사용)
                FamilyMemberSelection(
                    title = "가족 구성원 중 나는?",
                    members = familyMembers,
                    selections = familyMemberSelections,
                    onSelectionChange = { member, isSelected ->
                        familyMemberSelections = familyMemberSelections.toMutableMap().apply { this[member] = isSelected }
                    },
                    otherChecked = otherFamilyMemberChecked,
                    onOtherCheckedChange = { otherFamilyMemberChecked = it },
                    otherText = otherFamilyMemberText,
                    onOtherTextChange = { otherFamilyMemberText = it },
                    focusManager = focusManager
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { /* TODO: 정보 수정 완료 로직 */ },
                    enabled = isCompleteButtonEnabled,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonActiveBackground,
                        contentColor = ButtonActiveText,
                        disabledContainerColor = ButtonDisabledBackground,
                        disabledContentColor = TextPrimary.copy(alpha = 0.7f)
                    )
                ) {
                    Text("완료", style = MaterialTheme.typography.labelLarge)
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = { /* TODO: 회원 탈퇴 로직 */ }) {
                    Text(
                        "회원탈퇴",
                        style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.Underline, color = TextPrimary.copy(alpha = 0.7f))
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun EditProfileTextField( // SignUpTextField와 유사하지만 readOnly 옵션 추가
    label: String, value: String, onValueChange: (String) -> Unit,
    placeholder: String? = null, keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false, imeAction: ImeAction,
    focusManager: FocusManager,
    onDone: (() -> Unit)? = null,
    readOnly: Boolean = false // 읽기 전용 옵션
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium, fontSize = 13.sp),
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = readOnly, // 읽기 전용 설정
            placeholder = { if (placeholder != null) Text(placeholder, color = TextPrimary.copy(alpha = 0.6f), fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = if (readOnly) TextPrimary.copy(alpha = 0.7f) else TextPrimary, fontSize = 15.sp), // 읽기 전용일 때 텍스트 색상 연하게
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                onDone = { onDone?.invoke() ?: focusManager.clearFocus() }
            ),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), // 읽기 전용(비활성) 시 테두리
                cursorColor = MaterialTheme.colorScheme.primary,
                // 읽기 전용일 때 배경색 등도 조절 가능
                disabledTextColor = TextPrimary.copy(alpha = 0.7f),
                disabledLabelColor = TextPrimary.copy(alpha = 0.5f),
                disabledPlaceholderColor = TextPrimary.copy(alpha = 0.4f)
            ),
            enabled = !readOnly // enabled 상태도 readOnly에 따라 설정
        )
    }
}

// SolarLunarCheckbox 컴포저블 (SignUpScreen.kt에서 복사 또는 공통 모듈로 분리)
@Composable
private fun SolarLunarCheckbox(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onCheckedChange(!checked) }
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.size(20.dp),
            colors = CheckboxDefaults.colors( /* ... */ )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp), color = TextPrimary)
    }
}

// FamilyMemberSelection 및 FamilyMemberItem 컴포저블 (SignUpScreen.kt에서 복사 또는 공통 모듈로 분리)
// const val MAX_FAMILY_MEMBER_OTHER_LENGTH = 10 // 이미 SignUpScreen에 있다면 중복 선언 X
// @Composable
// fun FamilyMemberSelection(...) { ... }
// @Composable
// private fun FamilyMemberItem(...) { ... }


// --- Preview 함수들 ---
class EditProfileScreenStateProvider : PreviewParameterProvider<EditProfileScreenState> {
    override val values = sequenceOf(
        EditProfileScreenState(), // 기본 상태
        EditProfileScreenState( // 정보 변경 및 비밀번호 변경 시도 (유효)
            newName = "김길동",
            oldPassword = "oldPassword123",
            newPassword = "newPassword456!",
            confirmNewPassword = "newPassword456!",
            simulateChangesMade = true
        ),
        EditProfileScreenState( // 정보 변경 없고, 비밀번호 변경 시도 (새 비밀번호 불일치)
            oldPassword = "oldPassword123",
            newPassword = "newPassword456!",
            confirmNewPassword = "wrongConfirm"
        )
    )
}

@Preview(showBackground = true, name = "Edit Profile Screen States", widthDp = 390, heightDp = 844)
@Composable
fun EditProfileScreenAllStatesPreview(
    @PreviewParameter(EditProfileScreenStateProvider::class) state: EditProfileScreenState
) {
    DaytogetherTheme {
        EditProfileScreen(
            navController = rememberNavController(),
            initialState = state
        )
    }
}