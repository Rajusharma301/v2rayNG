package com.v2ray.ang.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2ray.ang.R
import com.v2ray.ang.ui.compose.AppDivider
import com.v2ray.ang.ui.compose.ThemeManager

@Composable
fun AuraDrawer(drawerState: DrawerState, onSelect: (Int) -> Unit) {
    val mode by ThemeManager.themeMode.collectAsState()
    val items = listOf(
        "Home" to R.drawable.ic_play_24dp,
        "Servers" to R.drawable.ic_subscriptions_24dp,
        "Speed Test" to R.drawable.ic_routing_24dp,
        "Settings" to R.drawable.ic_settings_24dp
    )
    ModalDrawerSheet(
        drawerState = drawerState,
        modifier = Modifier.fillMaxWidth(0.75f),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AuraLogo(Modifier.size(48.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Aura X VPN", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            items.forEachIndexed { index, item ->
                NavigationDrawerItem(
                    label = { Text(item.first) },
                    selected = false,
                    onClick = { onSelect(index) },
                    icon = { Icon(painterResource(item.second), contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
            AppDivider()
            Text(
                text = "Theme",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp)
            )
            Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                DrawerChip("Light", mode == "1", Modifier.weight(1f)) { ThemeManager.setThemeMode("1") }
                Spacer(modifier = Modifier.width(8.dp))
                DrawerChip("Dark", mode == "2", Modifier.weight(1f)) { ThemeManager.setThemeMode("2") }
                Spacer(modifier = Modifier.width(8.dp))
                DrawerChip("Auto", mode != "1" && mode != "2", Modifier.weight(1f)) { ThemeManager.setThemeMode("0") }
            }
        }
    }
}

@Composable
private fun DrawerChip(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = if (selected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
