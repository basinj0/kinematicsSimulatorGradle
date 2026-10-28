import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font

object ThemeManager {

    var currentTheme by mutableStateOf("Dark Theme")
}

object LanguageManager {
    var currentLanguage by mutableStateOf("English")
}

object Settings {
    var currentInputMethod by mutableStateOf("Cartesian")
    var currentSimulationTiming by mutableStateOf("Clock-Time")
    var hertz by mutableStateOf("500")
}

val JetBrainsMono = FontFamily(
    Font(resource = "font/JetBrainsMono-Regular.ttf", weight = FontWeight.Normal),
    Font(resource = "font/JetBrainsMono-Bold.ttf", weight = FontWeight.Bold),
    Font(resource = "font/JetBrainsMono-SemiBold.ttf", weight = FontWeight.SemiBold),
    Font(resource = "font/JetBrainsMono-Light.ttf", weight = FontWeight.Light),
    Font(resource = "font/JetBrainsMono-ExtraBold.ttf", weight = FontWeight.ExtraBold),
    Font(resource = "font/JetBrainsMono-Medium.ttf", weight = FontWeight.Medium)
)

