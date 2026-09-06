package com.example.aegis.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aegis.data.model.ChatMessageEntity
import com.example.aegis.ui.components.LiveExecutionBanner
import com.example.aegis.ui.theme.*
import com.example.aegis.ui.viewmodel.AegisViewModel
import com.example.aegis.ui.viewmodel.AppTab
import com.example.aegis.ui.viewmodel.HubSection
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatScreen(
    viewModel: AegisViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatMessages.collectAsState()
    val isBusy by viewModel.isAgentBusy.collectAsState()
    val statusText by viewModel.agentStatusText.collectAsState()
    val steps by viewModel.activeExecutionSteps.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, isBusy) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmBeigeBackground)
    ) {
        // Minimalist Anthropic Workspace Header
        Surface(
            color = SoftCreamSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, WarmStoneBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(TerracottaAccent)
                    )
                    Text(
                        text = "Aegis Assistant",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Serif,
                        color = CharcoalPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MutedBeigeCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarmStoneBorder)
                    ) {
                        Text(
                            text = if (settings?.linkedinConnected == true) "LinkedIn Live ✓" else "Groq LLaMA 3.3",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Serif,
                            color = CharcoalMuted,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.clearHistory() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteSweep,
                            contentDescription = "Clear Chat",
                            tint = CharcoalMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Live Execution Steps
        LiveExecutionBanner(statusText = statusText, steps = steps)

        // Chat Message Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                AnthropicChatMessageItem(
                    message = message,
                    onNavigateToHub = { section ->
                        viewModel.setHubSection(section)
                        viewModel.setTab(AppTab.HUB)
                    }
                )
            }
        }

        // Quick Action Chips Row
        AnthropicQuickActionsBar(
            onSelectAction = { promptText ->
                textInput = promptText
            },
            isBusy = isBusy
        )

        // Bottom Chat Input Box (Anthropic Style Pill)
        Surface(
            color = SoftCreamSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, WarmStoneBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        placeholder = {
                            Text(
                                text = "Ask Aegis to write posts, connect, or sweep inbox...",
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = FontFamily.Serif,
                                color = CharcoalLight
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TerracottaAccent,
                            unfocusedBorderColor = WarmStoneBorder,
                            focusedContainerColor = WarmBeigeBackground,
                            unfocusedContainerColor = WarmBeigeBackground,
                            cursorColor = TerracottaAccent
                        ),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = false,
                        maxLines = 4,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (textInput.isNotBlank() && !isBusy) {
                                    viewModel.handleUserChatInput(textInput)
                                    textInput = ""
                                }
                            }
                        ),
                        enabled = !isBusy
                    )

                    FloatingActionButton(
                        onClick = {
                            if (textInput.isNotBlank() && !isBusy) {
                                viewModel.handleUserChatInput(textInput)
                                textInput = ""
                            }
                        },
                        modifier = Modifier
                            .testTag("chat_send_button")
                            .size(46.dp),
                        shape = CircleShape,
                        containerColor = if (textInput.isNotBlank() && !isBusy) TerracottaAccent else MutedBeigeCard,
                        contentColor = if (textInput.isNotBlank() && !isBusy) SoftCreamSurface else CharcoalLight,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
                    ) {
                        if (isBusy) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = TerracottaAccent,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Aegis v2.4 • Connected directly to LinkedIn API and Groq LLaMA-3.3-70B",
                    style = MaterialTheme.typography.labelSmall,
                    color = CharcoalLight,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun AnthropicChatMessageItem(
    message: ChatMessageEntity,
    onNavigateToHub: (HubSection) -> Unit
) {
    val isUser = message.role == "user"
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { timeFormatter.format(Date(message.timestamp)) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MutedBeigeCard)
                    .border(1.dp, WarmStoneBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = TerracottaAccent,
                    fontSize = 15.sp
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 330.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                color = if (isUser) TerracottaSoft else SoftCreamSurface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isUser) TerracottaBorder else WarmStoneBorder
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!isUser && message.actionType != null) {
                        AnthropicActionTag(actionType = message.actionType)
                    }

                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = FontFamily.Serif,
                        color = CharcoalPrimary,
                        lineHeight = 22.sp
                    )

                    // Navigation deep links for quick agent interactions
                    if (!isUser && message.actionType != null) {
                        when (message.actionType) {
                            "post" -> AnthropicHubQuickLinkButton(label = "View Post History →") {
                                onNavigateToHub(HubSection.POSTS)
                            }
                            "connect" -> AnthropicHubQuickLinkButton(label = "View Outreach Pipeline →") {
                                onNavigateToHub(HubSection.CONNECT)
                            }
                            "inbox" -> AnthropicHubQuickLinkButton(label = "Open Inbox Sweep →") {
                                onNavigateToHub(HubSection.INBOX)
                            }
                            "profile" -> AnthropicHubQuickLinkButton(label = "View Profile Comparison →") {
                                onNavigateToHub(HubSection.PROFILE)
                            }
                            "email" -> AnthropicHubQuickLinkButton(label = "Check Email Logs →") {
                                onNavigateToHub(HubSection.EMAIL)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Serif,
                color = CharcoalLight,
                modifier = Modifier.padding(horizontal = 6.dp)
            )
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MutedBeigeCard)
                    .border(1.dp, WarmStoneBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User",
                    tint = CharcoalPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun AnthropicActionTag(actionType: String) {
    val (label, icon, color) = when (actionType) {
        "post" -> Triple("LinkedIn Feed Post Live", Icons.Outlined.Create, TerracottaAccent)
        "connect" -> Triple("Connection Invitation", Icons.Outlined.PersonAdd, DarkSage)
        "inbox" -> Triple("Inbox Auto-Sweep", Icons.Outlined.QuestionAnswer, DarkSage)
        "profile" -> Triple("Profile Optimization", Icons.Outlined.AutoAwesome, DarkAmber)
        "email" -> Triple("SMTP Email Alert", Icons.Outlined.Email, TerracottaAccent)
        else -> Triple("Aegis Assistant", Icons.Outlined.SmartToy, CharcoalPrimary)
    }

    Surface(
        color = MutedBeigeCard,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WarmStoneBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Serif,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AnthropicHubQuickLinkButton(label: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 4.dp),
        modifier = Modifier.height(28.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontFamily = FontFamily.Serif,
            color = TerracottaAccent,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AnthropicQuickActionsBar(
    onSelectAction: (String) -> Unit,
    isBusy: Boolean
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AnthropicQuickActionChip(
            emoji = "📝",
            label = "Publish Post",
            prompt = "Post: Excited to announce our autonomous AI agent workflows! #AIAgents #Innovation",
            onSelect = onSelectAction,
            enabled = !isBusy
        )
        AnthropicQuickActionChip(
            emoji = "🤝",
            label = "Connect",
            prompt = "Connect to https://linkedin.com/in/marcus-vance-pm",
            onSelect = onSelectAction,
            enabled = !isBusy
        )
        AnthropicQuickActionChip(
            emoji = "📬",
            label = "Sweep Inbox",
            prompt = "Sweep inbox and draft smart replies",
            onSelect = onSelectAction,
            enabled = !isBusy
        )
        AnthropicQuickActionChip(
            emoji = "✨",
            label = "Optimize Profile",
            prompt = "Optimize my LinkedIn headline and summary",
            onSelect = onSelectAction,
            enabled = !isBusy
        )
        AnthropicQuickActionChip(
            emoji = "✉️",
            label = "Send Email",
            prompt = "Send email to team@company.com, subject: Status Update, body: All systems running autonomously.",
            onSelect = onSelectAction,
            enabled = !isBusy
        )
    }
}

@Composable
fun AnthropicQuickActionChip(
    emoji: String,
    label: String,
    prompt: String,
    onSelect: (String) -> Unit,
    enabled: Boolean
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SoftCreamSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, WarmStoneBorder),
        modifier = Modifier.clickable(enabled = enabled) { onSelect(prompt) }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = emoji, fontSize = 13.sp)
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontFamily = FontFamily.Serif,
                color = CharcoalPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
