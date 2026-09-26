package com.larder.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.larder.app.domain.model.Category
import com.larder.app.domain.model.Item
import com.larder.app.ui.components.ItemCard
import com.larder.app.ui.theme.ClayAccent
import com.larder.app.ui.theme.CreamBase
import com.larder.app.ui.theme.DeepOliveText
import com.larder.app.ui.theme.NeedsReviewOrange
import com.larder.app.ui.theme.SurfaceCream
import com.larder.app.ui.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: InventoryViewModel,
    onItemClick: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    val filteredItems = uiState.items.filter { item ->
        val matchesCategory = uiState.selectedCategory == null || item.getCategoryEnum() == uiState.selectedCategory
        val matchesQuery = uiState.searchQuery.isEmpty() || item.name.contains(uiState.searchQuery, ignoreCase = true)
        matchesCategory && matchesQuery
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBase)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Pantry Inventory",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DeepOliveText,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Review Banner (if any item needs review)
        if (uiState.itemsNeedingReview.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = NeedsReviewOrange.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${uiState.itemsNeedingReview.size} items need review",
                        fontWeight = FontWeight.SemiBold,
                        color = NeedsReviewOrange,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text("Search groceries...", color = DeepOliveText.copy(alpha = 0.5f)) },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            item {
                FilterChip(
                    selected = uiState.selectedCategory == null,
                    onClick = { viewModel.filterByCategory(null) },
                    label = { Text("All") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ClayAccent,
                        selectedLabelColor = CreamBase
                    )
                )
            }
            items(Category.entries.toTypedArray()) { cat ->
                FilterChip(
                    selected = uiState.selectedCategory == cat,
                    onClick = { viewModel.filterByCategory(cat) },
                    label = { Text(cat.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ClayAccent,
                        selectedLabelColor = CreamBase
                    )
                )
            }
        }

        // Inventory Item List
        if (filteredItems.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No items found in pantry",
                    color = DeepOliveText.copy(alpha = 0.6f),
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredItems, key = { it.id }) { item ->
                    ItemCard(
                        item = item,
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }
    }
}
