package com.larder.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.larder.app.domain.model.ItemStatus
import com.larder.app.ui.theme.ConfirmedGreen
import com.larder.app.ui.theme.CreamBase
import com.larder.app.ui.theme.NeedsReviewOrange

@Composable
fun StatusPill(
    status: ItemStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, text) = when (status) {
        ItemStatus.CONFIRMED -> Pair(ConfirmedGreen, "Confirmed")
        ItemStatus.NEEDS_REVIEW -> Pair(NeedsReviewOrange, "Needs Review")
    }

    Text(
        text = text,
        color = CreamBase,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
