package com.larder.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.larder.app.domain.model.Item
import com.larder.app.feature.mcp.ExportTargetService
import com.larder.app.feature.mcp.McpClientExporter
import com.larder.app.ui.theme.ClayAccent
import com.larder.app.ui.theme.CreamBase
import com.larder.app.ui.theme.DeepOliveText
import com.larder.app.ui.theme.SurfaceCream
import kotlinx.coroutines.launch

@Composable
fun ExportShoppingListDialog(
    householdId: String,
    itemsToExport: List<Item>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTarget by remember { mutableStateOf(ExportTargetService.NOTION) }
    var isExporting by remember { mutableStateOf(false) }
    var exportStatusMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val exporter = remember { McpClientExporter() }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CreamBase),
            modifier = modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Export Shopping List",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepOliveText
                )

                Text(
                    text = "Push a structured list of ${itemsToExport.size} low-stock or expiring items to your connected service via MCP.",
                    fontSize = 13.sp,
                    color = DeepOliveText.copy(alpha = 0.7f)
                )

                // Service Target Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedTarget == ExportTargetService.NOTION) ClayAccent else SurfaceCream
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTarget = ExportTargetService.NOTION }
                    ) {
                        Text(
                            text = "Notion Database",
                            fontWeight = FontWeight.SemiBold,
                            color = if (selectedTarget == ExportTargetService.NOTION) CreamBase else DeepOliveText,
                            modifier = Modifier.padding(14.dp)
                        )
                    }

                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedTarget == ExportTargetService.GOOGLE_SHEETS) ClayAccent else SurfaceCream
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTarget = ExportTargetService.GOOGLE_SHEETS }
                    ) {
                        Text(
                            text = "Google Sheet",
                            fontWeight = FontWeight.SemiBold,
                            color = if (selectedTarget == ExportTargetService.GOOGLE_SHEETS) CreamBase else DeepOliveText,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }

                if (exportStatusMessage != null) {
                    Text(
                        text = exportStatusMessage!!,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClayAccent
                    )
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LarderButton(
                        text = "Cancel",
                        onClick = onDismiss,
                        isSecondary = true,
                        modifier = Modifier.weight(1f)
                    )
                    LarderButton(
                        text = if (isExporting) "Sending..." else "Send List",
                        onClick = {
                            scope.launch {
                                isExporting = true
                                val result = exporter.exportShoppingList(
                                    householdId = householdId,
                                    targetService = selectedTarget,
                                    itemsToExport = itemsToExport
                                )
                                if (result.isSuccess) {
                                    exportStatusMessage = result.getOrThrow().message
                                } else {
                                    exportStatusMessage = "Failed to send list"
                                }
                                isExporting = false
                            }
                        },
                        enabled = !isExporting && itemsToExport.isNotEmpty(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
