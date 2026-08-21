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

    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messages.size, isBusy) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
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
                ChatMessageItem(
                    message = message,
                    onNavigateToHub = { section ->
                        viewModel.setHubSection(section)
                        viewModel.setTab(AppTab.HUB)
                    }
                )
            }
        }

        // Quick Action Chips Row
        QuickActionsBar(
            onSelectAction = { promptText ->
                textInput = promptText
            },
            isBusy = isBusy
        )

        // Bottom Chat Input Box
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
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
                                text = "Tell Aegis what to automate...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkGreen,
                            unfocusedBorderColor = BorderColor,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = false,
                        maxLines = 3,
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
                            .size(50.dp),
                        shape = CircleShape,
                        containerColor = if (textInput.isNotBlank() && !isBusy) DarkGreen else SageGreen.copy(alpha = 0.5f),
                        contentColor = PureWhite,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp)
                    ) {
                        if (isBusy) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = PureWhite,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "💡 Try: \"Post: Scaling our autonomous workflows\" or \"Check messages\"",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(
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
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(DarkGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🛡️", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 320.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                color = if (isUser) DarkGreen else MaterialTheme.colorScheme.surface,
                tonalElevation = if (isUser) 0.dp else 2.dp,
                shadowElevation = 1.dp,
                border = if (isUser) null else androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!isUser && message.actionType != null) {
                        ActionTag(actionType = message.actionType)
                    }

                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isUser) PureWhite else MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )

                    // Navigation deep links for quick agent interactions
                    if (!isUser && message.actionType != null) {
                        when (message.actionType) {
                            "post" -> HubQuickLinkButton(label = "View Post History →") {
                                onNavigateToHub(HubSection.POSTS)
                            }
                            "connect" -> HubQuickLinkButton(label = "View Outreach Pipeline →") {
                                onNavigateToHub(HubSection.CONNECT)
                            }
                            "inbox" -> HubQuickLinkButton(label = "Open Inbox Sweep →") {
                                onNavigateToHub(HubSection.INBOX)
                            }
                            "profile" -> HubQuickLinkButton(label = "View Profile Comparison →") {
                                onNavigateToHub(HubSection.PROFILE)
                            }
                            "email" -> HubQuickLinkButton(label = "Check Email Logs →") {
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 6.dp)
            )
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(LightSage),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "User",
                    tint = DarkGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ActionTag(actionType: String) {
    val (label, icon, color) = when (actionType) {
        "post" -> Triple("LinkedIn Feed Post", Icons.Outlined.Create, DarkGreen)
        "connect" -> Triple("Outbound Connect", Icons.Outlined.PersonAdd, DarkGreen)
        "inbox" -> Triple("Inbox Auto-Reply", Icons.Outlined.QuestionAnswer, DarkGreen)
        "profile" -> Triple("Profile Optimization", Icons.Outlined.AutoAwesome, AmberAccent)
        "email" -> Triple("SMTP Email Alert", Icons.Outlined.Email, DarkGreen)
        else -> Triple("Aegis Assistant", Icons.Outlined.SmartToy, DarkGreen)
    }

    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp)
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
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun HubQuickLinkButton(label: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 4.dp),
        modifier = Modifier.height(28.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = DarkGreen,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun QuickActionsBar(
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
        QuickActionChip(
            emoji = "📝",
            label = "Publish Post",
            prompt = "Post: Check out our newest autonomous AI system release! 🚀 #AIAgents #Innovation",
            onSelect = onSelectAction,
            enabled = !isBusy
        )
        QuickActionChip(
            emoji = "🤝",
            label = "Connect",
            prompt = "Connect to https://linkedin.com/in/marcus-vance-pm",
            onSelect = onSelectAction,
            enabled = !isBusy
        )
        QuickActionChip(
            emoji = "📬",
            label = "Sweep Inbox",
            prompt = "Sweep inbox and reply to unread messages",
            onSelect = onSelectAction,
            enabled = !isBusy
        )
        QuickActionChip(
            emoji = "⚙️",
            label = "Tune Profile",
            prompt = "Optimize my profile headline and summary",
            onSelect = onSelectAction,
            enabled = !isBusy
        )
        QuickActionChip(
            emoji = "📧",
            label = "SMTP Alert",
            prompt = "Send email to verification@company.com, subject: Aegis Alert, body: System status confirmed online.",
            onSelect = onSelectAction,
            enabled = !isBusy
        )
    }
}

@Composable
fun QuickActionChip(
    emoji: String,
    label: String,
    prompt: String,
    onSelect: (String) -> Unit,
    enabled: Boolean
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        modifier = Modifier.clickable(enabled = enabled) { onSelect(prompt) }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = emoji, fontSize = 14.sp)
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
