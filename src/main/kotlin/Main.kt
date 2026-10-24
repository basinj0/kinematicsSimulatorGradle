
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    // This creates an actual desktop window on your screen
    Window(
        onCloseRequest = ::exitApplication,
        title = "Kinematics Simulator"
    ) {
        // This calls the multi-page UI we made, putting it inside the window
        KinematicsUI()
    }
}