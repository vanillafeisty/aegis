package com.example.aegis.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aegis.data.model.*
import com.example.aegis.ui.theme.*
import com.example.aegis.ui.viewmodel.AegisViewModel
import com.example.aegis.ui.viewmodel.HubSection
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AutomationHubScreen(
    viewModel: AegisViewModel,
    modifier: Modifier = Modifier
) {
    val activeSection by viewModel.hubSection.collectAsState()
    val isBusy by viewModel.isAgentBusy.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Hub Sub-Navigation Tabs
        ScrollableTabRow(
            selectedTabIndex = activeSection.ordinal,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = DarkGreen,
            edgePadding = 16.dp,
            divider = { HorizontalDivider(color = BorderColor) }
        ) {
            HubTabItem(
                title = "Feed Posts",
                icon = Icons.Outlined.Create,
                selected = activeSection == HubSection.POSTS,
                onClick = { viewModel.setHubSection(HubSection.POSTS) }
            )
            HubTabItem(
                title = "Connect Leads",
                icon = Icons.Outlined.PersonAdd,
                selected = activeSection == HubSection.CONNECT,
                onClick = { viewModel.setHubSection(HubSection.CONNECT) }
            )
            HubTabItem(
                title = "Inbox Sweep",
                icon = Icons.Outlined.QuestionAnswer,
                selected = activeSection == HubSection.INBOX,
                onClick = { viewModel.setHubSection(HubSection.INBOX) }
            )
            HubTabItem(
                title = "Profile Audit",
                icon = Icons.Outlined.AutoAwesome,
                selected = activeSection == HubSection.PROFILE,
                onClick = { viewModel.setHubSection(HubSection.PROFILE) }
            )
            HubTabItem(
                title = "SMTP Email",
                icon = Icons.Outlined.Email,
                selected = activeSection == HubSection.EMAIL,
                onClick = { viewModel.setHubSection(HubSection.EMAIL) }
            )
        }

        // Active Section Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeSection) {
                HubSection.POSTS -> PostsSection(viewModel = viewModel, isBusy = isBusy)
                HubSection.CONNECT -> ConnectSection(viewModel = viewModel, isBusy = isBusy)
                HubSection.INBOX -> InboxSection(viewModel = viewModel, isBusy = isBusy)
                HubSection.PROFILE -> ProfileSection(viewModel = viewModel, isBusy = isBusy)
                HubSection.EMAIL -> EmailSection(viewModel = viewModel, isBusy = isBusy)
            }
        }
    }
}

