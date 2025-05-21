package com.redemastery.launcher.presentation.features.login

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.redemastery.design.composable.LauncherButton
import com.redemastery.design.theme.ApplicationTheme
import com.redemastery.launcher.R
import com.redemastery.launcher.presentation.features.login.model.LoginUiEvent
import com.redemastery.launcher.presentation.features.login.model.LoginUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.redemastery.oldapi.pojav.PojavProfile

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
    val state by viewModel.state.collectAsStateWithLifecycle()
    val account = state.account
    val loadingState = state.loadingState

    val scope = rememberCoroutineScope()

    LaunchedEffect(account) {
        if(account != null){
            scope.launch(Dispatchers.IO){
                account.updateSkinFace()
            }
            PojavProfile.getCurrentProfileContents(context, null)
            state.account = null
            onLoginPirate(context)
        }
    }

    Column(
        modifier = modifier.padding(horizontal = ApplicationTheme.spacing.medium),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .wrapContentSize(),
            painter = painterResource(id = R.drawable.logo_text),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )


        Spacer(modifier = Modifier.height(ApplicationTheme.spacing.extraLarge))

        OutlinedTextField(
            value = nickname,
            onValueChange = {

                nickname = it.take(16)
                isNicknameError = when {
                    it.isNotEmpty() && it.length !in 3..16 -> true
                    !usernameRegex.matches(it) -> true
                    else -> false
                }

            },
            singleLine = true,
            label = { Text("Usuário") },
            isError = isNicknameError,
            supportingText = {
                if(isNicknameError){
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        text = "Nome inválido",
                        style = ApplicationTheme.typography.bodyMedium.copy(
                            color = ApplicationTheme.colors.error
                        )
                    )
                }
            },
            modifier = Modifier.widthIn(max = ApplicationTheme.spacing.ultraEpic + 80.dp),
            shape = ApplicationTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedLabelColor = ApplicationTheme.colors.tertiary,
                unfocusedBorderColor = ApplicationTheme.colors.tertiary
            )
        )

        Spacer(modifier = Modifier.height(ApplicationTheme.spacing.mediumSmall))

        LauncherButton(
            text = "Entrar",
            enabled = (nickname.isNotEmpty() && !isNicknameError) && loadingState != LoginUiState.LoadingState.MicrosoftAccount,
            modifier = Modifier.widthIn(min = ApplicationTheme.spacing.ultraEpic + 80.dp),
            backgroundColor = ApplicationTheme.colors.tertiary,
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

    }
}
