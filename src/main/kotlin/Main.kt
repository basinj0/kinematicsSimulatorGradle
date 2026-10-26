
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

enum class Step { TITLE, DIMENSION, COMPLEXITY, EXTRAPARAMETERS, INPUTS, RUNNING, RESULTS, CREDITS, HISTORY }

fun main() = application {

    var currentStep by remember { mutableStateOf(Step.TITLE) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Kinematics Simulator",
        onKeyEvent = { event ->
            if (event.type == KeyEventType.KeyDown && event.key == Key.Escape) {

                when (currentStep) {
                    Step.TITLE -> { }
                    Step.DIMENSION -> { currentStep = Step.TITLE }
                    Step.COMPLEXITY -> { currentStep = Step.DIMENSION }
                    Step.EXTRAPARAMETERS -> { currentStep = Step.COMPLEXITY }
                    Step.INPUTS -> { }
                    Step.RUNNING -> { }
                    Step.RESULTS -> { }
                    Step.CREDITS -> { currentStep = Step.TITLE }
                    Step.HISTORY -> { currentStep = Step.TITLE }
                }

                true
            } else {
                false
            }
        }
    ) {
        KinematicsUI(
            currentStep = currentStep,
            onStepChange = { newStep -> currentStep = newStep }
        )
    }
}