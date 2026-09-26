package com.larder.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.larder.app.ui.theme.BorderColor
import com.larder.app.ui.theme.DeepOliveText
import com.larder.app.ui.theme.SurfaceCream

@Composable
fun QuantityStepper(
    quantity: Double,
    onQuantityChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
    step: Double = 1.0,
    minQuantity: Double = 0.5
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
            .background(SurfaceCream)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        // Minus Button
        Text(
            text = "−",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = DeepOliveText,
            modifier = Modifier
                .clickable {
                    if (quantity - step >= minQuantity) {
                        onQuantityChange(quantity - step)
                    }
                }
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )

        // Quantity Display
        Text(
            text = if (quantity % 1.0 == 0.0) quantity.toInt().toString() else quantity.toString(),
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = DeepOliveText,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Plus Button
        Text(
            text = "+",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = DeepOliveText,
            modifier = Modifier
                .clickable { onQuantityChange(quantity + step) }
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
