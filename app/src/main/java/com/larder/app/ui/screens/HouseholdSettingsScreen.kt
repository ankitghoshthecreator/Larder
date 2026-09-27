package com.larder.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.larder.app.ui.components.ExportShoppingListDialog
import com.larder.app.ui.components.LarderButton
import com.larder.app.ui.theme.ClayAccent
import com.larder.app.ui.theme.CreamBase
import com.larder.app.ui.theme.DeepOliveText
import com.larder.app.ui.theme.SurfaceCream
import com.larder.app.ui.viewmodel.InventoryViewModel

@Composable
fun HouseholdSettingsScreen(
    viewModel: InventoryViewModel,
    householdName: String = "My Home Household",
    inviteCode: String = "LRD-8492",
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showExportDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBase)
            .padding(16.dp)
    ) {
        Text(
            text = "Household & Integrations",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DeepOliveText,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Active Household Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCream),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Current Household",
                    fontSize = 12.sp,
                    color = DeepOliveText.copy(alpha = 0.6f)
                )
                Text(
                    text = householdName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepOliveText,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Invite Code", fontSize = 12.sp, color = DeepOliveText.copy(alpha = 0.6f))
                        Text(text = inviteCode, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ClayAccent)
                    }
                    LarderButton(
                        text = "Copy Code",
                        onClick = { /* Copy code */ },
                        isSecondary = true
                    )
                }
            }
        }

        // MCP Export Connector Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCream),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Model Context Protocol (MCP) Integrations",
                    fontSize = 12.sp,
                    color = DeepOliveText.copy(alpha = 0.6f)
                )
                Text(
                    text = "Export Shopping List",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepOliveText,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Push low-stock and expiring items into your connected Notion database or Google Sheet.",
                    fontSize = 13.sp,
                    color = DeepOliveText.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                LarderButton(
                    text = "Send Shopping List to Notion/Sheets",
                    onClick = { showExportDialog = true },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showExportDialog) {
        ExportShoppingListDialog(
            householdId = uiState.householdId,
            itemsToExport = uiState.expiringItems.ifEmpty { uiState.items },
            onDismiss = { showExportDialog = false }
        )
    }
}
