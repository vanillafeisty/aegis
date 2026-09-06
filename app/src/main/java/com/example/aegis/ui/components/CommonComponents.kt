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
    currentTab: AppTab,
    onNavigate: (AppTab) -> Unit
) {
    Surface(
        color = SoftCreamSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, WarmStoneBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 18.dp, vertical = 12.dp)
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
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(TerracottaAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "A",
                            fontFamily = FontFamily.Serif,
                            color = SoftCreamSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Column {
                        Text(
                            text = "Aegis",
                            style = MaterialTheme.typography.titleMedium,
                            color = CharcoalPrimary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Autonomous LinkedIn Intelligence",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Serif,
                            color = CharcoalMuted
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { onNavigate(AppTab.OPENING) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Home,
                            contentDescription = "Welcome Home",
                            tint = if (currentTab == AppTab.OPENING) TerracottaAccent else CharcoalMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = { onNavigate(AppTab.CONNECT) },
                        modifier = Modifier
                            .testTag("topbar_settings_button")
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Tune,
                            contentDescription = "Connection Settings",
                            tint = if (currentTab == AppTab.CONNECT) TerracottaAccent else CharcoalMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Integration status badges (Anthropic style)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(
                    label = "LinkedIn API",
                    isConnected = settings?.linkedinConnected == true,
                    testTag = "badge_linkedin"
                )
                StatusBadge(
                    label = "Gmail",
                    isConnected = settings?.gmailConnected == true,
                    testTag = "badge_gmail"
                )
                StatusBadge(
                    label = "Groq AI",
                    isConnected = settings?.aiConnected == true,
                    testTag = "badge_ai"
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Stealth Cadence",
                    style = MaterialTheme.typography.labelSmall,
                    color = CharcoalLight,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium
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
    val bgColor = if (isConnected) PastelSage else MutedBeigeCard
    val textColor = if (isConnected) DarkSage else CharcoalMuted
    val borderColor = if (isConnected) SageBorder else WarmStoneBorder

    Box(
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isConnected) DarkSage else CharcoalLight)
            )
            Text(
                text = "${if (isConnected) "✓" else "○"} $label",
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontFamily = FontFamily.Serif,
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
        containerColor = SoftCreamSurface,
        tonalElevation = 0.dp,
        modifier = Modifier
            .testTag("bottom_nav_bar")
            .border(1.dp, WarmStoneBorder, RoundedCornerShape(0.dp))
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        NavigationBarItem(
            selected = currentTab == AppTab.OPENING,
            onClick = { onTabSelected(AppTab.OPENING) },
            icon = {
                Icon(
                    imageVector = if (currentTab == AppTab.OPENING) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                    contentDescription = "Overview"
                )
            },
            label = {
                Text(
                    text = "Welcome",
                    fontFamily = FontFamily.Serif,
                    fontSize = 11.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TerracottaAccent,
                selectedTextColor = TerracottaAccent,
                unselectedIconColor = CharcoalMuted,
                unselectedTextColor = CharcoalMuted,
                indicatorColor = TerracottaSoft
            ),
            modifier = Modifier.testTag("nav_item_opening")
        )

        NavigationBarItem(
            selected = currentTab == AppTab.CONNECT,
            onClick = { onTabSelected(AppTab.CONNECT) },
            icon = {
                Icon(
                    imageVector = if (currentTab == AppTab.CONNECT) Icons.Filled.Link else Icons.Outlined.Link,
                    contentDescription = "Connect"
                )
            },
            label = {
                Text(
                    text = "Connect",
                    fontFamily = FontFamily.Serif,
                    fontSize = 11.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TerracottaAccent,
                selectedTextColor = TerracottaAccent,
                unselectedIconColor = CharcoalMuted,
                unselectedTextColor = CharcoalMuted,
                indicatorColor = TerracottaSoft
            ),
            modifier = Modifier.testTag("nav_item_connect")
        )

        NavigationBarItem(
            selected = currentTab == AppTab.CHAT,
            onClick = { onTabSelected(AppTab.CHAT) },
            icon = {
                Icon(
                    imageVector = if (currentTab == AppTab.CHAT) Icons.Filled.ChatBubble else Icons.Outlined.ChatBubbleOutline,
                    contentDescription = "Conversation"
                )
            },
            label = {
                Text(
                    text = "Conversation",
                    fontFamily = FontFamily.Serif,
                    fontSize = 11.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TerracottaAccent,
                selectedTextColor = TerracottaAccent,
                unselectedIconColor = CharcoalMuted,
                unselectedTextColor = CharcoalMuted,
                indicatorColor = TerracottaSoft
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
            label = {
                Text(
                    text = "Hub",
                    fontFamily = FontFamily.Serif,
                    fontSize = 11.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TerracottaAccent,
                selectedTextColor = TerracottaAccent,
                unselectedIconColor = CharcoalMuted,
                unselectedTextColor = CharcoalMuted,
                indicatorColor = TerracottaSoft
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
            label = {
                Text(
                    text = "Logs",
                    fontFamily = FontFamily.Serif,
                    fontSize = 11.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TerracottaAccent,
                selectedTextColor = TerracottaAccent,
                unselectedIconColor = CharcoalMuted,
                unselectedTextColor = CharcoalMuted,
                indicatorColor = TerracottaSoft
            ),
            modifier = Modifier.testTag("nav_item_logs")
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
            color = MutedBeigeCard,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, WarmStoneBorder),
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
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = TerracottaAccent
                    )
                    Text(
                        text = statusText ?: "Agent executing automation...",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Serif,
                        color = CharcoalPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (steps.isNotEmpty()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(start = 26.dp)
                    ) {
                        steps.forEach { step ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = if (step.isDone) "✓" else if (step.isRunning) "⏳" else "○",
                                    color = if (step.isDone) DarkSage else if (step.isRunning) DarkAmber else CharcoalLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = step.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontFamily = FontFamily.Serif,
                                    color = if (step.isDone || step.isRunning) CharcoalPrimary else CharcoalMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
