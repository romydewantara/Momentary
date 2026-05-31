package com.example.momentary.ui.screen.home.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.momentary.R
import com.example.momentary.ui.screen.home.HomeViewModel

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
    viewModel: HomeViewModel
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val selectedItem = state.selectedDrawerItem

    LaunchedEffect(drawerState.isClosed) {
        if (drawerState.isClosed) {
            viewModel.onDrawerItemSelected(DrawerItem.MAIN)
        }
    }

    if (state.showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissLogoutDialog() },
            title = { Text(text = stringResource(R.string.drawer_logout_confirm_header)) },
            text = { Text(text = stringResource(R.string.drawer_logout_confirm_body)) },
            confirmButton = {
                TextButton(
                    modifier = Modifier.testTag("log_out"),
                    onClick = {
                        viewModel.confirmLogout()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.drawer_logout_confirm_positive),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissLogoutDialog() }
                ) { Text(text = stringResource(R.string.drawer_logout_confirm_negative)) }
            }
        )
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
                    viewModel.onDrawerItemSelected(DrawerItem.LANGUAGE)
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
                modifier = Modifier.testTag("logout_menu_item"),
                label = {
                    Text(
                        text = stringResource(R.string.drawer_logout),
                        color = MaterialTheme.colorScheme.error
                    )
                },
                selected = selectedItem == DrawerItem.LOGOUT,
                onClick = {
                    viewModel.onDrawerItemSelected(DrawerItem.LOGOUT)
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
