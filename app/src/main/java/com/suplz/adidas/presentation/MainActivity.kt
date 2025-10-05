@file:OptIn(ExperimentalMaterial3Api::class)

package com.suplz.adidas.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import com.suplz.adidas.presentation.navigation.AppNavigation
import com.suplz.adidas.presentation.ui.theme.AdidasTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdidasTheme {
                Scaffold { innerPadding ->
                    AppNavigation(paddingValues = innerPadding)
                }
            }
        }
    }
}