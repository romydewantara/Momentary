package com.example.momentary.ui.screen.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.example.momentary.R
import com.example.momentary.ui.common.CustomSnackBar
import com.example.momentary.ui.common.CustomView
import com.example.momentary.ui.common.TextFieldType
import com.example.momentary.ui.navigation.NavKeys
import com.example.momentary.ui.screen.auth.login.component.LoginHeader
import com.example.momentary.ui.theme.Tertiary

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    backStackEntry: NavBackStackEntry,
    onLoginSuccess: (String) -> Unit,
    navigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var isErrorSnackBar by remember { mutableStateOf(false) }

    val savedStateHandle = backStackEntry.savedStateHandle
    val registerSuccess = savedStateHandle.get<Boolean>(NavKeys.REGISTER_SUCCESS)
    val successMessage = stringResource(R.string.register_success)
    val loginSuccessMessage = stringResource(R.string.login_success)

    LaunchedEffect(registerSuccess) {
        if (registerSuccess == true) {
            snackBarHostState.showSnackbar(
                message = successMessage
            )
            isErrorSnackBar = false

            savedStateHandle.remove<Boolean>(NavKeys.REGISTER_SUCCESS)
        }
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onLoginSuccess(loginSuccessMessage)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            isErrorSnackBar = event.isError

            snackBarHostState.showSnackbar(
                message = event.message.asString(context)
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

            LoginHeader()

            Spacer(modifier = Modifier.height(32.dp))

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
                    viewModel.login()
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
                    Text(stringResource(R.string.login_button_login))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val annotatedText = buildAnnotatedString {
                append(stringResource(R.string.login_register_text))
                append(" ")
                pushLink(
                    LinkAnnotation.Clickable(
                        tag = stringResource(R.string.tag_register),
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = Tertiary
                            )
                        ),
                        linkInteractionListener = { navigateToRegister() }
                    )
                )
                append(stringResource(R.string.login_register_text_link))
                pop()
            }

            Text(
                text = annotatedText,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
