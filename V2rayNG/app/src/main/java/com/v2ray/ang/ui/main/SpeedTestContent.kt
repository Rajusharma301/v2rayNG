package com.v2ray.ang.ui.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2ray.ang.ui.compose.LocalDarkTheme
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val GAUGE_MAX = 200f
private val needleColor = Color(0xFFF5A524)

private suspend fun measureDownload(onSpeed: (Float) -> Unit): Float = withContext(Dispatchers.IO) {
    val conn = URL("https://speed.cloudflare.com/__down?bytes=40000000").openConnection() as HttpURLConnection
    conn.connectTimeout = 8000
    conn.readTimeout = 8000
    conn.setRequestProperty("User-Agent", "Mozilla/5.0")
    var avg = 0f
    try {
        conn.inputStream.use { input ->
            val buf = ByteArray(65536)
            var total = 0L
            val start = System.nanoTime()
            var last = start
            while (true) {
                val n = input.read(buf)
                if (n < 0) break
                total += n
                val now = System.nanoTime()
                if (now - last > 200_000_000L) {
                    avg = total * 8f / ((now - start) / 1e9f) / 1_000_000f
                    onSpeed(avg)
                    last = now
                }
                if (now - start > 8_000_000_000L) break
            }
            val elapsed = (System.nanoTime() - start) / 1e9f
            avg = total * 8f / elapsed / 1_000_000f
        }
    } finally {
        conn.disconnect()
    }
    avg
}

private suspend fun measureUpload(onSpeed: (Float) -> Unit): Float = withContext(Dispatchers.IO) {
    val conn = URL("https://speed.cloudflare.com/__up").openConnection() as HttpURLConnection
    conn.requestMethod = "POST"
    conn.doOutput = true
    conn.connectTimeout = 8000
    conn.readTimeout = 10000
    conn.setRequestProperty("User-Agent", "Mozilla/5.0")
    conn.setChunkedStreamingMode(65536)
    var avg = 0f
    try {
        val buf = ByteArray(65536)
        val start = System.nanoTime()
        var sent = 0L
        var last = start
        conn.outputStream.use { out ->
            while (true) {
                out.write(buf)
                sent += buf.size
                val now = System.nanoTime()
                if (now - last > 200_000_000L) {
                    avg = sent * 8f / ((now - start) / 1e9f) / 1_000_000f
                    onSpeed(avg)
                    last = now
                }
                if (now - start > 7_000_000_000L) break
            }
        }
        val elapsed = (System.nanoTime() - start) / 1e9f
        avg = sent * 8f / elapsed / 1_000_000f
        try {
            conn.responseCode
        } catch (_: Exception) {
        }
    } finally {
        conn.disconnect()
    }
    avg
}

@Composable
fun SpeedTestContent(
    modifier: Modifier = Modifier,
    statusText: String,
    onAction: (MainAction) -> Unit
) {
    val isDark = LocalDarkTheme.current
    val green = if (isDark) Color(0xFF4FC79B) else Color(0xFF1B7F5C)
    val scope = rememberCoroutineScope()
    var running by remember { mutableStateOf(false) }
    var phase by remember { mutableStateOf("Ready to test") }
    var live by remember { mutableStateOf(0f) }
    var download by remember { mutableStateOf<Float?>(null) }
    var upload by remember { mutableStateOf<Float?>(null) }

    val animated by animateFloatAsState(
        targetValue = (live / GAUGE_MAX).coerceIn(0f, 1f),
        animationSpec = tween(250),
        label = "gauge"
    )
    val cardColor = if (isDark) MaterialTheme.colorScheme.surfaceContainerHigh
    else MaterialTheme.colorScheme.surface
    val trackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f)
    val tickColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f)

    fun start() {
        if (running) return
        running = true
        download = null
        upload = null
        live = 0f
        scope.launch {
            try {
                phase = "Testing download..."
                val d = measureDownload { live = it }
                download = d
                live = 0f
                phase = "Testing upload..."
                val u = measureUpload { live = it }
                upload = u
                live = d
                phase = "Test completed"
            } catch (e: Exception) {
                phase = "Test failed. Check your connection."
                live = 0f
            } finally {
                running = false
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Speed Test", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            SpeedCard("Download", download, Modifier.weight(1f), cardColor, green)
            Spacer(modifier = Modifier.width(12.dp))
            SpeedCard("Upload", upload, Modifier.weight(1f), cardColor, green)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.size(290.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stroke = 16.dp.toPx()
                val inset = stroke / 2f + 8.dp.toPx()
                val arcSize = Size(size.width - inset * 2f, size.height - inset * 2f)
                val topLeft = Offset(inset, inset)
                val radius = arcSize.width / 2f
                val c = center

                drawArc(
                    color = trackColor,
                    startAngle = 150f,
                    sweepAngle = 240f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
                drawArc(
                    color = green,
                    startAngle = 150f,
                    sweepAngle = 240f * animated,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )

                for (i in 0..10) {
                    val a = Math.toRadians(150.0 + 240.0 * i / 10.0)
                    val r1 = radius - stroke - 6.dp.toPx()
                    val r2 = r1 - 10.dp.toPx()
                    drawLine(
                        color = tickColor,
                        start = Offset(c.x + r1 * cos(a).toFloat(), c.y + r1 * sin(a).toFloat()),
                        end = Offset(c.x + r2 * cos(a).toFloat(), c.y + r2 * sin(a).toFloat()),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                val na = Math.toRadians(150.0 + 240.0 * animated)
                val nl = radius - stroke - 30.dp.toPx()
                drawLine(
                    color = needleColor,
                    start = c,
                    end = Offset(c.x + nl * cos(na).toFloat(), c.y + nl * sin(na).toFloat()),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawCircle(color = needleColor, radius = 9.dp.toPx(), center = c)
                drawCircle(color = Color.White, radius = 3.dp.toPx(), center = c)
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = String.format(Locale.US, "%.1f", live),
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Mbps",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = phase,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { start() },
            enabled = !running,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1B7F5C),
                contentColor = Color.White
            )
        ) {
            Text(
                text = when {
                    running -> "Testing..."
                    download == null -> "Start Speed Test"
                    else -> "Restart Speed Test"
                },
                fontWeight = FontWeight.SemiBold
            )
        }

        TextButton(onClick = { onAction(MainAction.TestAllServers) }) {
            Text("Ping all servers")
        }
        Text(
            text = statusText,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun SpeedCard(
    label: String,
    value: Float?,
    modifier: Modifier,
    cardColor: Color,
    green: Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(cardColor)
            .border(1.dp, green.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = if (value == null) "--" else String.format(Locale.US, "%.1f", value),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Mbps",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}
