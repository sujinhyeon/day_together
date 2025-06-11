package com.example.day_together

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.day_together.ui.theme.Day_togetherTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Day_togetherTheme {
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