@Composable
fun HubTabItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Tab(
        selected = selected,
        onClick = onClick,
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (selected) DarkGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = title,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = if (selected) DarkGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

// ----------------------------------------------------
// 1. POSTS SECTION
// ----------------------------------------------------
@Composable
fun PostsSection(viewModel: AegisViewModel, isBusy: Boolean) {
    val posts by viewModel.posts.collectAsState()
    var topicInput by remember { mutableStateOf("") }
    var selectedTone by remember { mutableStateOf("Thought Leadership") }
    val tones = listOf("Thought Leadership", "Actionable Insights", "Storytelling / Journey")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "✍️", fontSize = 20.sp)
                        Text(
                            text = "AI LinkedIn Post Composer",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    OutlinedTextField(
                        value = topicInput,
                        onValueChange = { topicInput = it },
                        label = { Text("What would you like to post about?") },
                        placeholder = { Text("e.g. Scaling autonomous agents with Playwright & LLMs...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("post_topic_input"),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2,
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkGreen,
                            unfocusedBorderColor = BorderColor
                        )
                    )

                    Text(
                        text = "Select Post Tone:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tones.forEach { tone ->
                            FilterChip(
                                selected = selectedTone == tone,
                                onClick = { selectedTone = tone },
                                label = { Text(tone, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LightSage,
                                    selectedLabelColor = DarkGreen
                                )
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val topic = topicInput.ifBlank { "Excited to share our latest milestone in AI agent automation!" }
                            viewModel.publishCustomPost(topic, selectedTone)
                            topicInput = ""
                        },
                        enabled = !isBusy,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_publish_post"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                    ) {
                        if (isBusy) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = PureWhite, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Stealth Publishing...")
                        } else {
                            Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate & Publish to Feed")
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Published & Queued Posts (${posts.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(posts, key = { it.id }) { post ->
            PostCardItem(post = post, onDelete = { viewModel.deletePost(post) })
        }
    }
}

@Composable
fun PostCardItem(post: PostDraftEntity, onDelete: () -> Unit) {
    val formatter = remember { SimpleDateFormat("MMM d, yyyy • hh:mm a", Locale.getDefault()) }
    val formattedDate = remember(post.createdAt) { formatter.format(Date(post.createdAt)) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DarkGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👤", fontSize = 16.sp)
                    }
                    Column {
                        Text(
                            text = "LinkedIn Feed Post",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = if (post.status == "Published") EmeraldAccent.copy(alpha = 0.15f) else AmberAccent.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = post.status,
                        color = if (post.status == "Published") DarkGreen else AmberAccent,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = post.tone,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        tint = CrimsonError,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// 2. CONNECT LEADS SECTION
// ----------------------------------------------------
@Composable
fun ConnectSection(viewModel: AegisViewModel, isBusy: Boolean) {
    val leads by viewModel.leads.collectAsState()
    var urlInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var headlineInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🤝", fontSize = 20.sp)
                        Text(
                            text = "Outbound Outreach Pipeline",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        label = { Text("LinkedIn Profile URL") },
                        placeholder = { Text("https://linkedin.com/in/prospect-username") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_lead_url"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkGreen,
                            unfocusedBorderColor = BorderColor
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Prospect Name (Optional)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DarkGreen,
                                unfocusedBorderColor = BorderColor
                            )
                        )
                        OutlinedTextField(
                            value = headlineInput,
                            onValueChange = { headlineInput = it },
                            label = { Text("Headline / Role") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DarkGreen,
                                unfocusedBorderColor = BorderColor
                            )
                        )
                    }

                    Button(
                        onClick = {
                            val url = if (urlInput.startsWith("https://")) urlInput else "https://linkedin.com/in/prospect"
                            viewModel.addLead(url, nameInput, headlineInput, "")
                            urlInput = ""
                            nameInput = ""
                            headlineInput = ""
                        },
                        enabled = !isBusy,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_send_connect"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                    ) {
                        Icon(Icons.Filled.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Draft AI Note & Send Invite")
                    }
                }
            }
        }

        item {
            Text(
                text = "Outbound Pipeline Leads (${leads.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(leads, key = { it.id }) { lead ->
            LeadCardItem(lead = lead, onDelete = { viewModel.deleteLead(lead) })
        }
    }
}

@Composable
fun LeadCardItem(lead: ConnectionLeadEntity, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(LightSage),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = lead.fullName.take(1).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen
                        )
                    }
                    Column {
                        Text(
                            text = lead.fullName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = lead.headline,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                Surface(
                    color = when (lead.status) {
                        "Connected" -> EmeraldAccent.copy(alpha = 0.15f)
                        "Sent" -> SageGreen.copy(alpha = 0.2f)
                        else -> AmberAccent.copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = lead.status,
                        color = if (lead.status == "Connected") DarkGreen else DarkGreen,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            if (lead.personalizedNote.isNotBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "AI Personalized Invite Note:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedText,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "\"${lead.personalizedNote}\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = FontFamily.Serif
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = lead.profileUrl,
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkGreen,
                    maxLines = 1
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        tint = CrimsonError,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// 3. INBOX SECTION
// ----------------------------------------------------
@Composable
fun InboxSection(viewModel: AegisViewModel, isBusy: Boolean) {
    val threads by viewModel.threads.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "📬", fontSize = 20.sp)
                            Text(
                                text = "Smart Inbox & DM Auto-Reply",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "Aegis scans unread direct messages, extracts conversation context, and drafts natural, concise professional replies (under 3 sentences) ready for single-click dispatch.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = { viewModel.triggerInboxSweep() },
                        enabled = !isBusy,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_sweep_inbox"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Run 1-Click Inbox Sweep & Auto-Draft")
                    }
                }
            }
        }

        item {
            Text(
                text = "Active Direct Message Threads (${threads.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(threads, key = { it.id }) { thread ->
            InboxThreadCard(thread = thread, onReply = { reply ->
                viewModel.replyToThread(thread, reply)
            })
        }
    }
}

@Composable
fun InboxThreadCard(thread: InboxThreadEntity, onReply: (String) -> Unit) {
    var replyText by remember(thread.aiDraftReply) { mutableStateOf(thread.aiDraftReply) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(LightSage),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = thread.senderName.take(1),
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen
                        )
                    }
                    Column {
                        Text(
                            text = thread.senderName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = thread.senderHeadline,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                Surface(
                    color = if (thread.isReplied) EmeraldAccent.copy(alpha = 0.15f) else AmberAccent.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (thread.isReplied) "Replied ✓" else "Unread (1)",
                        color = if (thread.isReplied) DarkGreen else AmberAccent,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Incoming message bubble
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Incoming Message:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedText,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"${thread.lastMessage}\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // AI Reply Area
            OutlinedTextField(
                value = replyText,
                onValueChange = { replyText = it },
                label = { Text("AI Generated Reply (Editable)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                minLines = 2,
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DarkGreen,
                    unfocusedBorderColor = BorderColor
                )
            )

            Button(
                onClick = { onReply(replyText) },
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Approve & Send DM")
            }
        }
    }
}

// ----------------------------------------------------
// 4. PROFILE SECTION
// ----------------------------------------------------
@Composable
fun ProfileSection(viewModel: AegisViewModel, isBusy: Boolean) {
    val audits by viewModel.audits.collectAsState()
    val latestAudit = audits.firstOrNull()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    var currentHeadline by remember { mutableStateOf("Lead Software Engineer & AI System Architect") }
    var currentAbout by remember { mutableStateOf("Experienced engineer building scalable cloud systems and autonomous workflows with Kotlin and Python.") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "⚙️", fontSize = 20.sp)
                        Text(
                            text = "LinkedIn Profile AI Optimizer",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Aegis brand strategist analyzes your headline & summary, applying high-impact copywriting and SEO keywords to maximize profile visits and recruiter inquiries.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = currentHeadline,
                        onValueChange = { currentHeadline = it },
                        label = { Text("Current Headline") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkGreen,
                            unfocusedBorderColor = BorderColor
                        )
                    )

                    OutlinedTextField(
                        value = currentAbout,
                        onValueChange = { currentAbout = it },
                        label = { Text("Current About / Summary") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkGreen,
                            unfocusedBorderColor = BorderColor
                        )
                    )

                    Button(
                        onClick = { viewModel.triggerProfileOptimization(currentHeadline, currentAbout) },
                        enabled = !isBusy,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_optimize_profile"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Audit & Generate High-Impact Copy")
                    }
                }
            }
        }

        if (latestAudit != null) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✨ AI Recommended Optimization",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DarkGreen
                            )
                            Surface(
                                color = EmeraldAccent.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Score: ${latestAudit.score}/100",
                                    color = DarkGreen,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Headline Comparison
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "OPTIMIZED HEADLINE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MutedText
                            )
                            Surface(
                                color = LightSage.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = latestAudit.optimizedHeadline,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DarkGreen,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        // About Section Comparison
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "OPTIMIZED ABOUT SECTION",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MutedText
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = latestAudit.optimizedAbout,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(10.dp),
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString("${latestAudit.optimizedHeadline}\n\n${latestAudit.optimizedAbout}"))
                                    Toast.makeText(context, "Copied profile copy to clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Outlined.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Copy Copy")
                            }

                            Button(
                                onClick = {
                                    Toast.makeText(context, "Profile tweaks applied to sync queue!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                            ) {
                                Icon(Icons.Filled.Done, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sync to LinkedIn")
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 5. EMAIL SECTION
// ----------------------------------------------------
@Composable
fun EmailSection(viewModel: AegisViewModel, isBusy: Boolean) {
    val emails by viewModel.emails.collectAsState()
    var recipient by remember { mutableStateOf("team@company.com") }
    var subject by remember { mutableStateOf("Aegis AI Agent Alert: Daily Outreach Report") }
    var body by remember {
        mutableStateOf(
            "Hello!\n\nThis is an automated report from your Aegis Autonomous LinkedIn Agent.\n" +
            "• Outbound requests sent: 12\n• Replies drafted: 4\n• Feed updates published: 1\n\nAll tasks operational."
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "📧", fontSize = 20.sp)
                        Text(
                            text = "SMTP Email Verification & Alerts",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedTextField(
                        value = recipient,
                        onValueChange = { recipient = it },
                        label = { Text("Recipient Email Address") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_email_recipient"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkGreen,
                            unfocusedBorderColor = BorderColor
                        )
                    )

                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Subject") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkGreen,
                            unfocusedBorderColor = BorderColor
                        )
                    )

                    OutlinedTextField(
                        value = body,
                        onValueChange = { body = it },
                        label = { Text("Email Body") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 3,
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkGreen,
                            unfocusedBorderColor = BorderColor
                        )
                    )

                    Button(
                        onClick = { viewModel.sendEmail(recipient, subject, body) },
                        enabled = !isBusy,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_send_smtp_email"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                    ) {
                        Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send via Gmail SMTP (SSL:465)")
                    }
                }
            }
        }

        item {
            Text(
                text = "Dispatched Email Log (${emails.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(emails, key = { it.id }) { email ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = email.recipientEmail, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text(text = "Sent ✓", color = EmeraldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Text(text = email.subject, style = MaterialTheme.typography.bodyMedium, color = DarkGreen)
                    Text(text = email.body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                }
            }
        }
    }
}
