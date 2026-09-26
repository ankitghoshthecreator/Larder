package com.larder.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.larder.app.data.local.db.LarderDatabase
import com.larder.app.data.repository.LocalInventoryRepository
import com.larder.app.ui.navigation.LarderNavGraph
import com.larder.app.ui.theme.LarderTheme
import com.larder.app.ui.viewmodel.InventoryViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: InventoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = LarderDatabase.getDatabase(applicationContext)
        val repository = LocalInventoryRepository(database.itemDao())
        viewModel = InventoryViewModel(repository)

        setContent {
            LarderTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LarderNavGraph(
                        viewModel = viewModel,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}
