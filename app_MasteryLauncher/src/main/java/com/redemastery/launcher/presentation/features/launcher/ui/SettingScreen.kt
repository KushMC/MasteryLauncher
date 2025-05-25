package com.redemastery.launcher.presentation.features.launcher.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.redemastery.design.theme.ApplicationTheme
import com.redemastery.launcher.domain.model.MinecraftConfig

@Composable
fun ConfigScreen(
    config: MinecraftConfig?,
    default: MinecraftConfig = MinecraftConfig(),
    onSave: (MinecraftConfig) -> Unit,
    onRetry: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = ApplicationTheme.colors.background.copy(alpha = 0.8f)
        ),
        shape = ApplicationTheme.shapes.medium
    ) {

        if (config == null) {

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Não foi possivel carregar as configurações",
                    modifier = Modifier.fillMaxSize(),
                    style = ApplicationTheme.typography.labelLarge,
                    textAlign = TextAlign.Center
                )

                TextButton(
                    onClick = onRetry,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        modifier = Modifier.size(ApplicationTheme.spacing.mediumLarge),
                        contentDescription = null,
                        tint = ApplicationTheme.colors.error
                    )
                    Spacer(Modifier.width(ApplicationTheme.spacing.medium))
                    Text(
                        text = "Tentar novamente",
                        color = ApplicationTheme.colors.error
                    )
                }
            }

        } else {

            var guiScale by remember {mutableIntStateOf(config.guiScale) }
            var fancyGraphics by remember { mutableStateOf(config.fancyGraphics) }
            var clouds by remember { mutableStateOf(config.clouds) }
            var renderDistance by remember { mutableIntStateOf(config.renderDistance) }

            val guiOptions = listOf("AUTO" to 0, "PEQUENA" to 1, "MEDIO" to 2, "GRANDE" to 3)

            val isModified = guiScale != config.guiScale ||
                    fancyGraphics != config.fancyGraphics ||
                    clouds != config.clouds ||
                    renderDistance != config.renderDistance


            Column(
                modifier = Modifier
                    .padding(
                        vertical = ApplicationTheme.spacing.medium,
                        horizontal = ApplicationTheme.spacing.medium
                    )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = ApplicationTheme.spacing.small),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Configurações",
                        style = ApplicationTheme.typography.titleLarge,
                        color = Color.White
                    )

                    Row(
                        modifier = Modifier.padding(ApplicationTheme.spacing.small)
                    ) {
                        TextButton(
                            onClick = {
                                val newConfig = MinecraftConfig(
                                    guiScale = guiScale,
                                    fancyGraphics = fancyGraphics,
                                    clouds = clouds,
                                    renderDistance = renderDistance,
                                    fov = config.fov // FOV estático aqui, ou adicione o controle se desejar.
                                )
                                onSave(newConfig)
                            },
                            enabled = isModified // habilita só se tiver alteração
                        ) {
                            Text(
                                text = "SALVAR",
                                style = ApplicationTheme.typography.labelLarge.copy(
                                    color = if (isModified)
                                        ApplicationTheme.colors.tertiary
                                    else
                                        ApplicationTheme.colors.tertiary.copy(alpha = 0.3f)
                                )
                            )
                        }

                        TextButton(
                            onClick = {
                                guiScale = default.guiScale
                                fancyGraphics = default.fancyGraphics
                                clouds = default.clouds
                                renderDistance = default.renderDistance
                            }
                        ) {
                            Text(
                                text = "REDEFINIR",
                                style = ApplicationTheme.typography.labelLarge.copy(
                                    color = ApplicationTheme.colors.onSurface
                                )
                            )
                        }
                    }
                }

                LazyColumn {
                    item {
                        ConfigOptionSelector(
                            title = "Escala",
                            selected = guiScale,
                            options = guiOptions,
                            onSelect = { guiScale = it }
                        )
                    }

                    item {
                        Spacer(Modifier.height(16.dp))
                        ConfigOptionSelector(
                            title = "Gráficos",
                            selected = fancyGraphics,
                            options = listOf("BÁSICO" to false, "ALTA" to true),
                            onSelect = { fancyGraphics = it }
                        )
                    }

                    item {
                        Spacer(Modifier.height(16.dp))
                        ConfigToggle(
                            title = "Nuvens",
                            selected = clouds,
                            onSelect = { clouds = it }
                        )
                    }

                    item {
                        Spacer(Modifier.height(16.dp))
                        ConfigSlider(
                            title = "Distância de Renderização",
                            value = renderDistance.toFloat(),
                            valueRange = 2f..32f,
                            steps = 30,
                            onValueChange = { renderDistance = it.toInt() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun <T> ConfigOptionSelector(
    title: String,
    selected: T,
    options: List<Pair<String, T>>,
    onSelect: (T) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = Color.White)

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEach { (label, value) ->
                OutlinedButton(
                    onClick = { onSelect(value) },
                    colors = if (selected == value)
                        ButtonDefaults.outlinedButtonColors(
                            contentColor = ApplicationTheme.colors.tertiary,
                        )
                    else
                        ButtonDefaults.outlinedButtonColors(
                            contentColor = ApplicationTheme.colors.tertiary.copy(alpha = 0.6f)
                        )
                ) {
                    if (selected == value) {
                        Icon(
                            Icons.Default.Check,
                            modifier = Modifier.size(ApplicationTheme.spacing.medium),
                            contentDescription = null
                        )
                        Spacer(Modifier.width(ApplicationTheme.spacing.extraSmall))
                    }
                    Text(
                        text = label,
                        fontWeight = if (selected == value) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun ConfigSlider(
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text("$title: $value", color = Color.White)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = ApplicationTheme.colors.tertiary,
                activeTrackColor = ApplicationTheme.colors.tertiary,
                inactiveTrackColor = ApplicationTheme.colors.tertiary.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
fun ConfigToggle(
    title: String,
    selected: Boolean,
    onSelect: (Boolean) -> Unit
) {
    val options = listOf("SIM" to true, "NÃO" to false)
    ConfigOptionSelector(title, selected, options, onSelect)
}


