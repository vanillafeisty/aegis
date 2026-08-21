package com.example.aegis.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aegis.data.model.CredentialSettingsEntity
import com.example.aegis.ui.theme.*
import com.example.aegis.ui.viewmodel.AppTab
import com.example.aegis.ui.viewmodel.ExecutionStep

@Composable
fun AegisTopBar(
    settings: CredentialSettingsEntity?,
    onSettingsClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🛡️",
                            fontSize = 20.sp
                        )
                    }
                    Column {
                        Text(
                            text = "Aegis AI Agent",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "Autonomous LinkedIn Outreach & Automation",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .testTag("topbar_settings_button")
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Integration status badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(
                    label = "LinkedIn",
                    isConnected = settings?.linkedinConnected == true,
                    testTag = "badge_linkedin"
                )
                StatusBadge(
                    label = "Gmail",
                    isConnected = settings?.gmailConnected == true,
                    testTag = "badge_gmail"
                )
                StatusBadge(
                    label = "AI Engine",
                    isConnected = settings?.aiConnected == true,
                    testTag = "badge_ai"
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Stealth Active",
                    style = MaterialTheme.typography.labelSmall,
                    color = EmeraldAccent,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun StatusBadge(
    label: String,
    isConnected: Boolean,
    testTag: String
) {
    val bgColor = if (isConnected) EmeraldAccent.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isConnected) DarkGreen else MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = if (isConnected) EmeraldAccent.copy(alpha = 0.4f) else BorderColor

    Box(
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(if (isConnected) EmeraldAccent else Color.Gray)
            )
            Text(
                text = "${if (isConnected) "✓" else "○"} $label",
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AegisBottomNavigationBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier
            .testTag("bottom_nav_bar")
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        NavigationBarItem(
            selected = currentTab == AppTab.CHAT,
            onClick = { onTabSelected(AppTab.CHAT) },
            icon = {
                Icon(
                    imageVector = if (currentTab == AppTab.CHAT) Icons.Filled.ChatBubble else Icons.Outlined.ChatBubbleOutline,
                    contentDescription = "Agent Chat"
                )
            },
            label = { Text("Agent Chat") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DarkGreen,
                selectedTextColor = DarkGreen,
                indicatorColor = LightSage
            ),
            modifier = Modifier.testTag("nav_item_chat")
        )

        NavigationBarItem(
            selected = currentTab == AppTab.HUB,
            onClick = { onTabSelected(AppTab.HUB) },
            icon = {
                Icon(
                    imageVector = if (currentTab == AppTab.HUB) Icons.Filled.Hub else Icons.Outlined.Hub,
                    contentDescription = "Automation Hub"
                )
            },
            label = { Text("Automation") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DarkGreen,
                selectedTextColor = DarkGreen,
                indicatorColor = LightSage
            ),
            modifier = Modifier.testTag("nav_item_hub")
        )

        NavigationBarItem(
            selected = currentTab == AppTab.LOGS,
            onClick = { onTabSelected(AppTab.LOGS) },
            icon = {
                Icon(
                    imageVector = if (currentTab == AppTab.LOGS) Icons.Filled.History else Icons.Outlined.History,
                    contentDescription = "Activity Logs"
                )
            },
            label = { Text("Logs") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DarkGreen,
                selectedTextColor = DarkGreen,
                indicatorColor = LightSage
            ),
            modifier = Modifier.testTag("nav_item_logs")
        )

        NavigationBarItem(
            selected = currentTab == AppTab.SETUP,
            onClick = { onTabSelected(AppTab.SETUP) },
            icon = {
                Icon(
                    imageVector = if (currentTab == AppTab.SETUP) Icons.Filled.Tune else Icons.Outlined.Tune,
                    contentDescription = "Connection Setup"
                )
            },
            label = { Text("Setup") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DarkGreen,
                selectedTextColor = DarkGreen,
                indicatorColor = LightSage
            ),
            modifier = Modifier.testTag("nav_item_setup")
        )
    }
}

@Composable
fun LiveExecutionBanner(
    statusText: String?,
    steps: List<ExecutionStep>
) {
    AnimatedVisibility(
        visible = statusText != null || steps.isNotEmpty(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Surface(
            color = DarkSurfaceVariant,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = EmeraldAccent
                    )
                    Text(
                        text = statusText ?: "Agent executing automation...",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (steps.isNotEmpty()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(start = 28.dp)
                    ) {
                        steps.forEach { step ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = if (step.isDone) "✓" else if (step.isRunning) "⏳" else "○",
                                    color = if (step.isDone) EmeraldAccent else if (step.isRunning) AmberAccent else Color.LightGray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = step.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (step.isDone || step.isRunning) Color.White else Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
