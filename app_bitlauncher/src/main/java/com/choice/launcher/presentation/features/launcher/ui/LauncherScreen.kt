package com.choice.launcher.presentation.features.launcher.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.choice.design.composable.LogoBitLauncher
import com.choice.design.theme.ApplicationTheme
import com.choice.launcher.R


@Composable
fun LauncherScreen() {
    val skin = rememberAsyncImagePainter(R.drawable.skin)
    val banner = painterResource(id = R.drawable.ic_pojav_full)

    val context = LocalContext.current

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopBar()
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2A2A2A))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {},
                    border = BorderStroke(1.dp, Color.White),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Computer, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("LOCALHOST")
                }

                Button(onClick = {}, colors = ButtonDefaults.buttonColors(Color(0xFF3E8CFF))) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("QUICK PLAY")
                }
            }
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Left - Player & Play
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF2A2A2A))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = skin,
                    contentDescription = null,
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.DarkGray)
                )
            }

            Spacer(Modifier.width(16.dp))

            // Right - Friends + News (Agora Scrollável)
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                Column(
                    Modifier
                        .verticalScroll(rememberScrollState())

                ) {
                    Text(
                        "FRIENDS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2A2A2A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No friends found", color = Color.Gray)
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        "NEWS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun TopBar(
    username: String = "4xvn",
    onSettingsClick: () -> Unit = {},
    onVersionClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onFriendsClick: () -> Unit = {},
    onModsClick: () -> Unit = {},
    onLocationClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ApplicationTheme.colors.background)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LogoBitLauncher(
                modifier = Modifier.size(ApplicationTheme.spacing.extraHuge)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = ApplicationTheme.typography.headlineSmall.copy(
                            color = ApplicationTheme.colors.primary,
                            fontWeight = FontWeight.Bold
                        ).toSpanStyle(),
                        block = {
                            append("Bit ")
                        }
                    )
                    withStyle(
                        style = ApplicationTheme.typography.headlineSmall.copy(
                            color = ApplicationTheme.colors.onSurface,
                        ).toSpanStyle(),
                        block = {
                            append("Launcher")
                        }
                    )
                }
            )
        }

        // BOTÕES
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = onFriendsClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2980FF)),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(username, color = Color.White)
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Default.Groups, contentDescription = "Friends", tint = Color.White)
            }

            Spacer(Modifier.width(8.dp))
            IconButtonCard(icon = Icons.Default.Person, onClick = onProfileClick)
            IconButtonCard(icon = Icons.Default.Place, onClick = onLocationClick)
            IconButtonCard(icon = Icons.Default.Extension, onClick = onModsClick)
            IconButtonCard(
                icon = Icons.Default.Dns,
                onClick = onVersionClick,
                content = {
                    Text("1.8.9", color = Color.White, fontSize = 14.sp)
                }
            )
            IconButtonCard(icon = Icons.Default.Settings, onClick = onSettingsClick)
        }
    }
}

@Composable
fun IconButtonCard(
    icon: ImageVector,
    onClick: () -> Unit,
    content: @Composable (RowScope.() -> Unit)? = null
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF222222)),
        shape = RoundedCornerShape(6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (content != null) {
                content()
            } else {
                Icon(icon, contentDescription = null, tint = Color.White)
            }
        }
    }
}
