package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.ads.AdMobManager
import com.example.ui.screens.WhyIChangedApp
import com.example.ui.viewmodel.BookReaderViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AdMobManager.initialize(applicationContext)
        setContent {
            val readerViewModel: BookReaderViewModel = viewModel(
                factory = BookReaderViewModel.Factory(applicationContext)
            )
            WhyIChangedApp(viewModel = readerViewModel)
        }
    }
}
