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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.larder.app.feature.camera.ScanType
import com.larder.app.feature.inference.InferenceClient
import com.larder.app.ui.components.LarderButton
import com.larder.app.ui.theme.ClayAccent
import com.larder.app.ui.theme.CreamBase
import com.larder.app.ui.theme.DeepOliveText
import com.larder.app.ui.theme.SurfaceCream
import com.larder.app.ui.viewmodel.InventoryViewModel
import kotlinx.coroutines.launch

@Composable
fun ScanScreen(
    viewModel: InventoryViewModel,
    onScanCaptured: (ScanType, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMode by remember { mutableStateOf(ScanType.RECEIPT) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val inferenceClient = remember { InferenceClient() }

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
                    onClick = { selectedMode = ScanType.RECEIPT },
                    isSecondary = selectedMode != ScanType.RECEIPT,
                    modifier = Modifier.weight(1f)
                )
                LarderButton(
                    text = "Shelf Photo Mode",
                    onClick = { selectedMode = ScanType.SHELF },
                    isSecondary = selectedMode != ScanType.SHELF,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Camera Viewport Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceCream),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (selectedMode == ScanType.RECEIPT) "Align receipt within frame" else "Point camera at shelf",
                    color = DeepOliveText.copy(alpha = 0.6f),
                    fontSize = 16.sp
                )
                if (resultMessage != null) {
                    Text(
                        text = resultMessage!!,
                        color = ClayAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }

        // Capture & Analyze Action Button
        LarderButton(
            text = if (isAnalyzing) "Analyzing Image..." else "Capture & Analyze",
            onClick = {
                scope.launch {
                    isAnalyzing = true
                    resultMessage = null
                    
                    val imagePath = "scans/captured_${selectedMode.name.lowercase()}.jpg"
                    val result = inferenceClient.analyzeImage(
                        householdId = viewModel.uiState.value.householdId,
                        signedImagePath = imagePath,
                        scanType = selectedMode
                    )

                    if (result.isSuccess) {
                        val items = result.getOrThrow()
                        items.forEach { item ->
                            viewModel.addItem(
                                name = item.name,
                                category = item.getCategoryEnum(),
                                quantity = item.quantity,
                                unit = item.unit,
                                customExpiryMillis = item.expiryEstimate,
                                needsReview = item.getItemStatusEnum() == com.larder.app.domain.model.ItemStatus.NEEDS_REVIEW
                            )
                        }
                        resultMessage = "Successfully detected & added ${items.size} items!"
                    } else {
                        resultMessage = "Analysis failed. Queued scan for retry."
                    }
                    isAnalyzing = false
                }
            },
            enabled = !isAnalyzing,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
    }
}
