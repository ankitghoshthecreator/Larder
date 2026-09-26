package com.larder.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import com.larder.app.ui.components.LarderButton
import com.larder.app.ui.theme.ClayAccent
import com.larder.app.ui.theme.CreamBase
import com.larder.app.ui.theme.DeepOliveText
import com.larder.app.ui.theme.SurfaceCream

@Composable
fun HouseholdSettingsScreen(
    householdName: String = "My Home Household",
    inviteCode: String = "LRD-8492",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBase)
            .padding(16.dp)
    ) {
        Text(
            text = "Household Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DeepOliveText,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Active Household Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCream),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Current Household",
                    fontSize = 12.sp,
                    color = DeepOliveText.copy(alpha = 0.6f)
                )
                Text(
                    text = householdName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepOliveText,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Invite Code", fontSize = 12.sp, color = DeepOliveText.copy(alpha = 0.6f))
                        Text(text = inviteCode, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ClayAccent)
                    }
                    LarderButton(
                        text = "Copy Code",
                        onClick = { /* Copy to Clipboard */ },
                        isSecondary = true
                    )
                }
            }
        }
    }
}
