package com.larder.app

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.larder.app.data.local.db.LarderDatabase
import com.larder.app.data.remote.supabase.SupabaseAuthManager
import com.larder.app.data.repository.LocalInventoryRepository
import com.larder.app.feature.household.HouseholdAuditLogger
import com.larder.app.feature.household.HouseholdManager
import com.larder.app.feature.household.HouseholdViewModel
import com.larder.app.feature.notifications.LarderNotificationService
import com.larder.app.ui.navigation.LarderNavGraph
import com.larder.app.ui.theme.LarderTheme
import com.larder.app.ui.viewmodel.InventoryViewModel

class MainActivity : ComponentActivity() {

    private lateinit var inventoryViewModel: InventoryViewModel
    private lateinit var householdViewModel: HouseholdViewModel

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* Permissions granted or denied — handled gracefully in features */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- Setup local database
        val database = LarderDatabase.getDatabase(applicationContext)

        // --- Setup repositories
        val inventoryRepository = LocalInventoryRepository(database.itemDao())

        // --- Setup auth + household managers
        val authManager = SupabaseAuthManager()
        val householdManager = HouseholdManager(database.householdDao())
        val auditLogger = HouseholdAuditLogger()

        // --- Initialise ViewModels
        inventoryViewModel = InventoryViewModel(inventoryRepository)
        householdViewModel = HouseholdViewModel(householdManager, auditLogger, authManager)

        // --- Register notification channels (Part 6)
        LarderNotificationService(applicationContext).createNotificationChannels()

        // --- Request runtime permissions
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.POST_NOTIFICATIONS
            )
        )

        setContent {
            LarderTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    LarderNavGraph(
                        inventoryViewModel = inventoryViewModel,
                        householdViewModel = householdViewModel
                    )
                }
            }
        }
    }
}
