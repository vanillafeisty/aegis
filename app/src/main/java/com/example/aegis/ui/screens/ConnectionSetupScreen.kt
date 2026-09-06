package com.example.aegis.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aegis.ui.theme.*
import com.example.aegis.ui.viewmodel.AegisViewModel
import com.example.aegis.ui.viewmodel.AppTab

enum class ConnectStep {
    LINKEDIN,
    GMAIL,
    AI_ENGINE,
    SUMMARY
}

@Composable
fun ConnectionSetupScreen(
    viewModel: AegisViewModel
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()

    var activeStep by remember { mutableStateOf(ConnectStep.LINKEDIN) }

    var liToken by remember(settings) {
        mutableStateOf(settings?.linkedinAccessToken ?: "")
    }
    var liCookie by remember(settings) {
        mutableStateOf(settings?.linkedinSessionCookie ?: "")
    }
    var liClientId by remember(settings) {
        mutableStateOf(settings?.linkedinClientId ?: "")
    }
    var liClientSecret by remember(settings) {
        mutableStateOf(settings?.linkedinClientSecret ?: "")
    }
    var liRedirectUri by remember(settings) {
        mutableStateOf(settings?.linkedinRedirectUri?.ifBlank { "http://localhost:8000/callback" } ?: "http://localhost:8000/callback")
    }

    var smtpEmail by remember(settings) {
        mutableStateOf(settings?.smtpEmail ?: "")
    }
    var smtpPass by remember(settings) {
        mutableStateOf(settings?.smtpPassword ?: "")
    }
    var hidePassword by remember { mutableStateOf(true) }

    var groqKey by remember(settings) {
        mutableStateOf(settings?.groqApiKey ?: "")
    }
    var groqModel by remember(settings) {
        mutableStateOf(settings?.groqModel?.ifBlank { "llama-3.3-70b-versatile" } ?: "llama-3.3-70b-versatile")
    }
    var zapierUrl by remember(settings) {
        mutableStateOf(settings?.zapierMcpUrl?.ifBlank { "https://mcp.zapier.com/api/v1/connect" } ?: "https://mcp.zapier.com/api/v1/connect")
    }

    var isTestingConnection by remember { mutableStateOf(false) }
    var testResultText by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    fun persistAll() {
        viewModel.saveCredentials(
            liCookie = liCookie,
            liToken = liToken,
            liClientId = liClientId,
            liClientSecret = liClientSecret,
            liRedirectUri = liRedirectUri,
            groqKey = groqKey,
            groqModel = groqModel,
            zapierUrl = zapierUrl,
            smtpEmail = smtpEmail,
            smtpPass = smtpPass
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmBeigeBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.setTab(AppTab.OPENING) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Welcome",
                        tint = CharcoalPrimary
                    )
                }

                Text(
                    text = "CONNECT WORKSPACE",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.5.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalLight
                )

                TextButton(
                    onClick = {
                        persistAll()
                        Toast.makeText(context, "Credentials Saved ✓", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(
                        text = "Save",
                        color = TerracottaAccent,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Title & Subtitle
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Connect LinkedIn & Gmail",
                    style = MaterialTheme.typography.headlineLarge,
                    fontFamily = FontFamily.Serif,
                    color = CharcoalPrimary
                )
                Text(
                    text = "Configure your access credentials so Aegis can autonomously draft posts, outreach connections, and sweep your inbox.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CharcoalMuted,
                    lineHeight = 20.sp
                )
            }

            // Tab / Step Selector (Anthropic Style Pill Cards)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StepChip(
                    title = "LinkedIn",
                    isSelected = activeStep == ConnectStep.LINKEDIN,
                    isDone = liToken.isNotBlank() || liCookie.isNotBlank(),
                    onClick = { activeStep = ConnectStep.LINKEDIN },
                    modifier = Modifier.weight(1f)
                )
                StepChip(
                    title = "Gmail",
                    isSelected = activeStep == ConnectStep.GMAIL,
                    isDone = smtpEmail.isNotBlank() && smtpPass.isNotBlank(),
                    onClick = { activeStep = ConnectStep.GMAIL },
                    modifier = Modifier.weight(1f)
                )
                StepChip(
                    title = "Groq AI",
                    isSelected = activeStep == ConnectStep.AI_ENGINE,
                    isDone = groqKey.isNotBlank(),
                    onClick = { activeStep = ConnectStep.AI_ENGINE },
                    modifier = Modifier.weight(1f)
                )
                StepChip(
                    title = "Review",
                    isSelected = activeStep == ConnectStep.SUMMARY,
                    isDone = false,
                    onClick = { activeStep = ConnectStep.SUMMARY },
                    modifier = Modifier.weight(1f)
                )
            }

            // Step Content Cards
            Surface(
                color = SoftCreamSurface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, WarmStoneBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (activeStep) {
                        ConnectStep.LINKEDIN -> {
                            Text(
                                text = "LinkedIn Live Integration",
                                style = MaterialTheme.typography.titleLarge,
                                fontFamily = FontFamily.Serif,
                                color = CharcoalPrimary
                            )
                            Text(
                                text = "Connect via OAuth Access Token or session cookie (li_at) to post to feed and manage connections.",
                                style = MaterialTheme.typography.bodySmall,
                                color = CharcoalMuted
                            )

                            OutlinedTextField(
                                value = liToken,
                                onValueChange = { liToken = it },
                                label = { Text("LinkedIn OAuth Access Token", fontFamily = FontFamily.Serif) },
                                placeholder = { Text("AQUgHe...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_li_token"),
                                shape = RoundedCornerShape(10.dp),
                                minLines = 2,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TerracottaAccent,
                                    unfocusedBorderColor = WarmStoneBorder,
                                    focusedContainerColor = SoftCreamSurface,
                                    unfocusedContainerColor = SoftCreamSurface
                                )
                            )

                            OutlinedTextField(
                                value = liCookie,
                                onValueChange = { liCookie = it },
                                label = { Text("LinkedIn Session Cookie (li_at)", fontFamily = FontFamily.Serif) },
                                placeholder = { Text("Paste session cookie (li_at)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_li_cookie"),
                                shape = RoundedCornerShape(10.dp),
                                minLines = 2,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TerracottaAccent,
                                    unfocusedBorderColor = WarmStoneBorder,
                                    focusedContainerColor = SoftCreamSurface,
                                    unfocusedContainerColor = SoftCreamSurface
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = liClientId,
                                    onValueChange = { liClientId = it },
                                    label = { Text("Client ID", fontFamily = FontFamily.Serif) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = TerracottaAccent,
                                        unfocusedBorderColor = WarmStoneBorder,
                                        focusedContainerColor = SoftCreamSurface,
                                        unfocusedContainerColor = SoftCreamSurface
                                    )
                                )
                                OutlinedTextField(
                                    value = liClientSecret,
                                    onValueChange = { liClientSecret = it },
                                    label = { Text("Client Secret", fontFamily = FontFamily.Serif) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = TerracottaAccent,
                                        unfocusedBorderColor = WarmStoneBorder,
                                        focusedContainerColor = SoftCreamSurface,
                                        unfocusedContainerColor = SoftCreamSurface
                                    )
                                )
                            }

                            // Live Verify Button
                            Button(
                                onClick = {
                                    isTestingConnection = true
                                    testResultText = null
                                    viewModel.testLinkedInConnection { success, msg ->
                                        isTestingConnection = false
                                        testResultText = if (success) "✓ $msg" else "Status: $msg"
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MutedBeigeCard),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (isTestingConnection) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = CharcoalPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Verifying LinkedIn Token...", color = CharcoalPrimary, fontFamily = FontFamily.Serif)
                                } else {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DarkSage, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Verify Live LinkedIn Connection", color = CharcoalPrimary, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Medium)
                                }
                            }

                            testResultText?.let { res ->
                                Surface(
                                    color = if (res.startsWith("✓")) PastelSage else MutedBeigeCard,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = res,
                                        color = if (res.startsWith("✓")) DarkSage else CharcoalPrimary,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(10.dp),
                                        fontFamily = FontFamily.Serif
                                    )
                                }
                            }
                        }

                        ConnectStep.GMAIL -> {
                            Text(
                                text = "Gmail & SMTP Integration",
                                style = MaterialTheme.typography.titleLarge,
                                fontFamily = FontFamily.Serif,
                                color = CharcoalPrimary
                            )
                            Text(
                                text = "Enables autonomous email dispatches, executive meeting follow-ups, and calendar coordination.",
                                style = MaterialTheme.typography.bodySmall,
                                color = CharcoalMuted
                            )

                            OutlinedTextField(
                                value = smtpEmail,
                                onValueChange = { smtpEmail = it },
                                label = { Text("Gmail Address", fontFamily = FontFamily.Serif) },
                                placeholder = { Text("your.email@gmail.com") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_smtp_email"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TerracottaAccent,
                                    unfocusedBorderColor = WarmStoneBorder,
                                    focusedContainerColor = SoftCreamSurface,
                                    unfocusedContainerColor = SoftCreamSurface
                                )
                            )

                            OutlinedTextField(
                                value = smtpPass,
                                onValueChange = { smtpPass = it },
                                label = { Text("Google App Password (16-char)", fontFamily = FontFamily.Serif) },
                                placeholder = { Text("abcd efgh ijkl mnop") },
                                visualTransformation = if (hidePassword) PasswordVisualTransformation() else VisualTransformation.None,
                                trailingIcon = {
                                    IconButton(onClick = { hidePassword = !hidePassword }) {
                                        Icon(
                                            imageVector = if (hidePassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = null,
                                            tint = CharcoalMuted
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_smtp_pass"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TerracottaAccent,
                                    unfocusedBorderColor = WarmStoneBorder,
                                    focusedContainerColor = SoftCreamSurface,
                                    unfocusedContainerColor = SoftCreamSurface
                                )
                            )

                            Surface(
                                color = MutedBeigeCard,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("💡", fontSize = 16.sp)
                                    Text(
                                        text = "Generate a 16-character App Password from Google Account → Security → 2-Step Verification.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = CharcoalMuted
                                    )
                                }
                            }
                        }

                        ConnectStep.AI_ENGINE -> {
                            Text(
                                text = "Groq LLaMA-3.3 Intelligence",
                                style = MaterialTheme.typography.titleLarge,
                                fontFamily = FontFamily.Serif,
                                color = CharcoalPrimary
                            )
                            Text(
                                text = "High-speed Groq inference powers viral hooks, connection reasoning, and profile optimization.",
                                style = MaterialTheme.typography.bodySmall,
                                color = CharcoalMuted
                            )

                            OutlinedTextField(
                                value = groqKey,
                                onValueChange = { groqKey = it },
                                label = { Text("Groq API Key", fontFamily = FontFamily.Serif) },
                                placeholder = { Text("Enter your Groq API key") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_ai_key"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TerracottaAccent,
                                    unfocusedBorderColor = WarmStoneBorder,
                                    focusedContainerColor = SoftCreamSurface,
                                    unfocusedContainerColor = SoftCreamSurface
                                )
                            )

                            OutlinedTextField(
                                value = groqModel,
                                onValueChange = { groqModel = it },
                                label = { Text("Groq Model Identifier", fontFamily = FontFamily.Serif) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TerracottaAccent,
                                    unfocusedBorderColor = WarmStoneBorder,
                                    focusedContainerColor = SoftCreamSurface,
                                    unfocusedContainerColor = SoftCreamSurface
                                )
                            )

                            OutlinedTextField(
                                value = zapierUrl,
                                onValueChange = { zapierUrl = it },
                                label = { Text("Zapier MCP Connector URL", fontFamily = FontFamily.Serif) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TerracottaAccent,
                                    unfocusedBorderColor = WarmStoneBorder,
                                    focusedContainerColor = SoftCreamSurface,
                                    unfocusedContainerColor = SoftCreamSurface
                                )
                            )
                        }

                        ConnectStep.SUMMARY -> {
                            Text(
                                text = "Workspace Overview",
                                style = MaterialTheme.typography.titleLarge,
                                fontFamily = FontFamily.Serif,
                                color = CharcoalPrimary
                            )
                            Text(
                                text = "Verify your linked integrations before launching the conversational workspace.",
                                style = MaterialTheme.typography.bodySmall,
                                color = CharcoalMuted
                            )

                            OverviewRow(
                                emoji = "💼",
                                label = "LinkedIn Account",
                                detail = if (liToken.isNotBlank()) "OAuth Access Token Configured" else "Not Configured",
                                isReady = liToken.isNotBlank() || liCookie.isNotBlank()
                            )
                            OverviewRow(
                                emoji = "✉️",
                                label = "Gmail SMTP",
                                detail = if (smtpEmail.isNotBlank()) smtpEmail else "Not Configured",
                                isReady = smtpEmail.isNotBlank()
                            )
                            OverviewRow(
                                emoji = "⚡",
                                label = "Groq LLaMA Engine",
                                detail = if (groqKey.isNotBlank()) groqModel else "Not Configured",
                                isReady = groqKey.isNotBlank()
                            )
                            OverviewRow(
                                emoji = "🔗",
                                label = "Zapier MCP Pipeline",
                                detail = if (zapierUrl.isNotBlank()) "Connected" else "Disabled",
                                isReady = zapierUrl.isNotBlank()
                            )
                        }
                    }
                }
            }

            // Navigation Buttons (Next / Get Started)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (activeStep != ConnectStep.LINKEDIN) {
                    OutlinedButton(
                        onClick = {
                            activeStep = when (activeStep) {
                                ConnectStep.GMAIL -> ConnectStep.LINKEDIN
                                ConnectStep.AI_ENGINE -> ConnectStep.GMAIL
                                ConnectStep.SUMMARY -> ConnectStep.AI_ENGINE
                                else -> ConnectStep.LINKEDIN
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarmStoneBorder),
                        modifier = Modifier.weight(0.8f).height(52.dp)
                    ) {
                        Text("Previous", color = CharcoalPrimary, fontFamily = FontFamily.Serif)
                    }
                }

                if (activeStep != ConnectStep.SUMMARY) {
                    Button(
                        onClick = {
                            persistAll()
                            activeStep = when (activeStep) {
                                ConnectStep.LINKEDIN -> ConnectStep.GMAIL
                                ConnectStep.GMAIL -> ConnectStep.AI_ENGINE
                                ConnectStep.AI_ENGINE -> ConnectStep.SUMMARY
                                else -> ConnectStep.SUMMARY
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MutedBeigeCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(52.dp)
                    ) {
                        Text(
                            text = "Next Step →",
                            color = CharcoalPrimary,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // THE "GET STARTED" BUTTON requested by the user
                Button(
                    onClick = {
                        persistAll()
                        Toast.makeText(context, "🛡️ Workspace Activated! Opening Conversation...", Toast.LENGTH_SHORT).show()
                        viewModel.setTab(AppTab.CHAT)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TerracottaAccent,
                        contentColor = SoftCreamSurface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(52.dp)
                        .testTag("btn_get_started")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Get Started",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = FontFamily.Serif,
                            color = SoftCreamSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = SoftCreamSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StepChip(
    title: String,
    isSelected: Boolean,
    isDone: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) TerracottaAccent else if (isDone) PastelSage else MutedBeigeCard
    val textColor = if (isSelected) SoftCreamSurface else if (isDone) DarkSage else CharcoalMuted
    val borderColor = if (isSelected) TerracottaAccent else if (isDone) SageBorder else WarmStoneBorder

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontFamily = FontFamily.Serif
            )
        }
    }
}

@Composable
private fun OverviewRow(
    emoji: String,
    label: String,
    detail: String,
    isReady: Boolean
) {
    Surface(
        color = WarmBeigeBackground,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WarmStoneBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = emoji, fontSize = 18.sp)
                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.Serif,
                        color = CharcoalPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = detail,
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalMuted
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isReady) PastelSage else PastelAmber)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isReady) "✓" else "○",
                    color = if (isReady) DarkSage else DarkAmber,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
