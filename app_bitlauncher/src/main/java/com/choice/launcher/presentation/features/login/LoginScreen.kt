package com.choice.launcher.presentation.features.login

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.choice.design.composable.LauncherButton
import com.choice.design.theme.ApplicationTheme
import com.choice.launcher.presentation.features.login.model.LoginUiEvent
import com.choice.launcher.presentation.features.login.model.LoginUiState
import net.kdt.pojavlaunch.prefs.LauncherPreferences
import net.kdt.pojavlaunch.tasks.AsyncAssetManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier,
    onLoginPirate: (Context) -> Unit,
    onMicrosoftLogin: (Context) -> Unit,
) {
    val context = LocalContext.current

    var nickname by remember { mutableStateOf("") }
    var isNicknameError by remember { mutableStateOf(false) }
    val usernameRegex = Regex("^[a-zA-Z0-9_]*$")

    val viewModel = hiltViewModel<LoginViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val account = state.account
    val loadingState = state.loadingState

    if(account != null){
        state.account = null
        onLoginPirate(context)
    }

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
                if(usernameRegex.matches(it)){
                    nickname = it.take(11)
                    isNicknameError = false
                }else{
                    isNicknameError = true
                }
            },
            singleLine = true,
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
            enabled = (nickname.isNotEmpty() && !isNicknameError) && loadingState != LoginUiState.LoadingState.MicrosoftAccount,
            modifier = Modifier.widthIn(min = ApplicationTheme.spacing.ultraEpic + 80.dp),
            backgroundColor = ApplicationTheme.colors.primary,
            onClick = {
                viewModel.onEvent(LoginUiEvent.LoginPirate(nickname))
            }
        ) {
            if(loadingState == LoginUiState.LoadingState.PirateAccount){
                CircularProgressIndicator(
                    modifier = Modifier.size(ApplicationTheme.spacing.medium),
                    color = ApplicationTheme.colors.onPrimary,
                    strokeWidth = 2.dp,
                    strokeCap = StrokeCap.Round
                )
            }else{
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Login,
                    contentDescription = null,
                )
            }
        }

        Spacer(modifier = Modifier.height(ApplicationTheme.spacing.extraLarge))

        LauncherButton(
            enabled = loadingState != LoginUiState.LoadingState.PirateAccount,
            modifier = Modifier.wrapContentSize(),
            text = "Microsoft Account",
            backgroundColor = ApplicationTheme.colors.tertiary,
            onClick = { },
        ) {
            if(loadingState == LoginUiState.LoadingState.MicrosoftAccount){
                CircularProgressIndicator(
                    modifier = Modifier.size(ApplicationTheme.spacing.medium),
                    color = ApplicationTheme.colors.onPrimary,
                    strokeWidth = 2.dp,
                    strokeCap = StrokeCap.Round
                )
            }else{
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Login,
                    contentDescription = null,
                    tint = ApplicationTheme.colors.onPrimary
                )}
        }

    }
}
