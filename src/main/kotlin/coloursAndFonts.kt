import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font

// 1. Define your custom colors
val CustomDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFC0050),      // Your main accent color (e.g., Purple)
    secondary = Color(0xFFC4003E),    // Secondary highlight color
    background = Color(0xFF000000),   // Main window background
    surface = Color(0xFF470017),      // Card and container background
    onPrimary = Color.White,          // Text color drawn ON top of primary buttons
    onBackground = Color(0xFFE0E0E0), // Default body text color
    onSurface = Color(0xFFE0E0E0)     // Text color drawn ON top of cards/surface
)

val JetBrainsMono = FontFamily(
    Font(resource = "font/JetBrainsMono-Regular.ttf", weight = FontWeight.Normal),
    Font(resource = "font/JetBrainsMono-Bold.ttf", weight = FontWeight.Bold),
    Font(resource = "font/JetBrainsMono-SemiBold.ttf", weight = FontWeight.SemiBold),
    Font(resource = "font/JetBrainsMono-Light.ttf", weight = FontWeight.Light),
    Font(resource = "font/JetBrainsMono-ExtraLight.ttf", weight = FontWeight.ExtraBold),
    Font(resource = "font/JetBrainsMono-Medium.ttf", weight = FontWeight.Medium)
)

