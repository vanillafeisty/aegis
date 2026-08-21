package com.example.aegis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.aegis.ui.components.AegisBottomNavigationBar
import com.example.aegis.ui.components.AegisTopBar
import com.example.aegis.ui.screens.*
import com.example.aegis.ui.theme.AegisTheme
import com.example.aegis.ui.viewmodel.AegisViewModel
import com.example.aegis.ui.viewmodel.AppTab

class MainActivity : ComponentActivity() {
    private val viewModel: AegisViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AegisTheme {
                val currentTab by viewModel.currentTab.collectAsState()
                val settings by viewModel.settings.collectAsState()

                Scaffold(
                    topBar = {
                        AegisTopBar(
                            settings = settings,
                            onSettingsClick = { viewModel.setTab(AppTab.SETUP) }
                        )
                    },
                    bottomBar = {
                        AegisBottomNavigationBar(
                            currentTab = currentTab,
                            onTabSelected = { tab -> viewModel.setTab(tab) }
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentTab,
                            label = "tab_transition",
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            }
                        ) { tab ->
                            when (tab) {
                                AppTab.CHAT -> ChatScreen(viewModel = viewModel)
                                AppTab.HUB -> AutomationHubScreen(viewModel = viewModel)
                                AppTab.LOGS -> ActivityLogsScreen(viewModel = viewModel)
                                AppTab.SETUP -> ConnectionSetupScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
