package com.larder.app.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.larder.app.domain.calculator.ExpiryCalculator
import com.larder.app.domain.model.Item
import com.larder.app.ui.theme.BorderColor
import com.larder.app.ui.theme.DeepOliveText
import com.larder.app.ui.theme.SurfaceCream

@Composable
fun ItemCard(
    item: Item,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val daysLeft = ExpiryCalculator.calculateDaysRemaining(item.expiryEstimate)

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = SurfaceCream),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DeepOliveText
                )
                Text(
                    text = "${item.category} • ${item.quantity} ${item.unit}",
                    fontSize = 13.sp,
                    color = DeepOliveText.copy(alpha = 0.7f)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                StatusPill(status = item.getItemStatusEnum())
                Text(
                    text = if (daysLeft >= 0) "${daysLeft}d left" else "Expired",
                    fontSize = 12.sp,
                    color = DeepOliveText.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
