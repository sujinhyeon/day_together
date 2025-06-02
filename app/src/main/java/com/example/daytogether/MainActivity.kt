package com.example.daytogether

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import com.example.daytogether.ui.theme.DaytogetherTheme
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DaytogetherTheme {
                Scaffold{
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ){
                        Button(
                            onClick = {
                                startActivity(Intent(this@MainActivity, ChatActivity::class.java))
                            }
                        ){
                            Text("채팅하러 가기")
                        }
                    }
                }
            }
        }
    }
}

