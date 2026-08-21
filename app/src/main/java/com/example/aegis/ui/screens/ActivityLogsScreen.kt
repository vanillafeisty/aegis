package com.example.aegis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aegis.data.model.ActivityLogEntity
import com.example.aegis.ui.theme.*
import com.example.aegis.ui.viewmodel.AegisViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ActivityLogsScreen(
    viewModel: AegisViewModel,
    modifier: Modifier = Modifier
) {
    val activities by viewModel.activities.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") }
    val filterScrollState = rememberScrollState()

    val filteredActivities = remember(activities, selectedFilter) {
        if (selectedFilter == "ALL") activities
        else activities.filter { it.type == selectedFilter }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
                        Text(text = "📊", fontSize = 20.sp)
                        Text(
                            text = "Activity & Audit Logs",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    }

                    TextButton(
                        onClick = { viewModel.clearLogs() },
                        colors = ButtonDefaults.textButtonColors(contentColor = CrimsonError)
                    ) {
                        Icon(Icons.Outlined.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear Logs")
                    }
                }

                // Filter chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(filterScrollState),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LogFilterChip("ALL", "All Logs (${activities.size})", selectedFilter == "ALL") { selectedFilter = "ALL" }
                    LogFilterChip("POST", "Posts", selectedFilter == "POST") { selectedFilter = "POST" }
                    LogFilterChip("CONNECT", "Connect", selectedFilter == "CONNECT") { selectedFilter = "CONNECT" }
                    LogFilterChip("INBOX_REPLY", "Inbox", selectedFilter == "INBOX_REPLY") { selectedFilter = "INBOX_REPLY" }
                    LogFilterChip("PROFILE_TWEAK", "Profile", selectedFilter == "PROFILE_TWEAK") { selectedFilter = "PROFILE_TWEAK" }
                    LogFilterChip("EMAIL", "Email", selectedFilter == "EMAIL") { selectedFilter = "EMAIL" }
                    LogFilterChip("SETUP", "Setup", selectedFilter == "SETUP") { selectedFilter = "SETUP" }
                }
            }
        }

        // Timeline List
        if (filteredActivities.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "🛡️", fontSize = 48.sp)
                    Text(
                        text = "No Activities Recorded Yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Automated tasks, feed posts, and connection events will appear here in real-time.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredActivities, key = { it.id }) { activity ->
                    ActivityLogItem(activity = activity)
                }
            }
        }
    }
}

@Composable
fun LogFilterChip(
    filterKey: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label, fontSize = 12.sp) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = DarkGreen,
            selectedLabelColor = PureWhite,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.testTag("filter_chip_$filterKey")
    )
}

@Composable
fun ActivityLogItem(activity: ActivityLogEntity) {
    val formatter = remember { SimpleDateFormat("MMM d • hh:mm:ss a", Locale.getDefault()) }
    val formattedTime = remember(activity.timestamp) { formatter.format(Date(activity.timestamp)) }

    val (icon, tintColor) = when (activity.type) {
        "POST" -> Pair(Icons.Outlined.Create, DarkGreen)
        "CONNECT" -> Pair(Icons.Outlined.PersonAdd, DarkGreen)
        "INBOX_REPLY" -> Pair(Icons.Outlined.QuestionAnswer, DarkGreen)
        "PROFILE_TWEAK" -> Pair(Icons.Outlined.AutoAwesome, AmberAccent)
        "EMAIL" -> Pair(Icons.Outlined.Email, DarkGreen)
        "SETUP" -> Pair(Icons.Outlined.Settings, EmeraldAccent)
        else -> Pair(Icons.Outlined.Info, DarkGreen)
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(tintColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tintColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = activity.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = activity.detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (activity.status == "SUCCESS") EmeraldAccent else AmberAccent)
                    )
                    Text(
                        text = activity.status,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (activity.status == "SUCCESS") EmeraldAccent else AmberAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
