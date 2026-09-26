package com.larder.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.larder.app.ui.theme.CreamBase
import com.larder.app.ui.theme.DeepOliveText
import com.larder.app.ui.theme.LarderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LarderTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LarderMainApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun LarderMainApp(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBase),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Larder — Smart Pantry & Grocery",
            color = DeepOliveText,
            fontSize = 20.sp,
            style = MaterialTheme.typography.titleLarge
        )
    }
}
