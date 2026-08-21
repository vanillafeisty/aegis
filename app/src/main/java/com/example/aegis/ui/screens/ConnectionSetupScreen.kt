package com.example.aegis.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aegis.ui.theme.*
import com.example.aegis.ui.viewmodel.AegisViewModel
import com.example.aegis.ui.viewmodel.AppTab

enum class SetupStep {
    LINKEDIN,
    GMAIL,
    AI,
    REVIEW
}

@Composable
fun ConnectionSetupScreen(
    viewModel: AegisViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current

    var currentStep by remember { mutableStateOf(SetupStep.LINKEDIN) }

    var liCookie by remember(settings) { mutableStateOf(settings?.linkedinSessionCookie ?: "AQEDAQ8xXy-aegis-session-cookie...") }
    var liToken by remember(settings) { mutableStateOf(settings?.linkedinAccessToken ?: "AQV9K7L-aegis-token...") }
    var liClientId by remember(settings) { mutableStateOf(settings?.linkedinClientId ?: "78aegis920") }

    var smtpEmail by remember(settings) { mutableStateOf(settings?.smtpEmail ?: "agent.aegis@gmail.com") }
    var smtpPass by remember(settings) { mutableStateOf(settings?.smtpPassword ?: "abcd efgh ijkl mnop") }
    var hidePassword by remember { mutableStateOf(true) }

    var apiKey by remember(settings) { mutableStateOf(settings?.openaiApiKey ?: "sk-aegis-core-key-active") }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Card
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
                        Text(text = "🛡️", fontSize = 20.sp)
                    }
                    Column {
                        Text(
                            text = "Aegis Connection Wizard",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "Configure LinkedIn, Gmail, and AI tokens for autonomous execution",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Progress Step Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StepIndicatorItem(
                        number = 1,
                        label = "LinkedIn",
                        isActive = currentStep == SetupStep.LINKEDIN,
                        isDone = currentStep.ordinal > SetupStep.LINKEDIN.ordinal
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
                    StepIndicatorItem(
                        number = 2,
                        label = "Gmail",
                        isActive = currentStep == SetupStep.GMAIL,
                        isDone = currentStep.ordinal > SetupStep.GMAIL.ordinal
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
                    StepIndicatorItem(
                        number = 3,
                        label = "AI Core",
                        isActive = currentStep == SetupStep.AI,
                        isDone = currentStep.ordinal > SetupStep.AI.ordinal
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
                    StepIndicatorItem(
                        number = 4,
                        label = "Review",
                        isActive = currentStep == SetupStep.REVIEW,
                        isDone = false
                    )
                }
            }
        }

        // Active Step Content
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (currentStep) {
                    SetupStep.LINKEDIN -> {
                        Text(
                            text = "1. LinkedIn Authentication",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen
                        )
                        Text(
                            text = "Session cookies (li_at) allow Aegis stealth containers to securely interact with your feed and DMs locally.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = liCookie,
                            onValueChange = { liCookie = it },
                            label = { Text("LinkedIn Session Cookie (li_at)") },
                            placeholder = { Text("AQEDAQ8xXy...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_li_cookie"),
                            shape = RoundedCornerShape(10.dp),
                            minLines = 2,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DarkGreen,
                                unfocusedBorderColor = BorderColor
                            )
                        )

                        OutlinedTextField(
                            value = liToken,
                            onValueChange = { liToken = it },
                            label = { Text("LinkedIn Access Token") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DarkGreen,
                                unfocusedBorderColor = BorderColor
                            )
                        )

                        OutlinedTextField(
                            value = liClientId,
                            onValueChange = { liClientId = it },
                            label = { Text("LinkedIn Client ID (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DarkGreen,
                                unfocusedBorderColor = BorderColor
                            )
                        )
                    }

                    SetupStep.GMAIL -> {
                        Text(
                            text = "2. Gmail SMTP Dispatcher",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen
                        )
                        Text(
                            text = "Enables automated email notifications and verification delivery via SSL port 465.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = smtpEmail,
                            onValueChange = { smtpEmail = it },
                            label = { Text("Gmail Address") },
                            placeholder = { Text("your.email@gmail.com") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_smtp_email"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DarkGreen,
                                unfocusedBorderColor = BorderColor
                            )
                        )

                        OutlinedTextField(
                            value = smtpPass,
                            onValueChange = { smtpPass = it },
                            label = { Text("Google App Password (16-char)") },
                            placeholder = { Text("•••• •••• •••• ••••") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_smtp_password"),
                            shape = RoundedCornerShape(10.dp),
                            visualTransformation = if (hidePassword) PasswordVisualTransformation() else VisualTransformation.None,
                            trailingIcon = {
                                IconButton(onClick = { hidePassword = !hidePassword }) {
                                    Icon(
                                        imageVector = if (hidePassword) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                        contentDescription = "Toggle password visibility"
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DarkGreen,
                                unfocusedBorderColor = BorderColor
                            )
                        )
                    }

                    SetupStep.AI -> {
                        Text(
                            text = "3. AI Agent Core Intelligence",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen
                        )
                        Text(
                            text = "Powers high-impact copywriting, custom DM replies, keyword optimization, and command synthesis.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = apiKey,
                            onValueChange = { apiKey = it },
                            label = { Text("AI API Key (Groq / Gemini / OpenAI)") },
                            placeholder = { Text("sk-...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_ai_key"),
                            shape = RoundedCornerShape(10.dp),
                            minLines = 2,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DarkGreen,
                                unfocusedBorderColor = BorderColor
                            )
                        )
                    }

                    SetupStep.REVIEW -> {
                        Text(
                            text = "4. Review Configuration",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen
                        )
                        Text(
                            text = "Confirm all credentials before activating the live autonomous agent platform.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        ReviewItemCard(
                            emoji = "💼",
                            title = "LinkedIn Integration",
                            subtitle = if (liCookie.isNotBlank()) "Session Cookie & Access Token Configured ✓" else "Not Configured",
                            isReady = liCookie.isNotBlank()
                        )

                        ReviewItemCard(
                            emoji = "📧",
                            title = "Gmail SMTP Server",
                            subtitle = if (smtpEmail.isNotBlank()) "$smtpEmail (SSL:465) ✓" else "Not Configured",
                            isReady = smtpEmail.isNotBlank()
                        )

                        ReviewItemCard(
                            emoji = "🤖",
                            title = "AI Agent Pipeline",
                            subtitle = if (apiKey.isNotBlank()) "AI completion core online ✓" else "Not Configured",
                            isReady = apiKey.isNotBlank()
                        )
                    }
                }

                // Action Navigation Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (currentStep != SetupStep.LINKEDIN) {
                        OutlinedButton(
                            onClick = {
                                currentStep = when (currentStep) {
                                    SetupStep.GMAIL -> SetupStep.LINKEDIN
                                    SetupStep.AI -> SetupStep.GMAIL
                                    SetupStep.REVIEW -> SetupStep.AI
                                    else -> SetupStep.LINKEDIN
                                }
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Back")
                        }
                    } else {
                        Button(
                            onClick = {
                                liCookie = "AQEDAQ8xXy-aegis-cookie-session-active"
                                liToken = "AQV9K7L-aegis-token-prod"
                                smtpEmail = "agent.aegis@gmail.com"
                                smtpPass = "aegis app pass key"
                                apiKey = "sk-aegis-ai-agent-core"
                                Toast.makeText(context, "Sample credentials populated!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Prefill Sample", color = DarkGreen)
                        }
                    }

                    if (currentStep != SetupStep.REVIEW) {
                        Button(
                            onClick = {
                                currentStep = when (currentStep) {
                                    SetupStep.LINKEDIN -> SetupStep.GMAIL
                                    SetupStep.GMAIL -> SetupStep.AI
                                    SetupStep.AI -> SetupStep.REVIEW
                                    else -> SetupStep.REVIEW
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Next →")
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.saveCredentials(liCookie, liToken, liClientId, apiKey, smtpEmail, smtpPass)
                                Toast.makeText(context, "🛡️ Aegis Activated Successfully!", Toast.LENGTH_SHORT).show()
                                viewModel.setTab(AppTab.CHAT)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_activate_aegis")
                        ) {
                            Text("Activate Aegis Agent ✓")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepIndicatorItem(
    number: Int,
    label: String,
    isActive: Boolean,
    isDone: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    if (isDone) EmeraldAccent
                    else if (isActive) DarkGreen
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Text(text = "✓", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            } else {
                Text(
                    text = "$number",
                    color = if (isActive) PureWhite else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            color = if (isActive) DarkGreen else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ReviewItemCard(
    emoji: String,
    title: String,
    subtitle: String,
    isReady: Boolean
) {
    Surface(
        color = if (isReady) LightSage.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isReady) EmeraldAccent.copy(alpha = 0.4f) else BorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = emoji, fontSize = 22.sp)
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = if (isReady) DarkGreen else MutedText)
            }
            if (isReady) {
                Text(text = "✓", color = EmeraldAccent, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}
