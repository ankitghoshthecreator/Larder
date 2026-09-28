package com.larder.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.larder.app.feature.household.HouseholdViewModel
import com.larder.app.ui.screens.ExpiringScreen
import com.larder.app.ui.screens.HomeScreen
import com.larder.app.ui.screens.HouseholdSettingsScreen
import com.larder.app.ui.screens.ScanScreen
import com.larder.app.ui.theme.ClayAccent
import com.larder.app.ui.theme.CreamBase
import com.larder.app.ui.theme.DeepOliveText
import com.larder.app.ui.theme.SurfaceCream
import com.larder.app.ui.viewmodel.InventoryViewModel

enum class LarderTab(val title: String) {
    HOME("Pantry"),
    EXPIRING("Expiring"),
    SCAN("Scan"),
    HOUSEHOLD("Household")
}

@Composable
fun LarderNavGraph(
    inventoryViewModel: InventoryViewModel,
    householdViewModel: HouseholdViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(LarderTab.HOME) }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = SurfaceCream, contentColor = DeepOliveText) {
                LarderTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        label = {
                            Text(
                                text = tab.title,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = { },
                        colors = NavigationBarItemDefaults.colors(
                            selectedTextColor = ClayAccent,
                            indicatorColor = CreamBase
                        )
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        when (selectedTab) {
            LarderTab.HOME -> HomeScreen(viewModel = inventoryViewModel, onItemClick = {}, modifier = contentModifier)
            LarderTab.EXPIRING -> ExpiringScreen(viewModel = inventoryViewModel, onItemClick = {}, modifier = contentModifier)
            LarderTab.SCAN -> ScanScreen(viewModel = inventoryViewModel, onScanCaptured = { _, _ -> }, modifier = contentModifier)
            LarderTab.HOUSEHOLD -> HouseholdSettingsScreen(
                viewModel = inventoryViewModel,
                householdViewModel = householdViewModel,
                modifier = contentModifier
            )
        }
    }
}
