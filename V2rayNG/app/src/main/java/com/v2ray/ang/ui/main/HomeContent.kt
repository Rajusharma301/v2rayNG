package com.v2ray.ang.ui.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2ray.ang.R
import com.v2ray.ang.ui.compose.LocalDarkTheme
import java.util.Locale
import kotlinx.coroutines.delay

private val mapRows: List<List<IntRange>> = listOf(
    listOf(10..16, 20..26, 33..34),
    listOf(4..6, 9..18, 20..26, 31..34, 38..54),
    listOf(2..7, 8..19, 21..26, 31..35, 36..56, 57..58),
    listOf(2..18, 23..24, 30..36, 37..57),
    listOf(6..19, 28..29, 30..37, 38..55),
    listOf(7..19, 29..38, 39..54, 56..56),
    listOf(7..19, 28..38, 39..52, 55..56),
    listOf(8..18, 27..36, 37..41, 42..52, 55..55),
    listOf(8..17, 27..37, 38..41, 42..53),
    listOf(9..15, 17..17, 27..37, 38..41, 43..46, 48..52),
    listOf(12..16, 17..17, 27..37, 40..41, 44..46, 48..52, 54..54),
    listOf(14..16, 18..21, 27..37, 45..45, 48..51),
    listOf(17..22, 29..38, 47..49, 51..53),
    listOf(17..23, 30..39, 47..48, 51..53, 55..58),
    listOf(17..24, 30..38, 49..51, 56..58),
    listOf(18..25, 31..38, 51..55),
    listOf(19..25, 31..39, 49..56),
    listOf(19..24, 31..36, 48..56),
    listOf(19..23, 31..36, 48..57),
    listOf(19..22, 32..34, 51..59),
    listOf(19..21, 54..54, 58..59),
    listOf(19..20),
    listOf(19..20)
)

@Composable
private fun WorldMapDots(modifier: Modifier, color: Color) {
    Canvas(modifier = modifier.fillMaxWidth().aspectRatio(60f / 23f)) {
        val cell = size.width / 60f
        mapRows.forEachIndexed { row, ranges ->
            ranges.forEach { range ->
                for (col in range) {
                    drawCircle(
                        color = color,
                        radius = cell * 0.3f,
                        center = Offset(col * cell + cell / 2f, row * cell + cell / 2f)
                    )
                }
            }
        }
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    isRunning: Boolean,
    displayText: String,
    onToggle: () -> Unit,
    onSelectServer: () -> Unit
) {
    val isDark = LocalDarkTheme.current
    val green = if (isDark) Color(0xFF4FC79B) else Color(0xFF1B7F5C)
    var seconds by remember { mutableStateOf(0L) }

    LaunchedEffect(isRunning) {
        seconds = 0L
        while (isRunning) {
            delay(1000)
            seconds += 1
        }
    }

    val timeText = String.format(
        Locale.US, "%02d:%02d:%02d",
        seconds / 3600, (seconds % 3600) / 60, seconds % 60
    )

    val outerBrush = if (isDark) {
        Brush.radialGradient(listOf(Color(0xFF2E3A35), Color(0xFF1E2522)))
    } else {
        Brush.radialGradient(listOf(Color.White, Color(0xFFD6F0E3)))
    }
    val innerColor = when {
        isRunning -> Color(0xFF1B7F5C)
        isDark -> Color(0xFF3F6B5A)
        else -> Color(0xFF7FB09B)
    }
    val cardColor = if (isDark) MaterialTheme.colorScheme.surfaceContainerHigh
    else MaterialTheme.colorScheme.surface

    Box(modifier = modifier.fillMaxSize()) {
        WorldMapDots(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp),
            color = green.copy(alpha = if (isDark) 0.20f else 0.22f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = if (isRunning) "VPN Connected" else "VPN Disconnected",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Text(
                text = timeText,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(230.dp)
                    .shadow(12.dp, CircleShape)
                    .clip(CircleShape)
                    .background(outerBrush)
                    .border(2.dp, green.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val inset = 14.dp.toPx()
                    drawArc(
                        color = green.copy(alpha = if (isRunning) 1f else 0.45f),
                        startAngle = -140f,
                        sweepAngle = 100f,
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = Size(size.width - inset * 2f, size.height - inset * 2f),
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(innerColor)
                        .clickable(onClick = onToggle),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            if (isRunning) R.drawable.ic_stop_24dp else R.drawable.ic_play_24dp
                        ),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = if (isRunning) "Tap to disconnect" else "Tap to connect",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (isRunning) green else Color(0xFFB0B0B0))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isRunning) "You're protected" else "You're not protected",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = displayText,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(cardColor)
                    .clickable(onClick = onSelectServer)
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Select Server",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Tap to choose a location",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "›", color = MaterialTheme.colorScheme.surface, fontSize = 20.sp)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
