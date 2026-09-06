package com.example.aegis.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aegis.ui.theme.*
import com.example.aegis.ui.viewmodel.AegisViewModel
import com.example.aegis.ui.viewmodel.AppTab

@Composable
fun OpeningScreen(
    viewModel: AegisViewModel
) {
    val settings by viewModel.settings.collectAsState()
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmBeigeBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Minimalist Header Bar
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
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(TerracottaAccent)
                    )
                    Text(
                        text = "AEGIS",
                        style = MaterialTheme.typography.titleSmall,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = CharcoalPrimary
                    )
                }

                Surface(
                    color = MutedBeigeCard,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WarmStoneBorder)
                ) {
                    Text(
                        text = "Anthropic Architecture",
                        style = MaterialTheme.typography.labelSmall,
                        color = CharcoalMuted,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontFamily = FontFamily.Serif
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Editorial Headline (Times New Roman / Serif)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "The autonomous intelligence for your professional presence.",
                    style = MaterialTheme.typography.displayMedium,
                    fontFamily = FontFamily.Serif,
                    color = CharcoalPrimary,
                    lineHeight = 36.sp,
                    letterSpacing = (-0.5).sp
                )

                Text(
                    text = "Aegis orchestrates your LinkedIn feed publications, bespoke connection requests, and direct message sweeps with natural human cadence.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = CharcoalMuted,
                    lineHeight = 24.sp,
                    fontFamily = FontFamily.Serif
                )
            }

            // Status Indicator Pill Card
            Surface(
                color = SoftCreamSurface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, WarmStoneBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "SYSTEM ENGINE",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.sp,
                            color = CharcoalLight,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (settings?.isConfigured == true) "LinkedIn & Groq Configured" else "Ready to Connect",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = FontFamily.Serif,
                            color = CharcoalPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (settings?.isConfigured == true) PastelSage else PastelAmber)
                            .border(1.dp, if (settings?.isConfigured == true) SageBorder else AmberBorder, RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (settings?.isConfigured == true) "Active ✓" else "Setup Required",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (settings?.isConfigured == true) DarkSage else DarkAmber,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Minimalist Capabilities Cards (Anthropic Style)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "CORE CAPABILITIES",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.5.sp,
                    color = CharcoalLight,
                    fontWeight = FontWeight.SemiBold
                )

                CapabilityItemCard(
                    number = "01",
                    title = "Thought Leadership & Feed Publishing",
                    description = "Creates high-conversion, formatted commentary and dispatches directly to your live LinkedIn feed."
                )

                CapabilityItemCard(
                    number = "02",
                    title = "Contextual Connection Outreach",
                    description = "Extracts profile context and writes concise, personalized invitation notes under 300 characters."
                )

                CapabilityItemCard(
                    number = "03",
                    title = "Smart Inbox Triage & DM Replies",
                    description = "Sweeps unread conversations and formulates tailored 2-sentence responses with zero manual drag."
                )

                CapabilityItemCard(
                    number = "04",
                    title = "Profile Audit & SEO Refinement",
                    description = "Revitalizes headlines and summaries using high-impact keywords and executive positioning."
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Primary Call to Action Button
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { viewModel.setTab(AppTab.CONNECT) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TerracottaAccent,
                        contentColor = SoftCreamSurface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("btn_begin_setup")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Connect Accounts & Setup",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = FontFamily.Serif,
                            color = SoftCreamSurface,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = SoftCreamSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (settings?.isConfigured == true) {
                    OutlinedButton(
                        onClick = { viewModel.setTab(AppTab.CHAT) },
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarmStoneBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = SoftCreamSurface,
                            contentColor = CharcoalPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_skip_to_chat")
                    ) {
                        Text(
                            text = "Open Conversation Directly →",
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Serif,
                            color = CharcoalPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CapabilityItemCard(
    number: String,
    title: String,
    description: String
) {
    Surface(
        color = SoftCreamSurface,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WarmStoneBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.titleSmall,
                fontFamily = FontFamily.Serif,
                color = TerracottaAccent,
                fontWeight = FontWeight.Bold
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Serif,
                    color = CharcoalPrimary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = CharcoalMuted,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
