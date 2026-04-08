package com.example.momentary.ui.screen.auth.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.momentary.R
import com.example.momentary.ui.common.CustomSnackBar
import com.example.momentary.ui.common.CustomView
import com.example.momentary.ui.common.TextFieldType

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var isErrorSnackBar by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onRegisterSuccess()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            isErrorSnackBar = event.isError

            snackBarHostState.showSnackbar(
                message = event.message.asString(context),
                withDismissAction = false
            )
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                snackbar = { data ->
                    CustomSnackBar(
                        message = data.visuals.message,
                        isError = isErrorSnackBar
                    )
                }
            )
        }
    ) { padding ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .imePadding(),
            verticalArrangement = Arrangement.Center
        ) {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onBack() },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = stringResource(R.string.cd_button_back),
                        modifier = Modifier
                            .size(44.dp)
                            .offset(x = (-8).dp)
                    )
                }
                Text(
                    text = stringResource(R.string.register_heading),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.register_subheading),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(24.dp))

            CustomView(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = stringResource(R.string.text_field_name),
                type = TextFieldType.TEXT,
                isError = state.nameError != null,
                errorMessage = state.nameError?.asString(context),
                onDone = { focusManager.clearFocus() },
                leadingIcon = painterResource(R.drawable.ic_person)
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomView(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                label = stringResource(R.string.text_field_email),
                type = TextFieldType.EMAIL,
                isError = state.emailError != null,
                errorMessage = state.emailError?.asString(context),
                onDone = { focusManager.clearFocus() },
                leadingIcon = painterResource(R.drawable.ic_email)
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomView(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                label = stringResource(R.string.text_field_password),
                type = TextFieldType.PASSWORD,
                isError = state.passwordError != null,
                errorMessage = state.passwordError?.asString(context),
                onDone = { focusManager.clearFocus() },
                leadingIcon = painterResource(R.drawable.ic_lock)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.register()
                },
                enabled = state.isValid && !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(26.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.register_button_register))
                }
            }
        }
    }
}
