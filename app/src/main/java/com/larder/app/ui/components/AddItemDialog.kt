package com.larder.app.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.larder.app.domain.model.Category
import com.larder.app.domain.model.UnitType
import com.larder.app.ui.theme.ClayAccent
import com.larder.app.ui.theme.CreamBase
import com.larder.app.ui.theme.DeepOliveText
import com.larder.app.ui.theme.SurfaceCream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    onAddItem: (String, Category, Double, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(Category.PRODUCE) }
    var quantity by remember { mutableDoubleStateOf(1.0) }
    var selectedUnit by remember { mutableStateOf(UnitType.UNIT) }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var unitDropdownExpanded by remember { mutableStateOf(false) }

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
                    text = "Add Pantry Item",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepOliveText
                )

                // Item Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") },
                    placeholder = { Text("e.g. Whole Milk 1L") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Dropdown
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
                            .clickable { categoryDropdownExpanded = true }
                    ) {
                        Text(
                            text = selectedCategory.displayName,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepOliveText,
                            modifier = Modifier.padding(14.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        Category.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.displayName) },
                                onClick = {
                                    selectedCategory = cat
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Quantity Stepper & Unit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Quantity",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = DeepOliveText.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        QuantityStepper(
                            quantity = quantity,
                            onQuantityChange = { quantity = it }
                        )
                    }

                    Column {
                        Text(
                            text = "Unit",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = DeepOliveText.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCream),
                            modifier = Modifier.clickable { unitDropdownExpanded = true }
                        ) {
                            Text(
                                text = selectedUnit.symbol,
                                fontWeight = FontWeight.SemiBold,
                                color = DeepOliveText,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = unitDropdownExpanded,
                            onDismissRequest = { unitDropdownExpanded = false }
                        ) {
                            UnitType.entries.forEach { unit ->
                                DropdownMenuItem(
                                    text = { Text(unit.symbol) },
                                    onClick = {
                                        selectedUnit = unit
                                        unitDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Buttons
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
                        text = "Save Item",
                        onClick = {
                            if (name.isNotBlank()) {
                                onAddItem(name.trim(), selectedCategory, quantity, selectedUnit.symbol)
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
