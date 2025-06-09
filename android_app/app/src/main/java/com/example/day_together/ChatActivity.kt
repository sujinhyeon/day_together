package com.example.day_together

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.day_together.ui.theme.Day_togetherTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.android.gms.tasks.Tasks
import java.util.*

data class ChatMessage(
    val content: String,
    val sender: String,
    val timestamp: Date = Date()
)

class ChatActivity : ComponentActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val messages = mutableStateListOf<ChatMessage>()
    private var currentUserName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인 상태가 아닙니다", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadCurrentUser()
        listenForMessages()

        setContent {
            Day_togetherTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        ChatScreen(
                            messages = messages,
                            onSend = { sendMessage(it) },
                            currentUserName = currentUserName
                        )
                    }
                }
            }
        }
    }

    private fun loadCurrentUser() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("members")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                currentUserName = document.getString("name") ?: "Unknown"
            }
    }

    private fun sendMessage(text: String) {
        if (currentUserName.isBlank()) return

        val message = hashMapOf(
            "sender" to currentUserName,
            "content" to text,
            "timestamp" to Date()
        )
        db.collection("chatRoom").add(message)
    }

    private fun listenForMessages() {
        db.collection("chatRoom")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val newMessages = snapshot.documents.map { doc ->
                        val content = doc.getString("content") ?: ""
                        val sender = doc.getString("sender") ?: "unknown"
                        val timestamp = doc.getDate("timestamp") ?: Date()
                        ChatMessage(content, sender, timestamp)
                    }
                    messages.clear()
                    messages.addAll(newMessages.sortedBy { it.timestamp })
                }
            }
    }

    private fun inviteMembers(
        inviterUserId: String,
        invitedUserId: List<String>,
        onComplete: (Boolean, String?) -> Unit){

        // 채팅방 ID 생성
        val chatRoomRef = db.collection("chatRooms").document()
        val chatRoomId = chatRoomRef.id

        // 채팅방 데이터 구성
        val chatRoomData = hashMapOf(
            "members" to listOf(inviterUserId),  // 초대한 사람만 우선 추가
            "invitedUsers" to invitedUserId,
            "createdAt" to com.google.firebase.Timestamp.now()
        )

        chatRoomRef.set(chatRoomData)
            .addOnSuccessListener {
                val tasks = invitedUserId.map { userId ->
                    db.collection("users")
                        .document(userId)
                        .update("invitedChatRoomId", chatRoomId)
                }

                Tasks.whenAllSuccess<Void>(tasks)
                    .addOnSuccessListener {
                        onComplete(true, null)
                    }
                    .addOnFailureListener { e ->
                        onComplete(false, "유저 업데이트 실패: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                onComplete(false, "채팅방 생성 실패: ${e.message}")
            }
    }
    @Composable
    fun ChatScreen(
        messages: List<ChatMessage>,
        onSend: (String) -> Unit,
        currentUserName: String
    ) {
        var input by remember { mutableStateOf("") }

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(messages.size) { index ->
                    val msg = messages[index]
                    MessageBubble(message = msg, isMine = msg.sender == currentUserName)
                }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f).padding(8.dp),
                    placeholder = { Text("메시지를 입력하세요") }
                )
                Button(onClick = {
                    if (input.isNotBlank()) {
                        onSend(input)
                        input = ""
                    }
                }) {
                    Text("전송")
                }
            }
        }
    }

    @Composable
    fun MessageBubble(message: ChatMessage, isMine: Boolean) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
        ) {
            Text(
                text = message.sender,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Surface(
                color = if (isMine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 2.dp
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(8.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}