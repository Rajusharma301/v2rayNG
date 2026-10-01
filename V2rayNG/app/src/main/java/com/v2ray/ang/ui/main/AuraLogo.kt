package com.v2ray.ang.ui.main

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun AuraLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val c = center
        val r = size.minDimension / 2f
        drawCircle(color = Color(0xFF14141A), radius = r)
        drawCircle(color = Color(0xFF3F6E5C), radius = r * 0.94f, style = Stroke(width = r * 0.07f))
        drawCircle(color = Color(0xFFF97910), radius = r * 0.76f, style = Stroke(width = r * 0.03f))
        val a = r * 0.42f
        val w = r * 0.2f
        drawLine(Color(0xFFF97910), Offset(c.x - a, c.y - a), Offset(c.x + a, c.y + a), strokeWidth = w, cap = StrokeCap.Round)
        drawLine(Color(0xFFFFBE3C), Offset(c.x + a, c.y - a), Offset(c.x - a, c.y + a), strokeWidth = w, cap = StrokeCap.Round)
        drawCircle(color = Color(0xFF14141A), radius = r * 0.07f)
    }
}
