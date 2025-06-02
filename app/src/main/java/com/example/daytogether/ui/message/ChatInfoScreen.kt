package com.example.daytogether.ui.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.daytogether.R
import com.example.daytogether.ui.theme.*
import androidx.compose.ui.tooling.preview.Preview


data class FamilyMember(
    val id: String,
    val name: String,
    val profileImageRes: Int = R.drawable.ic_profile_placeholder
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInfoScreen(navController: NavController) {
    val myUserName = "사용자 이름"
    val creationDate = "2024.01.01"
    val familyMembers = listOf(
        FamilyMember("1", "엄마"),
        FamilyMember("2", "아빠"),
        FamilyMember("3", "동생"),
        FamilyMember("4", "누나"),
        FamilyMember("5", "형")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로 가기",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ScreenBackground)
            )
        },
        containerColor = ScreenBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_profile_placeholder),
                        contentDescription = "내 프로필 이미지",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = myUserName,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = GothicA1)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "하루함께 개설일",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontFamily = GothicA1)
                    )
                    Text(
                        text = creationDate,
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary, fontFamily = GothicA1, fontWeight = FontWeight.Medium)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Divider(color = TextPrimary, thickness = 1.dp)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_group),
                        contentDescription = "가족 멤버 아이콘",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "참여 중인 가족 멤버",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = GothicA1)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = TextPrimary, thickness = 1.dp)
            }

            items(familyMembers) { member ->
                FamilyMemberItem(member = member)
                Divider(color = TextPrimary, thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                // "위 구분선" 삭제됨
                // "아래 구분선"이 텍스트 위로 이동
                Divider(color = TextPrimary, thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* TODO: 초대하기 로직 구현 */ }
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_message_invite), // 여기에 SVG 아이콘 리소스 ID 사용
                        contentDescription = "하루함께 초대하기 아이콘",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp) // 아이콘 크기 조절
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "하루함께 초대하기",
                        style = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary, fontFamily = GothicA1, fontWeight = FontWeight.Bold)
                    )
                }
                // 원래 "아래 구분선"이었던 것은 위로 갔으므로, 여기에는 더 이상 Divider가 없음
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun FamilyMemberItem(member: FamilyMember) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = member.profileImageRes),
            contentDescription = "${member.name} 프로필 이미지",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = member.name,
            style = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary, fontFamily = GothicA1)
        )
    }
}

@Preview(showBackground = true, name = "ChatInfoScreen Preview")
@Composable
fun ChatInfoScreenPreview() {
    DaytogetherTheme {
        ChatInfoScreen(navController = rememberNavController())
    }
}