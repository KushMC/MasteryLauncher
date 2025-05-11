package com.choice.launcher.presentation.features.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.choice.design.composable.LauncherButton
import com.choice.design.theme.ApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier,
    onContinue: () -> Unit
) {
    val context = LocalContext.current

    var nickname by remember { mutableStateOf("") }
    var isNicknameError by remember { mutableStateOf(false) }

/*    LaunchedEffect(isLoggingIn) {
        if (isLoggingIn) {
            delay(2500) // Tempo para o usuário logar via Pojav
            viewModel.loadSelectedAccountFromPojav(context)
            viewModel.resetLoginState()
        }
    }*/

    Column(
        modifier = modifier.padding(horizontal = ApplicationTheme.spacing.medium),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Escolha como deseja entrar:",
            style = ApplicationTheme.typography.titleLarge.copy(
                color = ApplicationTheme.colors.onSurface
            )
        )

        Spacer(modifier = Modifier.height(ApplicationTheme.spacing.extraLarge))

        OutlinedTextField(
            value = nickname,
            onValueChange = {
                nickname = it.take(11)
                isNicknameError = false
            },
            label = { Text("Nome para conta pirata") },
            isError = isNicknameError,
            modifier = Modifier.widthIn(max = ApplicationTheme.spacing.ultraEpic + 80.dp),
            shape = ApplicationTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedLabelColor = ApplicationTheme.colors.primary,
                unfocusedBorderColor = ApplicationTheme.colors.primary
            )
        )

        Spacer(modifier = Modifier.height(ApplicationTheme.spacing.mediumSmall))

        LauncherButton(
            text = "Local Account",
            modifier = Modifier.widthIn(min = ApplicationTheme.spacing.ultraEpic + 80.dp),
            backgroundColor = ApplicationTheme.colors.primary,
            onClick = {
                if (nickname.length !in 4..11) {
                    isNicknameError = true
                } else {
                    onContinue()
                }
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Login,
                contentDescription = null,
            )
        }

        Spacer(modifier = Modifier.height(ApplicationTheme.spacing.extraLarge))

        LauncherButton(
            modifier = Modifier.wrapContentSize(),
            text = "Microsoft Account",
            backgroundColor = ApplicationTheme.colors.tertiary,
            onClick = { },
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Login,
                contentDescription = null,
            )
        }

    }
}
