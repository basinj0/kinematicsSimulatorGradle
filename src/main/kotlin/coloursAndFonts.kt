import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font

val CustomDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFC0050),
    secondary = Color(0xFFC4003E),
    tertiary = Color(0xFF63001D),
    background = Color(0xFF000000),
    surface = Color(0xFF470017),
    onPrimary = Color.White,
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0)
)

val JetBrainsMono = FontFamily(
    Font(resource = "font/JetBrainsMono-Regular.ttf", weight = FontWeight.Normal),
    Font(resource = "font/JetBrainsMono-Bold.ttf", weight = FontWeight.Bold),
    Font(resource = "font/JetBrainsMono-SemiBold.ttf", weight = FontWeight.SemiBold),
    Font(resource = "font/JetBrainsMono-Light.ttf", weight = FontWeight.Light),
    Font(resource = "font/JetBrainsMono-ExtraBold.ttf", weight = FontWeight.ExtraBold),
    Font(resource = "font/JetBrainsMono-Medium.ttf", weight = FontWeight.Medium)
)

