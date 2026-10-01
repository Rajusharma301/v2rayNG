package com.v2ray.ang.ui.main
import androidx.compose.material3.Text
import androidx.compose.ui.unit.sp
import com.v2ray.ang.ui.compose.LocalDarkTheme
import com.v2ray.ang.ui.compose.ThemeManager

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.v2ray.ang.R
import com.v2ray.ang.ui.compose.AppTopBar

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
            val dark = LocalDarkTheme.current
            IconButton(onClick = { ThemeManager.setThemeMode(if (dark) "1" else "2") }) {
                Text(if (dark) "\u2600\uFE0F" else "\uD83C\uDF19", fontSize = 22.sp)
            }
        }
    )
}
