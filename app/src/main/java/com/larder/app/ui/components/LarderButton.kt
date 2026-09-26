package com.larder.app.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.larder.app.ui.theme.ClayAccent
import com.larder.app.ui.theme.CreamBase
import com.larder.app.ui.theme.DeepOliveText

@Composable
fun LarderButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isSecondary: Boolean = false
) {
    if (isSecondary) {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(vertical = 14.dp, horizontal = 20.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = DeepOliveText
            ),
            modifier = modifier
        ) {
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    } else {
        Button(
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(vertical = 14.dp, horizontal = 20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ClayAccent,
                contentColor = CreamBase,
                disabledContainerColor = ClayAccent.copy(alpha = 0.5f),
                disabledContentColor = CreamBase.copy(alpha = 0.7f)
            ),
            modifier = modifier
        ) {
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
