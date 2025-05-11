package com.choice.design.composable

import android.view.RoundedCorner
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.choice.design.R
import com.choice.design.theme.ApplicationTheme
import com.choice.design.theme.BitLauncherTheme

@Preview
@Composable
fun LogoBitLauncherPreview() {
    BitLauncherTheme {
        LogoBitLauncher()
    }
}

@Composable
fun LogoBitLauncher(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier,
        contentScale = ContentScale.Crop,
        painter = painterResource(R.drawable.icon_no_text),
        contentDescription = "Logo BitLauncher"
    )
}