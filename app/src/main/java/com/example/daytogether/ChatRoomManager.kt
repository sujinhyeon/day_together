package com.example.daytogether

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

object ChatRoomManager {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun inviteFamilyMembers(
        invitedUserIds: List<String>,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val currentUser = auth.currentUser ?: return onComplete(false, "로그인 정보 없음")

        // 채팅방 생성
        val chatRoomId = UUID.randomUUID().toString()
        val chatRoomData = hashMapOf(
            "chatRoomId" to chatRoomId,
            "createdBy" to currentUser.uid,
            "members" to listOf(currentUser.uid), // 생성자는 자동 추가
            "createdAt" to Date()
        )

        db.collection("chatRooms")
            .document(chatRoomId)
            .set(chatRoomData)
            .addOnSuccessListener {
                // 초대 유저 문서에 초대 정보 저장
                val tasks = invitedUserIds.map { userId ->
                    val inviteData = hashMapOf(
                        "chatRoomId" to chatRoomId,
                        "invitedBy" to currentUser.uid,
                        "status" to "pending",
                        "invitedAt" to Date()
                    )
                    db.collection("users")
                        .document(userId)
                        .collection("invitations")
                        .document(chatRoomId)
                        .set(inviteData)
                }

                // 모든 유저에게 초대 완료 시 콜백
                com.google.android.gms.tasks.Tasks.whenAllSuccess<Void>(tasks)
                    .addOnSuccessListener {
                        onComplete(true, null)
                    }
                    .addOnFailureListener { e ->
                        onComplete(false, "초대 저장 실패: ${e.message}")
                    }

            }
            .addOnFailureListener { e ->
                onComplete(false, "채팅방 생성 실패: ${e.message}")
            }
    }

    // 초대 수락
    fun acceptInvitation(chatRoomId: String, onComplete: (Boolean, String?) -> Unit) {
        val currentUser = auth.currentUser ?: return onComplete(false, "로그인 정보 없음")
        val userId = currentUser.uid

        // 채팅방 members 필드에 유저 추가
        val chatRoomRef = db.collection("chatRooms").document(chatRoomId)

        chatRoomRef.update("members", com.google.firebase.firestore.FieldValue.arrayUnion(userId))
            .addOnSuccessListener {
                // 초대 상태를 accepted로 변경
                db.collection("users")
                    .document(userId)
                    .collection("invitations")
                    .document(chatRoomId)
                    .update("status", "accepted")
                    .addOnSuccessListener {
                        onComplete(true, null)
                    }
                    .addOnFailureListener { e ->
                        onComplete(false, "초대 상태 변경 실패: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                onComplete(false, "채팅방 멤버 추가 실패: ${e.message}")
            }
    }
}
