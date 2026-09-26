package com.larder.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.larder.app.domain.model.Category
import com.larder.app.ui.theme.ClayAccent
import com.larder.app.ui.theme.CreamBase

@Composable
fun CategoryIcon(
    category: Category,
    modifier: Modifier = Modifier
) {
    val initial = category.displayName.take(1).uppercase()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(ClayAccent.copy(alpha = 0.15f))
    ) {
        Text(
            text = initial,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = ClayAccent
        )
    }
}
