package com.larder.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.larder.app.ui.components.LarderButton
import com.larder.app.ui.theme.BorderColor
import com.larder.app.ui.theme.CreamBase
import com.larder.app.ui.theme.DeepOliveText
import com.larder.app.ui.theme.SurfaceCream

enum class ScanMode { RECEIPT, SHELF }

@Composable
fun ScanScreen(
    onScanCaptured: (ScanMode, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMode by remember { mutableStateOf(ScanMode.RECEIPT) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBase)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Photo Scan",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DeepOliveText,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Mode Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LarderButton(
                    text = "Receipt OCR Mode",
                    onClick = { selectedMode = ScanMode.RECEIPT },
                    isSecondary = selectedMode != ScanMode.RECEIPT,
                    modifier = Modifier.weight(1f)
                )
                LarderButton(
                    text = "Shelf Photo Mode",
                    onClick = { selectedMode = ScanMode.SHELF },
                    isSecondary = selectedMode != ScanMode.SHELF,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Camera Viewport Box Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceCream),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (selectedMode == ScanMode.RECEIPT) "Align receipt within frame" else "Point camera at shelf",
                color = DeepOliveText.copy(alpha = 0.6f),
                fontSize = 16.sp
            )
        }

        // Capture Action
        LarderButton(
            text = "Capture & Analyze",
            onClick = { onScanCaptured(selectedMode, "sample_scan_path.jpg") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
    }
}
