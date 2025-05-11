import BitPluginsColors.Tertiary
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

object BitPluginsColors {
    val BackgroundDark = Color(0xFF091017)       // Fundo escuro
    val SurfaceDark = Color(0xFF1A1D21)          // Superfície (cartões, menus)
    val Primary = Color(0xFFC8925B)              // Texto "BitPlugins"
    val OnPrimary = Color(0xFF1C1109)            // Texto sobre Primary
    val Secondary = Color(0xFFFDD835)            // Números binários
    val OnSecondary = Color(0xFF1A1A1A)          // Texto sobre Secondary
    val Tertiary = Color(0xFF4CAF50)             // Grama
    val OnTertiary = Color(0xFF0F1F0F)           // Texto sobre Tertiary
    val Error = Color(0xFFFF6F61)                // Cor de erro (tom alaranjado suave)
    val OnError = Color(0xFF370000)
    val Outline = Color(0xFF00FFAA)              // Circuito ao redor do bloco
    val SurfaceVariant = Color(0xFF8D6E63)       // Terra do bloco
    val OnSurfaceVariant = Color(0xFF201914)
    val StarYellow = Color(0xFFFFF176)           // Detalhes de brilho
}
val BitPluginsDarkColorScheme: ColorScheme = darkColorScheme(
    primary = BitPluginsColors.Primary,
    onPrimary = BitPluginsColors.OnPrimary,
    secondary = BitPluginsColors.Secondary,
    onSecondary = BitPluginsColors.OnSecondary,
    tertiary = Tertiary,
    onTertiary = BitPluginsColors.OnTertiary,
    error = BitPluginsColors.Error,
    onError = BitPluginsColors.OnError,
    background = BitPluginsColors.BackgroundDark,
    surface = BitPluginsColors.SurfaceDark,
    onBackground = Color.White,
    onSurface = Color.White,
    outline = BitPluginsColors.Outline,
    surfaceVariant = BitPluginsColors.SurfaceVariant,
    onSurfaceVariant = BitPluginsColors.OnSurfaceVariant,
    inversePrimary = BitPluginsColors.StarYellow
)