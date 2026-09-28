package com.larder.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
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
import com.larder.app.domain.model.HouseholdMember
import com.larder.app.feature.household.ActionType
import com.larder.app.feature.household.HouseholdViewModel
import com.larder.app.ui.components.ExportShoppingListDialog
import com.larder.app.ui.components.LarderButton
import com.larder.app.ui.theme.AlertRed
import com.larder.app.ui.theme.ClayAccent
import com.larder.app.ui.theme.ConfirmedGreen
import com.larder.app.ui.theme.CreamBase
import com.larder.app.ui.theme.DeepOliveText
import com.larder.app.ui.theme.SurfaceCream
import com.larder.app.ui.viewmodel.InventoryViewModel

@Composable
fun HouseholdSettingsScreen(
    viewModel: InventoryViewModel,
    householdViewModel: HouseholdViewModel,
    modifier: Modifier = Modifier
) {
    val inventoryState by viewModel.uiState.collectAsState()
    val householdState by householdViewModel.uiState.collectAsState()
    val auditLogs by householdViewModel.auditLogs.collectAsState()

    var showExportDialog by remember { mutableStateOf(false) }
    var newHouseholdName by remember { mutableStateOf("") }
    var inviteCodeInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBase)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Household & Integrations",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DeepOliveText
            )
        }

        // Success / Error banners
        householdState.successMessage?.let { msg ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ConfirmedGreen.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(msg, color = ConfirmedGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        LarderButton("Dismiss", onClick = { householdViewModel.dismissMessages() }, isSecondary = true)
                    }
                }
            }
        }
        householdState.errorMessage?.let { msg ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AlertRed.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(msg, color = AlertRed, fontWeight = FontWeight.Bold, fontSize = 14.sp,
                        modifier = Modifier.padding(12.dp))
                }
            }
        }

        // Active Household Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCream),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Current Household", fontSize = 12.sp, color = DeepOliveText.copy(alpha = 0.6f))
                    Text(
                        householdState.household?.name ?: "No household yet",
                        fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DeepOliveText
                    )
                    if (householdState.inviteCode.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Invite Code", fontSize = 12.sp, color = DeepOliveText.copy(alpha = 0.6f))
                                Text(householdState.inviteCode, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ClayAccent)
                            }
                        }
                    }
                }
            }
        }

        // Create Household
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCream),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Create a New Household", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DeepOliveText)
                    OutlinedTextField(
                        value = newHouseholdName,
                        onValueChange = { newHouseholdName = it },
                        placeholder = { Text("Household name (e.g. The Sharma Family)") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    LarderButton(
                        text = if (householdState.isLoading) "Creating..." else "Create Household",
                        onClick = {
                            if (newHouseholdName.isNotBlank()) {
                                householdViewModel.createHousehold(newHouseholdName.trim())
                                newHouseholdName = ""
                            }
                        },
                        enabled = newHouseholdName.isNotBlank() && !householdState.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Join via Invite Code
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCream),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Join Existing Household", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DeepOliveText)
                    OutlinedTextField(
                        value = inviteCodeInput,
                        onValueChange = { inviteCodeInput = it.uppercase() },
                        placeholder = { Text("Enter invite code (e.g. LRD-8492)") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    LarderButton(
                        text = if (householdState.isLoading) "Joining..." else "Join Household",
                        onClick = {
                            if (inviteCodeInput.isNotBlank()) {
                                householdViewModel.joinHousehold(inviteCodeInput.trim())
                                inviteCodeInput = ""
                            }
                        },
                        enabled = inviteCodeInput.isNotBlank() && !householdState.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Members List
        if (householdState.members.isNotEmpty()) {
            item {
                Text("Members", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DeepOliveText)
            }
            items(householdState.members) { member ->
                MemberRow(member = member)
            }
        }

        // MCP Export Connector Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCream),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("MCP Integrations", fontSize = 12.sp, color = DeepOliveText.copy(alpha = 0.6f))
                    Text("Export Shopping List", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DeepOliveText)
                    Text(
                        "Push expiring or low-stock items into Notion or Google Sheets via MCP.",
                        fontSize = 13.sp, color = DeepOliveText.copy(alpha = 0.7f)
                    )
                    LarderButton(
                        text = "Send to Notion / Sheets",
                        onClick = { showExportDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Audit Log
        if (auditLogs.isNotEmpty()) {
            item {
                Text("Recent Activity", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DeepOliveText)
            }
            items(auditLogs.take(10)) { entry ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCream),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column {
                            Text(
                                text = "${entry.userName} — ${entry.actionType.name.replace('_', ' ')}",
                                fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DeepOliveText
                            )
                            Text(entry.details, fontSize = 12.sp, color = DeepOliveText.copy(alpha = 0.65f))
                        }
                    }
                }
            }
        }
    }

    if (showExportDialog) {
        ExportShoppingListDialog(
            householdId = inventoryState.householdId,
            itemsToExport = inventoryState.expiringItems.ifEmpty { inventoryState.items },
            onDismiss = { showExportDialog = false }
        )
    }
}

@Composable
private fun MemberRow(member: HouseholdMember) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCream),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = member.userId.take(12) + "...",
                fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DeepOliveText
            )
            Card(
                shape = RoundedCornerShape(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (member.role == "owner") ClayAccent.copy(alpha = 0.2f)
                    else SurfaceCream
                )
            ) {
                Text(
                    text = member.role.replaceFirstChar { it.uppercase() },
                    fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ClayAccent,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
