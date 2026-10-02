package com.v2ray.ang.ui.main

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.v2ray.ang.R
import com.v2ray.ang.ui.compose.AppTopBar
import com.v2ray.ang.ui.compose.LocalDarkTheme
import com.v2ray.ang.ui.compose.ThemeManager

@Composable
fun MainTopBar(
    isLoading: Boolean,
    showSearch: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchClose: () -> Unit,
    onSearchToggle: (Boolean) -> Unit,
    onMenuClick: () -> Unit,
    onAction: (MainAction) -> Unit,
    onMoreMenuAction: (MainMoreMenuAction) -> Unit
) {
    val isDark = LocalDarkTheme.current
    AppTopBar(
        title = "Aura X VPN",
        onBackClick = {},
        isLoading = isLoading,
        isSearchActive = false,
        searchQuery = searchQuery,
        onSearchQueryChange = onSearchQueryChange,
        onSearchClose = onSearchClose,
        searchPlaceholder = "",
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    painterResource(R.drawable.ic_menu_24dp),
                    contentDescription = stringResource(R.string.acc_open_menu)
                )
            }
        },
        actions = {
            IconButton(onClick = { ThemeManager.setThemeMode(if (isDark) "1" else "2") }) {
                Text(text = if (isDark) "☀️" else "🌙", fontSize = 20.sp)
            }
        }
    )
}
