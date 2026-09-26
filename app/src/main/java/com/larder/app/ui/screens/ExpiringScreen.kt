package com.larder.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.larder.app.domain.model.Item
import com.larder.app.ui.components.ItemCard
import com.larder.app.ui.theme.CreamBase
import com.larder.app.ui.theme.DeepOliveText
import com.larder.app.ui.viewmodel.InventoryViewModel

@Composable
fun ExpiringScreen(
    viewModel: InventoryViewModel,
    onItemClick: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val expiringItems = uiState.expiringItems

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBase)
            .padding(16.dp)
    ) {
        Text(
            text = "Expiring Soon",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DeepOliveText,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = "Items expiring within 3 days or already expired",
            fontSize = 14.sp,
            color = DeepOliveText.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (expiringItems.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No items expiring soon! 🎉",
                    fontSize = 16.sp,
                    color = DeepOliveText.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(expiringItems, key = { it.id }) { item ->
                    ItemCard(
                        item = item,
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }
    }
}
