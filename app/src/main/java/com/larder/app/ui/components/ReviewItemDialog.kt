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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.larder.app.domain.model.Category
import com.larder.app.domain.model.Item
import com.larder.app.ui.theme.CreamBase
import com.larder.app.ui.theme.DeepOliveText
import com.larder.app.ui.theme.NeedsReviewOrange
import com.larder.app.ui.theme.SurfaceCream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewItemDialog(
    item: Item,
    onDismiss: () -> Unit,
    onConfirmReview: (String, Category) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(item.name) }
    var selectedCategory by remember { mutableStateOf(item.getCategoryEnum()) }
    var dropdownExpanded by remember { mutableStateOf(false) }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Review Classification",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepOliveText
                    )
                    StatusPill(status = item.getItemStatusEnum())
                }

                Text(
                    text = "Classification model returned low confidence for this item. Please verify name and category.",
                    fontSize = 13.sp,
                    color = DeepOliveText.copy(alpha = 0.7f)
                )

                // Item Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Selection
                Column {
                    Text(
                        text = "Category",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = DeepOliveText.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCream),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { dropdownExpanded = true }
                    ) {
                        Text(
                            text = selectedCategory.displayName,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepOliveText,
                            modifier = Modifier.padding(14.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        Category.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.displayName) },
                                onClick = {
                                    selectedCategory = cat
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
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
                        text = "Confirm & Add",
                        onClick = {
                            if (name.isNotBlank()) {
                                onConfirmReview(name.trim(), selectedCategory)
                                onDismiss()
                            }
                        },
                        enabled = name.isNotBlank(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
