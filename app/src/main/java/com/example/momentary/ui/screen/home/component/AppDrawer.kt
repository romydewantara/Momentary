package com.example.momentary.ui.screen.home.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.momentary.R

enum class DrawerItem {
    MAIN, LANGUAGE, LOGOUT
}

@Composable
fun AppDrawer(
    modifier: Modifier = Modifier,
    name: String,
    email: String,
    drawerState: DrawerState,
    onClose: () -> Unit,
    onChangeLanguage: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedItem by remember { mutableStateOf(DrawerItem.MAIN) }

    LaunchedEffect(drawerState.isClosed) {
        if (drawerState.isClosed) {
            selectedItem = DrawerItem.MAIN
        }
    }

    ModalDrawerSheet(
        drawerTonalElevation = 8.dp
    ) {
        Column(
            modifier = modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {

            AppDrawerHeader(name = name, email = email)

            HorizontalDivider()

            Spacer(modifier = Modifier.height(12.dp))

            NavigationDrawerItem(
                label = { Text(text = stringResource(R.string.drawer_main_menu)) },
                selected = selectedItem == DrawerItem.MAIN,
                onClick = {
                    selectedItem = DrawerItem.MAIN
                    onClose()
                },
                icon = {
                    Icon(Icons.AutoMirrored.Default.List, contentDescription = null)
                }
            )

            NavigationDrawerItem(
                label = { Text(stringResource(R.string.drawer_change_language)) },
                selected = selectedItem == DrawerItem.LANGUAGE,
                onClick = {
                    selectedItem = DrawerItem.LANGUAGE
                    onChangeLanguage()
                },
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_language),
                        contentDescription = stringResource(R.string.cd_button_language)
                    )
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            NavigationDrawerItem(
                label = {
                    Text(
                        text = stringResource(R.string.drawer_logout),
                        color = MaterialTheme.colorScheme.error
                    )
                },
                selected = selectedItem == DrawerItem.LOGOUT,
                onClick = {
                    selectedItem = DrawerItem.LOGOUT
                    onLogout()
                },
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_logout),
                        contentDescription = stringResource(R.string.cd_button_logout),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.drawer_app_copyright),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.drawer_app_build),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
