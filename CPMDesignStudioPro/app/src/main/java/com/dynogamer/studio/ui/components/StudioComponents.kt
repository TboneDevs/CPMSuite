package com.dynogamer.studio.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dynogamer.studio.ui.theme.*

// ---- Studio Card ----

@Composable
fun StudioCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val mod = if (onClick != null) modifier.clickable(onClick = onClick) else modifier
    Card(
        modifier = mod,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

// ---- Dashboard Quick Action Tile ----

@Composable
fun QuickActionTile(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Boolean = false
) {
    val bg = if (accent) AccentRed else SurfaceElevated
    val border = if (accent) AccentRedGlow else Divider

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (accent) TextOnAccent else AccentRed,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (accent) TextOnAccent else TextPrimary,
                fontSize = 11.sp
            )
        }
    }
}

// ---- Section Header ----

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(20.dp)
                .background(AccentRed, RoundedCornerShape(2.dp))
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

// ---- Status Chip ----

@Composable
fun StatusChip(label: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = label, color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

// ---- Navigation Row Item ----

@Composable
fun NavigationRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AccentRedGlow),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = AccentRed, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            if (subtitle != null) Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextDisabled)
    }
}

// ---- Skeleton Loader ----

@Composable
fun SkeletonBox(modifier: Modifier = Modifier, height: Dp = 16.dp) {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "alpha"
    )
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(4.dp))
            .background(SurfaceCard.copy(alpha = alpha))
    )
}

// ---- Divider ----

@Composable
fun StudioDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier = modifier, color = com.dynogamer.studio.ui.theme.Divider, thickness = 1.dp)
}

// ---- Red Accent Button ----

@Composable
fun AccentButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AccentRed,
            contentColor = TextOnAccent,
            disabledContainerColor = AccentRedDark.copy(alpha = 0.4f)
        )
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}

// ---- Outline Button ----

@Composable
fun OutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentRed),
        border = androidx.compose.foundation.BorderStroke(1.dp, AccentRed)
    ) {
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}
