package com.choice.design.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.choice.design.theme.ApplicationTheme

@Composable
fun LauncherButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textColor: Color = ApplicationTheme.colors.onPrimary,
    backgroundColor: Color = ApplicationTheme.colors.primary,
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    icon: @Composable (() -> Unit)? = null,
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        shape = ApplicationTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = ApplicationTheme.colors.onPrimary,
        ),
        elevation = elevation,
        border = border,
        contentPadding = PaddingValues(
            start = ApplicationTheme.spacing.large,
            top = ApplicationTheme.spacing.medium,
            bottom = ApplicationTheme.spacing.medium,
            end = ApplicationTheme.spacing.large
        ),
        interactionSource = interactionSource,
        onClick = onClick
    ){
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ){
            if(icon != null){
                Box(
                    modifier = Modifier.padding(end = ApplicationTheme.spacing.small)
                ){
                    icon()
                }
            }

            Text(
                text = text,
                style = ApplicationTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = textColor
            )
        }
    }
}


@Preview
@Composable
fun PrimaryButtonPreview() {
    LauncherButton(text = "Button", onClick = {})
}