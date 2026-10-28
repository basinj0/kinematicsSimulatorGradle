
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import classes.DataCollector
import classes.body.InterBody1D
import classes.body.SimpleBody1D
import classes.body.SimpleBody2D
import classes.Logic
import interfaces.Body
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.TimeSource
import androidx.compose.ui.focus.focusRequester
import classes.SimulationRecords
import androidx.compose.material3.TextField
import com.example.compose.inverseOnSurfaceLight
import com.example.compose.inversePrimaryLight

fun String.convertToDouble(): Double {
    return this.replace(",", ".").toDoubleOrNull() ?: 0.0
}

fun convertPolarToCartesian(magnitude: Double, direction: Double): List<Double> {
    val initialXY = mutableListOf<Double>()

    val directionInRadians = toRadians(direction)

    val initialX = magnitude * (cos(directionInRadians))
    val initialY = magnitude * (sin(directionInRadians))

    initialXY.add(initialX)
    initialXY.add(initialY)
    return initialXY
}

@Composable
fun KinematicsUI(currentStep: Step, onStepChange: (Step) -> Unit) {
    CompositionLocalProvider(
        LocalTextStyle provides LocalTextStyle.current.copy(
            fontFamily = JetBrainsMono
        )
    ) {

        val logic = Logic()

        val mainColourScheme = if (ThemeManager.currentTheme == "Dark Theme") {
            darkColorScheme(
                primary = Color(0xFFFC0050),
                secondary = Color(0xFFC4003E),
                tertiary = Color(0xFF63001D),
                background = Color(0xFF000000),
                surface = Color(0xFF470017),
                onPrimary = Color.White,
                onBackground = Color(0xFFE0E0E0),
                onSurface = Color(0xFFE0E0E0)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFFB3D29D),
                secondary = Color(0xFFBFD8AF),
                tertiary = Color(0xFFE1F0DA),
                background = Color(0xFFEBF5E6),
                surface = Color(0xFFFFFFFF),
                onPrimary = Color(0xFF1B301E),
                onBackground = Color(0xFF1A1C19),
                onSurface = Color(0xFF1A1C19),
            )
        }


        MaterialTheme(
            colorScheme = mainColourScheme
        ) {
            val focusRequester = remember { FocusRequester() }

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(focusRequester)
                    .focusable(),
                color = MaterialTheme.colorScheme.background
            ) {
                var chosenDimension by remember { mutableStateOf(2) }
                var chosenComplexity by remember { mutableStateOf(1) }

                var chosenInputType by remember { mutableStateOf("") }

                var initialDisplacementInput by remember { mutableStateOf("") }
                var initialVelocityInput by remember { mutableStateOf("") }
                var initialAccelerationInput by remember { mutableStateOf("") }
                var durationOfSimulationInput by remember { mutableStateOf("") }
                var initialJerkInput by remember { mutableStateOf("") }

                var initialMagnitudeInput by remember { mutableStateOf("") }
                var initialAngleInput by remember { mutableStateOf("") }
                var initialXVelocityInput by remember { mutableStateOf("") }
                var initialYVelocityInput by remember { mutableStateOf("") }
                var initialXPositionInput by remember { mutableStateOf("") }
                var initialYPositionInput by remember { mutableStateOf("") }
                var initialGravityInput by remember { mutableStateOf("") }

                val scrollState = rememberScrollState()

                var createdObject by remember { mutableStateOf<Body>(SimpleBody1D(0.0, 0.0, 0.0)) }
                var latestHistory by remember { mutableStateOf<DataCollector?>(null) }
                val records = SimulationRecords()
                var listOfRecords by remember { mutableStateOf<List<List<String>>?>(null) }

                var historyIndexSelected by remember { mutableStateOf(0) }

                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val scalar = (sqrt(maxWidth.value * maxHeight.value) / 1000f).coerceIn(0.6f, 2.5f)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Kinematics Simulator",
                            style = MaterialTheme.typography.displayMedium.copy(fontFamily = JetBrainsMono),
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 60.sp * scalar
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(0.8f).fillMaxHeight(0.8f),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = mainColourScheme.surface,
                                contentColor = mainColourScheme.onSurface
                            )
                        ) {

                            Column(
                                modifier = Modifier.padding(32.dp * scalar).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                when (currentStep) {
                                    Step.TITLE -> {
                                        Box {
                                            Card(
                                                modifier = Modifier.align(Alignment.TopEnd).fillMaxWidth(0.345f)
                                                    .fillMaxHeight(),
                                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = mainColourScheme.tertiary,
                                                    contentColor = mainColourScheme.onSurface
                                                )
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                                                ) {
                                                    Column(
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.Center
                                                    ) {
                                                        Spacer(modifier = Modifier.height(12.dp))
                                                        Text(
                                                            modifier = Modifier.padding(horizontal = 16.dp * scalar)
                                                                .padding(vertical = 8.dp * scalar),
                                                            text = "/// Patch Notes ///\n/// FR3A ///",
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = (24 * scalar).sp,
                                                            textAlign = TextAlign.Center,
                                                            fontFamily = JetBrainsMono,
                                                            lineHeight = 30.sp * scalar
                                                        )
                                                        Spacer(Modifier.height(16.dp))
                                                        Text(
                                                            modifier = Modifier.padding(horizontal = 16.dp * scalar)
                                                                .padding(vertical = 8.dp * scalar),
                                                            text = "Added UI \n A UI has been created and implemented with previous logic including running simulations and generating graphs. \n\n" +
                                                                    "Added Settings & History \n A page for a history of simulations that have been run and settings to change the theme of the app.",
                                                            fontWeight = FontWeight.Normal,
                                                            fontSize = (20 * scalar).sp,
                                                            textAlign = TextAlign.Center,
                                                            fontFamily = JetBrainsMono
                                                        )
                                                    }
                                                }
                                            }

                                            Card(
                                                modifier = Modifier.align(Alignment.TopStart).fillMaxWidth(0.345f)
                                                    .fillMaxHeight(),
                                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = mainColourScheme.tertiary,
                                                    contentColor = mainColourScheme.onSurface
                                                )
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.Start,
                                                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                                                ) {
                                                    Column(
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.Center,
                                                        modifier = Modifier.fillMaxHeight().fillMaxWidth()
                                                    ) {
                                                        Button(
                                                            onClick = { onStepChange(Step.CREDITS) },
                                                            shape = RoundedCornerShape(8.dp * scalar),
                                                            colors = ButtonDefaults.buttonColors(
                                                                containerColor = mainColourScheme.secondary
                                                            )
                                                        ) {
                                                            Text(
                                                                text = "Credits",
                                                                fontWeight = FontWeight.Normal,
                                                                fontSize = (24 * scalar).sp,
                                                                textAlign = TextAlign.Center,
                                                                fontFamily = JetBrainsMono,
                                                                modifier = Modifier.padding(horizontal = 16.dp * scalar)
                                                                    .padding(16.dp * scalar)
                                                            )
                                                        }

                                                        Spacer(Modifier.height(24.dp * scalar))

                                                        Button(
                                                            onClick = { onStepChange(Step.SETTINGS) },
                                                            shape = RoundedCornerShape(8.dp * scalar),
                                                            colors = ButtonDefaults.buttonColors(
                                                                containerColor = mainColourScheme.secondary
                                                            )
                                                        ) {
                                                            Text(
                                                                text = "Settings",
                                                                fontWeight = FontWeight.Normal,
                                                                fontSize = (24 * scalar).sp,
                                                                textAlign = TextAlign.Center,
                                                                fontFamily = JetBrainsMono,
                                                                modifier = Modifier.padding(horizontal = 16.dp * scalar)
                                                                    .padding(16.dp * scalar)
                                                            )
                                                        }

                                                        Spacer(Modifier.height(24.dp * scalar))

                                                        Button(
                                                            onClick = { onStepChange(Step.HISTORY) },
                                                            shape = RoundedCornerShape(8.dp * scalar),
                                                            colors = ButtonDefaults.buttonColors(
                                                                containerColor = mainColourScheme.secondary
                                                            )
                                                        ) {
                                                            Text(
                                                                text = "History",
                                                                fontWeight = FontWeight.Normal,
                                                                fontSize = (24 * scalar).sp,
                                                                textAlign = TextAlign.Center,
                                                                fontFamily = JetBrainsMono,
                                                                modifier = Modifier.padding(horizontal = 16.dp * scalar)
                                                                    .padding(16.dp * scalar)
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center,
                                                modifier = Modifier.fillMaxWidth().fillMaxHeight()
                                            ) {
                                                Button(
                                                    onClick = { onStepChange(Step.DIMENSION) },
                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                    modifier = Modifier.padding(horizontal = 4.dp * scalar),
                                                ) {
                                                    Text(
                                                        text = "Start",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = (50 * scalar).sp,
                                                        textAlign = TextAlign.Center,
                                                        fontFamily = JetBrainsMono,
                                                        modifier = Modifier.padding(horizontal = 4.dp * scalar).padding(4.dp * scalar)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Step.DIMENSION -> {
                                        Text(
                                            "Select Number of Dimensions",
                                            fontFamily = JetBrainsMono,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 40.sp * scalar
                                        )
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxWidth().fillMaxHeight()
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Button(
                                                    onClick = {
                                                        chosenDimension = 1; onStepChange(Step.COMPLEXITY)
                                                    },
                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                    modifier = Modifier.padding(horizontal = 8.dp * scalar)
                                                ) {
                                                    Text(
                                                        text = "1D",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 36.sp * scalar,
                                                        textAlign = TextAlign.Center,
                                                        fontFamily = JetBrainsMono,
                                                        modifier = Modifier.padding(horizontal = 8.dp * scalar)
                                                            .padding(4.dp * scalar)
                                                    )
                                                }
                                                Button(
                                                    onClick = {
                                                        chosenDimension = 2; onStepChange(Step.COMPLEXITY)
                                                    },
                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                    modifier = Modifier.padding(horizontal = 8.dp * scalar)
                                                ) {
                                                    Text(
                                                        text = "2D",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 36.sp * scalar,
                                                        textAlign = TextAlign.Center,
                                                        fontFamily = JetBrainsMono,
                                                        modifier = Modifier.padding(horizontal = 8.dp * scalar)
                                                            .padding(4.dp * scalar)
                                                    )
                                                }
                                                Button(
                                                    onClick = { TODO("Implement in Logic before enabling") },
                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                    modifier = Modifier.padding(horizontal = 8.dp * scalar),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = mainColourScheme.secondary,
                                                    )
                                                ) {
                                                    Text(
                                                        text = "--",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 36.sp * scalar,
                                                        textAlign = TextAlign.Center,
                                                        fontFamily = JetBrainsMono,
                                                        modifier = Modifier.padding(horizontal = 8.dp * scalar)
                                                            .padding(4.dp * scalar)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Step.COMPLEXITY -> {
                                        Text(
                                            "Select Complexity",
                                            style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 40.sp * scalar
                                        )
                                        Column(
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.fillMaxWidth().fillMaxHeight()
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Button(
                                                    onClick = {
                                                        chosenComplexity = 1
                                                        if (chosenDimension == 1) {
                                                            onStepChange(Step.INPUTS)
                                                            initialDisplacementInput = ""
                                                            initialVelocityInput = ""
                                                            initialAccelerationInput = ""
                                                            durationOfSimulationInput = ""
                                                            initialJerkInput = ""

                                                            initialMagnitudeInput = ""
                                                            initialAngleInput = ""
                                                            initialXVelocityInput = ""
                                                            initialYVelocityInput = ""
                                                            initialXPositionInput = ""
                                                            initialYPositionInput = ""
                                                            initialGravityInput = ""
                                                        } else {
                                                            onStepChange(Step.EXTRAPARAMETERS)
                                                        }
                                                    },
                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                    modifier = Modifier.padding(horizontal = 4.dp * scalar)
                                                ) {
                                                    Text(
                                                        text = "Simple",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 30.sp * scalar,
                                                        textAlign = TextAlign.Center,
                                                        fontFamily = JetBrainsMono,
                                                        modifier = Modifier.padding(horizontal = 4.dp * scalar)
                                                            .padding(4.dp * scalar)
                                                    )
                                                }
                                                Button(
                                                    onClick = {
                                                        chosenComplexity = 2
                                                        if (chosenDimension == 1) {
                                                            onStepChange(Step.INPUTS)
                                                            initialDisplacementInput = ""
                                                            initialVelocityInput = ""
                                                            initialAccelerationInput = ""
                                                            durationOfSimulationInput = ""
                                                            initialJerkInput = ""

                                                            initialMagnitudeInput = ""
                                                            initialAngleInput = ""
                                                            initialXVelocityInput = ""
                                                            initialYVelocityInput = ""
                                                            initialXPositionInput = ""
                                                            initialYPositionInput = ""
                                                            initialGravityInput = ""
                                                        } else {
                                                            onStepChange(Step.EXTRAPARAMETERS)
                                                        }
                                                    },
                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                    modifier = Modifier.padding(horizontal = 4.dp * scalar)
                                                ) {
                                                    Text(
                                                        text = "Intermediate",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 30.sp * scalar,
                                                        textAlign = TextAlign.Center,
                                                        fontFamily = JetBrainsMono,
                                                        modifier = Modifier.padding(horizontal = 4.dp * scalar)
                                                            .padding(4.dp * scalar)
                                                    )
                                                }
                                                Button(
                                                    onClick = { TODO("Implement in Logic before enabling") },
                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                    modifier = Modifier.padding(horizontal = 4.dp * scalar),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = mainColourScheme.secondary,
                                                    )
                                                ) {
                                                    Text(
                                                        text = "Complex",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 30.sp * scalar,
                                                        textAlign = TextAlign.Center,
                                                        fontFamily = JetBrainsMono,
                                                        modifier = Modifier.padding(horizontal = 4.dp * scalar)
                                                            .padding(4.dp * scalar)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Step.EXTRAPARAMETERS -> {
                                        Text(
                                            "Select Input Method",
                                            style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 40.sp * scalar
                                        )
                                        Spacer(modifier = Modifier.height(20.dp))
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxWidth().fillMaxHeight()
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(12.dp * scalar),
                                            ) {
                                                Button(
                                                    onClick = {
                                                        chosenInputType = "Polar"
                                                        onStepChange(Step.INPUTS)
                                                        initialDisplacementInput = ""
                                                        initialVelocityInput = ""
                                                        initialAccelerationInput = ""
                                                        durationOfSimulationInput = ""
                                                        initialJerkInput = ""

                                                        initialMagnitudeInput = ""
                                                        initialAngleInput = ""
                                                        initialXVelocityInput = ""
                                                        initialYVelocityInput = ""
                                                        initialXPositionInput = ""
                                                        initialYPositionInput = ""
                                                        initialGravityInput = ""
                                                    },
                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                    modifier = Modifier.padding(horizontal = 4.dp * scalar)
                                                ) {
                                                    Text(
                                                        text = "Polar",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 30.sp * scalar,
                                                        textAlign = TextAlign.Center,
                                                        fontFamily = JetBrainsMono,
                                                        modifier = Modifier.padding(horizontal = 4.dp * scalar)
                                                            .padding(4.dp * scalar)
                                                    )
                                                }
                                                Button(
                                                    onClick = {
                                                        chosenInputType = "Cartesian"
                                                        onStepChange(Step.INPUTS)
                                                        initialDisplacementInput = ""
                                                        initialVelocityInput = ""
                                                        initialAccelerationInput = ""
                                                        durationOfSimulationInput = ""
                                                        initialJerkInput = ""

                                                        initialMagnitudeInput = ""
                                                        initialAngleInput = ""
                                                        initialXVelocityInput = ""
                                                        initialYVelocityInput = ""
                                                        initialXPositionInput = ""
                                                        initialYPositionInput = ""
                                                        initialGravityInput = ""
                                                    },
                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                    modifier = Modifier.padding(horizontal = 4.dp * scalar)
                                                ) {
                                                    Text(
                                                        text = "Cartesian",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 30.sp * scalar,
                                                        textAlign = TextAlign.Center,
                                                        fontFamily = JetBrainsMono,
                                                        modifier = Modifier.padding(horizontal = 4.dp * scalar)
                                                            .padding(4.dp * scalar)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Step.INPUTS -> {
                                        when (chosenDimension) {
                                            1 if chosenComplexity == 1 -> {
                                                Text(
                                                    "Enter Initial Parameters",
                                                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 40.sp * scalar
                                                )

                                                Spacer(modifier = Modifier.height(16.dp))

                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .verticalScroll(scrollState),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {

                                                    OutlinedTextField(
                                                        value = initialDisplacementInput,
                                                        onValueChange = { initialDisplacementInput = it },
                                                        label = {
                                                            Text(
                                                                "Initial Displacement (m)",
                                                                style = MaterialTheme.typography.titleLarge.copy(
                                                                    fontFamily = JetBrainsMono
                                                                ),
                                                                fontWeight = FontWeight.Light,
                                                                fontSize = 16.sp * scalar
                                                            )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textStyle = TextStyle(
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp,
                                                            color = mainColourScheme.primary
                                                        )
                                                    )

                                                    Spacer(modifier = Modifier.height(12.dp))

                                                    OutlinedTextField(
                                                        value = initialVelocityInput,
                                                        onValueChange = { initialVelocityInput = it },
                                                        label = {
                                                            Text(
                                                                "Initial Velocity (m/s)",
                                                                style = MaterialTheme.typography.titleLarge.copy(
                                                                    fontFamily = JetBrainsMono
                                                                ),
                                                                fontWeight = FontWeight.Light,
                                                                fontSize = 16.sp * scalar
                                                            )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textStyle = TextStyle(
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp,
                                                            color = mainColourScheme.primary
                                                        )
                                                    )

                                                    Spacer(modifier = Modifier.height(12.dp))

                                                    OutlinedTextField(
                                                        value = initialAccelerationInput,
                                                        onValueChange = { initialAccelerationInput = it },
                                                        label = {
                                                            Text(
                                                                "Initial Acceleration (m/s^2)",
                                                                style = MaterialTheme.typography.titleLarge.copy(
                                                                    fontFamily = JetBrainsMono
                                                                ),
                                                                fontWeight = FontWeight.Light,
                                                                fontSize = 16.sp * scalar
                                                            )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textStyle = TextStyle(
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp,
                                                            color = mainColourScheme.primary
                                                        )
                                                    )

                                                    Spacer(modifier = Modifier.height(12.dp))

                                                    OutlinedTextField(
                                                        value = durationOfSimulationInput,
                                                        onValueChange = { durationOfSimulationInput = it },
                                                        label = {
                                                            Text(
                                                                "Duration of Simulation (s)",
                                                                style = MaterialTheme.typography.titleLarge.copy(
                                                                    fontFamily = JetBrainsMono
                                                                ),
                                                                fontWeight = FontWeight.Light,
                                                                fontSize = 16.sp * scalar
                                                            )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textStyle = TextStyle(
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp,
                                                            color = mainColourScheme.primary
                                                        )
                                                    )

                                                    Spacer(modifier = Modifier.height(24.dp))

                                                    Button(
                                                        onClick = {
                                                            createdObject = SimpleBody1D(
                                                                initialVelocityInput.convertToDouble(),
                                                                initialAccelerationInput.convertToDouble(),
                                                                initialDisplacementInput.convertToDouble()
                                                            )
                                                            onStepChange(Step.RUNNING)
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        shape = RoundedCornerShape(8.dp * scalar),
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = mainColourScheme.primary,
                                                        )
                                                    ) {
                                                        Text(
                                                            "Run Simulation",
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 30.sp * scalar,
                                                            textAlign = TextAlign.Center,
                                                            fontFamily = JetBrainsMono
                                                        )
                                                    }
                                                }
                                            }

                                            1 if chosenComplexity == 2 -> {
                                                Text(
                                                    "Enter Initial Parameters",
                                                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 40.sp * scalar
                                                )

                                                Spacer(modifier = Modifier.height(16.dp))

                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .verticalScroll(scrollState),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {

                                                    OutlinedTextField(
                                                        value = initialDisplacementInput,
                                                        onValueChange = { initialDisplacementInput = it },
                                                        label = {
                                                            Text(
                                                                "Initial Displacement (m)",
                                                                style = MaterialTheme.typography.titleLarge.copy(
                                                                    fontFamily = JetBrainsMono
                                                                ),
                                                                fontWeight = FontWeight.Light,
                                                                fontSize = 16.sp * scalar
                                                            )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textStyle = TextStyle(
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp,
                                                            color = mainColourScheme.primary
                                                        )
                                                    )

                                                    Spacer(modifier = Modifier.height(12.dp))

                                                    OutlinedTextField(
                                                        value = initialVelocityInput,
                                                        onValueChange = { initialVelocityInput = it },
                                                        label = {
                                                            Text(
                                                                "Initial Velocity (m/s)",
                                                                style = MaterialTheme.typography.titleLarge.copy(
                                                                    fontFamily = JetBrainsMono
                                                                ),
                                                                fontWeight = FontWeight.Light,
                                                                fontSize = 16.sp * scalar
                                                            )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textStyle = TextStyle(
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp,
                                                            color = mainColourScheme.primary
                                                        )
                                                    )

                                                    Spacer(modifier = Modifier.height(12.dp))

                                                    OutlinedTextField(
                                                        value = initialAccelerationInput,
                                                        onValueChange = { initialAccelerationInput = it },
                                                        label = {
                                                            Text(
                                                                "Initial Acceleration (m/s^2)",
                                                                style = MaterialTheme.typography.titleLarge.copy(
                                                                    fontFamily = JetBrainsMono
                                                                ),
                                                                fontWeight = FontWeight.Light,
                                                                fontSize = 16.sp * scalar
                                                            )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textStyle = TextStyle(
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp,
                                                            color = mainColourScheme.primary
                                                        )
                                                    )

                                                    Spacer(modifier = Modifier.height(12.dp))

                                                    OutlinedTextField(
                                                        value = initialJerkInput,
                                                        onValueChange = { initialJerkInput = it },
                                                        label = {
                                                            Text(
                                                                "Initial Jerk (m/s^3)",
                                                                style = MaterialTheme.typography.titleLarge.copy(
                                                                    fontFamily = JetBrainsMono
                                                                ),
                                                                fontWeight = FontWeight.Light,
                                                                fontSize = 16.sp * scalar
                                                            )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textStyle = TextStyle(
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp,
                                                            color = mainColourScheme.primary
                                                        )
                                                    )

                                                    Spacer(modifier = Modifier.height(12.dp))

                                                    OutlinedTextField(
                                                        value = durationOfSimulationInput,
                                                        onValueChange = { durationOfSimulationInput = it },
                                                        label = {
                                                            Text(
                                                                "Duration of Simulation (s)",
                                                                style = MaterialTheme.typography.titleLarge.copy(
                                                                    fontFamily = JetBrainsMono
                                                                ),
                                                                fontWeight = FontWeight.Light,
                                                                fontSize = 16.sp * scalar
                                                            )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textStyle = TextStyle(
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp,
                                                            color = mainColourScheme.primary
                                                        )
                                                    )

                                                    Spacer(modifier = Modifier.height(24.dp))

                                                    Button(
                                                        onClick = {
                                                            createdObject = InterBody1D(
                                                                initialVelocityInput.convertToDouble(),
                                                                initialAccelerationInput.convertToDouble(),
                                                                initialDisplacementInput.convertToDouble(),
                                                                initialJerkInput.convertToDouble()
                                                            )
                                                            onStepChange(Step.RUNNING)
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        shape = RoundedCornerShape(8.dp * scalar),
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = mainColourScheme.primary,
                                                        )
                                                    ) {
                                                        Text(
                                                            "Run Simulation",
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 30.sp * scalar,
                                                            textAlign = TextAlign.Center,
                                                            fontFamily = JetBrainsMono
                                                        )
                                                    }
                                                }
                                            }

                                            2 if chosenComplexity == 1 && chosenInputType == "Polar" -> {
                                                Text(
                                                    "Enter Initial Parameters",
                                                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 40.sp * scalar
                                                )

                                                Spacer(modifier = Modifier.height(16.dp))

                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .verticalScroll(scrollState),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {

                                                    OutlinedTextField(
                                                        value = initialAngleInput,
                                                        onValueChange = { initialAngleInput = it },
                                                        label = {
                                                            Text(
                                                                "Initial Angle (°)",
                                                                style = MaterialTheme.typography.titleLarge.copy(
                                                                    fontFamily = JetBrainsMono
                                                                ),
                                                                fontWeight = FontWeight.Light,
                                                                fontSize = 16.sp * scalar
                                                            )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textStyle = TextStyle(
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp,
                                                            color = mainColourScheme.primary
                                                        )
                                                    )

                                                    Spacer(modifier = Modifier.height(12.dp))

                                                    OutlinedTextField(
                                                        value = initialMagnitudeInput,
                                                        onValueChange = { initialMagnitudeInput = it },
                                                        label = {
                                                            Text(
                                                                "Initial Magnitude (m/s)",
                                                                style = MaterialTheme.typography.titleLarge.copy(
                                                                    fontFamily = JetBrainsMono
                                                                ),
                                                                fontWeight = FontWeight.Light,
                                                                fontSize = 16.sp * scalar
                                                            )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textStyle = TextStyle(
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp,
                                                            color = mainColourScheme.primary
                                                        )
                                                    )

                                                    Spacer(modifier = Modifier.height(12.dp))

                                                    OutlinedTextField(
                                                        value = initialGravityInput,
                                                        onValueChange = { initialGravityInput = it },
                                                        label = {
                                                            Text(
                                                                "Initial Gravity (m/s^2) [Negative is down]",
                                                                style = MaterialTheme.typography.titleLarge.copy(
                                                                    fontFamily = JetBrainsMono
                                                                ),
                                                                fontWeight = FontWeight.Light,
                                                                fontSize = 16.sp * scalar
                                                            )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textStyle = TextStyle(
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp,
                                                            color = mainColourScheme.primary
                                                        )
                                                    )

                                                    Spacer(modifier = Modifier.height(12.dp))

                                                    OutlinedTextField(
                                                        value = initialAccelerationInput,
                                                        onValueChange = { initialAccelerationInput = it },
                                                        label = {
                                                            Text(
                                                                "Initial X Position (m)",
                                                                style = MaterialTheme.typography.titleLarge.copy(
                                                                    fontFamily = JetBrainsMono
                                                                ),
                                                                fontWeight = FontWeight.Light,
                                                                fontSize = 16.sp * scalar
                                                            )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textStyle = TextStyle(
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp,
                                                            color = mainColourScheme.primary
                                                        )
                                                    )

                                                    Spacer(modifier = Modifier.height(12.dp))

                                                    OutlinedTextField(
                                                        value = initialYPositionInput,
                                                        onValueChange = { initialYPositionInput = it },
                                                        label = {
                                                            Text(
                                                                "Initial Y Position (m)",
                                                                style = MaterialTheme.typography.titleLarge.copy(
                                                                    fontFamily = JetBrainsMono
                                                                ),
                                                                fontWeight = FontWeight.Light,
                                                                fontSize = 16.sp * scalar
                                                            )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textStyle = TextStyle(
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp,
                                                            color = mainColourScheme.primary
                                                        )
                                                    )

                                                    Spacer(modifier = Modifier.height(12.dp))

                                                    OutlinedTextField(
                                                        value = durationOfSimulationInput,
                                                        onValueChange = { durationOfSimulationInput = it },
                                                        label = {
                                                            Text(
                                                                "Duration of Simulation (s)",
                                                                style = MaterialTheme.typography.titleLarge.copy(
                                                                    fontFamily = JetBrainsMono
                                                                ),
                                                                fontWeight = FontWeight.Light,
                                                                fontSize = 16.sp * scalar
                                                            )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textStyle = TextStyle(
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp,
                                                            color = mainColourScheme.primary
                                                        )
                                                    )

                                                    Spacer(modifier = Modifier.height(24.dp))

                                                    Button(
                                                        onClick = {
                                                            val velocityXY = convertPolarToCartesian(
                                                                initialMagnitudeInput.convertToDouble(),
                                                                initialAngleInput.convertToDouble()
                                                            )

                                                            createdObject = SimpleBody2D(
                                                                velocityXY[0],
                                                                velocityXY[1],
                                                                initialGravityInput.convertToDouble(),
                                                                initialXPositionInput.convertToDouble(),
                                                                initialYPositionInput.convertToDouble(),
                                                                0.0,
                                                                0.0
                                                            )

                                                            onStepChange(Step.RUNNING)
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        shape = RoundedCornerShape(8.dp * scalar),
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = mainColourScheme.primary,
                                                        )
                                                    ) {
                                                        Text(
                                                            "Run Simulation",
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 30.sp * scalar,
                                                            textAlign = TextAlign.Center,
                                                            fontFamily = JetBrainsMono
                                                        )
                                                    }
                                                }
                                            }

                                            2 if chosenComplexity == 1 && chosenInputType == "Cartesian" -> {
                                                Text(
                                                    "Enter Initial Parameters",
                                                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 40.sp * scalar
                                                )

                                                Spacer(modifier = Modifier.height(16.dp))

                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .verticalScroll(scrollState),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {

                                                    OutlinedTextField(
                                                        value = initialXVelocityInput,
                                                        onValueChange = { initialXVelocityInput = it },
                                                        label = {
                                                            Text(
                                                                "Initial X Velocity (m/s)",
                                                                style = MaterialTheme.typography.titleLarge.copy(
                                                                    fontFamily = JetBrainsMono
                                                                ),
                                                                fontWeight = FontWeight.Light,
                                                                fontSize = 16.sp * scalar
                                                            )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textStyle = TextStyle(
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp,
                                                            color = mainColourScheme.primary
                                                        )
                                                    )

                                                    Spacer(modifier = Modifier.height(12.dp))

                                                    OutlinedTextField(
                                                        value = initialYVelocityInput,
                                                        onValueChange = { initialYVelocityInput = it },
                                                        label = {
                                                            Text(
                                                                "Initial Y Velocity (m/s)",
                                                                style = MaterialTheme.typography.titleLarge.copy(
                                                                    fontFamily = JetBrainsMono
                                                                ),
                                                                fontWeight = FontWeight.Light,
                                                                fontSize = 16.sp * scalar
                                                            )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textStyle = TextStyle(
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp,
                                                            color = mainColourScheme.primary
                                                        )
                                                    )

                                                    Spacer(modifier = Modifier.height(12.dp))

                                                    OutlinedTextField(
                                                        value = initialGravityInput,
                                                        onValueChange = { initialGravityInput = it },
                                                        label = {
                                                            Text(
                                                                "Initial Gravity (m/s^2) [Negative is down]",
                                                                style = MaterialTheme.typography.titleLarge.copy(
                                                                    fontFamily = JetBrainsMono
                                                                ),
                                                                fontWeight = FontWeight.Light,
                                                                fontSize = 16.sp * scalar
                                                            )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textStyle = TextStyle(
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp,
                                                            color = mainColourScheme.primary
                                                        )
                                                    )

                                                    Spacer(modifier = Modifier.height(12.dp))

                                                    OutlinedTextField(
                                                        value = initialXPositionInput,
                                                        onValueChange = { initialXPositionInput = it },
                                                        label = {
                                                            Text(
                                                                "Initial X Position (m)",
                                                                style = MaterialTheme.typography.titleLarge.copy(
                                                                    fontFamily = JetBrainsMono
                                                                ),
                                                                fontWeight = FontWeight.Light,
                                                                fontSize = 16.sp * scalar
                                                            )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textStyle = TextStyle(
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp,
                                                            color = mainColourScheme.primary
                                                        )
                                                    )

                                                    Spacer(modifier = Modifier.height(12.dp))

                                                    OutlinedTextField(
                                                        value = initialYPositionInput,
                                                        onValueChange = { initialYPositionInput = it },
                                                        label = {
                                                            Text(
                                                                "Initial Y Position (m)",
                                                                style = MaterialTheme.typography.titleLarge.copy(
                                                                    fontFamily = JetBrainsMono
                                                                ),
                                                                fontWeight = FontWeight.Light,
                                                                fontSize = 16.sp * scalar
                                                            )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textStyle = TextStyle(
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp,
                                                            color = mainColourScheme.primary
                                                        )
                                                    )

                                                    Spacer(modifier = Modifier.height(12.dp))

                                                    OutlinedTextField(
                                                        value = durationOfSimulationInput,
                                                        onValueChange = { durationOfSimulationInput = it },
                                                        label = {
                                                            Text(
                                                                "Duration of Simulation (s)",
                                                                style = MaterialTheme.typography.titleLarge.copy(
                                                                    fontFamily = JetBrainsMono
                                                                ),
                                                                fontWeight = FontWeight.Light,
                                                                fontSize = 16.sp * scalar
                                                            )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textStyle = TextStyle(
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp,
                                                            color = mainColourScheme.primary
                                                        )
                                                    )

                                                    Spacer(modifier = Modifier.height(24.dp))

                                                    Button(
                                                        onClick = {
                                                            createdObject = SimpleBody2D(
                                                                initialXVelocityInput.convertToDouble(),
                                                                initialYVelocityInput.convertToDouble(),
                                                                initialGravityInput.convertToDouble(),
                                                                initialXPositionInput.convertToDouble(),
                                                                initialYPositionInput.convertToDouble(),
                                                                0.0,
                                                                0.0
                                                            )
                                                            onStepChange(Step.RUNNING)
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        shape = RoundedCornerShape(8.dp * scalar),
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = mainColourScheme.primary,
                                                        )
                                                    ) {
                                                        Text(
                                                            "Run Simulation",
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 30.sp * scalar,
                                                            textAlign = TextAlign.Center,
                                                            fontFamily = JetBrainsMono
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Step.RUNNING -> {

                                        when (chosenDimension) {
                                            1 if chosenComplexity == 1 -> {

                                                var liveTime by remember { mutableStateOf(0.0) }
                                                var liveDisplacement by remember { mutableStateOf(0.0) }
                                                var liveVelocity by remember { mutableStateOf(0.0) }
                                                var liveAcceleration by remember { mutableStateOf(0.0) }

                                                LaunchedEffect(Unit) {
                                                    val body = createdObject
                                                    val timeSource = TimeSource.Monotonic
                                                    val startTime = timeSource.markNow()

                                                    latestHistory = withContext(Dispatchers.Default) {
                                                        logic.simulation1DSimple(
                                                            body = body as SimpleBody1D,
                                                            simulationDuration = durationOfSimulationInput.convertToDouble(),
                                                            lastTime = startTime
                                                        ) { time, disp, vel, acc ->
                                                            liveTime = time
                                                            liveDisplacement = disp
                                                            liveVelocity = vel
                                                            liveAcceleration = acc
                                                        }
                                                    }
                                                }

                                                Column(
                                                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                ) {
                                                    Text(
                                                        "Simulation Running",
                                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 40.sp * scalar,
                                                        color = mainColourScheme.primary,
                                                        textAlign = TextAlign.Left
                                                    )
                                                    Spacer(modifier = Modifier.height(16.dp))

                                                    Column(
                                                        modifier = Modifier.fillMaxWidth().weight(1.0f)
                                                            .verticalScroll(scrollState),
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.Center
                                                    ) {
                                                        Text(
                                                            "Time: ${String.format("%.2f", liveTime)} s",
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp * scalar
                                                        )
                                                        Spacer(modifier = Modifier.height(12.dp * scalar))
                                                        Text(
                                                            "Displacement: ${
                                                                String.format(
                                                                    "%.3f",
                                                                    liveDisplacement
                                                                )
                                                            } m",
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp * scalar
                                                        )
                                                        Spacer(modifier = Modifier.height(12.dp * scalar))
                                                        Text(
                                                            "Velocity: ${String.format("%.3f", liveVelocity)} m/s",
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp * scalar
                                                        )
                                                        Spacer(modifier = Modifier.height(12.dp * scalar))
                                                        Text(
                                                            "Acceleration: ${
                                                                String.format(
                                                                    "%.3f",
                                                                    liveAcceleration
                                                                )
                                                            } m/s^2",
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp * scalar
                                                        )
                                                    }

                                                    Spacer(modifier = Modifier.height(24.dp))

                                                    if (liveTime >= durationOfSimulationInput.convertToDouble()) {
                                                        OutlinedButton(
                                                            onClick = { onStepChange(Step.RESULTS) },
                                                            border = BorderStroke(1.dp * scalar, Color(0xFFC4003E)),
                                                            shape = RoundedCornerShape(8.dp * scalar),
                                                            modifier = Modifier.padding(horizontal = 4.dp * scalar),
                                                        ) {
                                                            Text(
                                                                "Results",
                                                                fontFamily = JetBrainsMono,
                                                                fontSize = 30.sp * scalar
                                                            )
                                                        }
                                                    } else {
                                                        OutlinedButton(
                                                            onClick = { },
                                                            border = BorderStroke(1.dp * scalar, Color(0x40C4003E)),
                                                            shape = RoundedCornerShape(8.dp * scalar),
                                                            modifier = Modifier.padding(horizontal = 4.dp * scalar)
                                                        ) {
                                                            Text(
                                                                "Results",
                                                                fontFamily = JetBrainsMono,
                                                                fontSize = 30.sp * scalar,
                                                                color = Color(0x40FFFFFF)
                                                            )
                                                        }
                                                    }
                                                }

                                            }

                                            1 if chosenComplexity == 2 -> {

                                                var liveTime by remember { mutableStateOf(0.0) }
                                                var liveDisplacement by remember { mutableStateOf(0.0) }
                                                var liveVelocity by remember { mutableStateOf(0.0) }
                                                var liveAcceleration by remember { mutableStateOf(0.0) }
                                                var liveJerk by remember { mutableStateOf(0.0) }

                                                LaunchedEffect(Unit) {
                                                    val body = createdObject
                                                    val timeSource = TimeSource.Monotonic
                                                    val startTime = timeSource.markNow()

                                                    latestHistory = withContext(Dispatchers.Default) {
                                                        logic.simulation1DInter(
                                                            body = body as InterBody1D,
                                                            simulationDuration = durationOfSimulationInput.convertToDouble(),
                                                            lastTime = startTime
                                                        ) { time, disp, vel, acc, jerk ->
                                                            liveTime = time
                                                            liveDisplacement = disp
                                                            liveVelocity = vel
                                                            liveAcceleration = acc
                                                            liveJerk = jerk
                                                        }
                                                    }
                                                }

                                                Column(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Text(
                                                        "Simulation Running...",
                                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 40.sp * scalar,
                                                        color = mainColourScheme.primary
                                                    )
                                                    Spacer(modifier = Modifier.height(24.dp))

                                                    Column(
                                                        modifier = Modifier.fillMaxWidth().weight(1.0f)
                                                            .verticalScroll(scrollState),
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.Center
                                                    ) {
                                                        Text(
                                                            "Time: ${String.format("%.2f", liveTime)} s",
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp * scalar
                                                        )
                                                        Spacer(modifier = Modifier.height(12.dp * scalar))
                                                        Text(
                                                            "Displacement: ${
                                                                String.format(
                                                                    "%.3f",
                                                                    liveDisplacement
                                                                )
                                                            } m",
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp * scalar
                                                        )
                                                        Spacer(modifier = Modifier.height(12.dp * scalar))
                                                        Text(
                                                            "Velocity: ${String.format("%.3f", liveVelocity)} m/s",
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp * scalar
                                                        )
                                                        Spacer(modifier = Modifier.height(12.dp * scalar))
                                                        Text(
                                                            "Acceleration: ${
                                                                String.format(
                                                                    "%.3f",
                                                                    liveAcceleration
                                                                )
                                                            } m/s^2",
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp * scalar
                                                        )
                                                        Spacer(modifier = Modifier.height(12.dp * scalar))
                                                        Text(
                                                            "Jerk: ${String.format("%.3f", liveJerk)} m/s^3",
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp * scalar
                                                        )
                                                    }

                                                    Spacer(modifier = Modifier.height(24.dp))

                                                    if (liveTime >= durationOfSimulationInput.convertToDouble()) {
                                                        OutlinedButton(
                                                            onClick = { onStepChange(Step.RESULTS) },
                                                            border = BorderStroke(1.dp * scalar, Color(0xFFC4003E)),
                                                            shape = RoundedCornerShape(8.dp * scalar),
                                                            modifier = Modifier.padding(horizontal = 4.dp * scalar)
                                                        ) {
                                                            Text(
                                                                "Results",
                                                                fontFamily = JetBrainsMono,
                                                                fontSize = 30.sp * scalar
                                                            )
                                                        }
                                                    } else {
                                                        OutlinedButton(
                                                            onClick = { },
                                                            border = BorderStroke(1.dp * scalar, Color(0x40C4003E)),
                                                            shape = RoundedCornerShape(8.dp * scalar),
                                                            modifier = Modifier.padding(horizontal = 4.dp * scalar)
                                                        ) {
                                                            Text(
                                                                "Results",
                                                                fontFamily = JetBrainsMono,
                                                                fontSize = 30.sp * scalar,
                                                                color = Color(0x40FFFFFF)
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            2 if chosenComplexity == 1 -> {

                                                var liveTime by remember { mutableStateOf(0.0) }
                                                var liveXPosition by remember { mutableStateOf(0.0) }
                                                var liveYPosition by remember { mutableStateOf(0.0) }
                                                var liveXVelocity by remember { mutableStateOf(0.0) }
                                                var liveYVelocity by remember { mutableStateOf(0.0) }
                                                var liveVelocity by remember { mutableStateOf(0.0) }
                                                var liveGravity by remember { mutableStateOf(0.0) }

                                                LaunchedEffect(Unit) {

                                                    val body = createdObject
                                                    val timeSource = TimeSource.Monotonic
                                                    val startTime = timeSource.markNow()

                                                    latestHistory = withContext(Dispatchers.Default) {
                                                        logic.simulation2DSimple(
                                                            body = body as SimpleBody2D,
                                                            simulationDuration = durationOfSimulationInput.convertToDouble(),
                                                            lastTime = startTime
                                                        ) { time, xPos, yPos, xVel, yVel, vel, grav ->
                                                            liveTime = time
                                                            liveXPosition = xPos
                                                            liveYPosition = yPos
                                                            liveXVelocity = xVel
                                                            liveYVelocity = yVel
                                                            liveVelocity = vel
                                                            liveGravity = grav
                                                        }
                                                    }
                                                }

                                                Column(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Text(
                                                        "Simulation Running...",
                                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 40.sp * scalar,
                                                        color = mainColourScheme.primary
                                                    )
                                                    Spacer(modifier = Modifier.height(24.dp))

                                                    Column(
                                                        modifier = Modifier.fillMaxWidth().weight(1.0f)
                                                            .verticalScroll(scrollState),
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.Center
                                                    ) {
                                                        Text(
                                                            "Time: ${String.format("%.2f", liveTime)} s",
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp * scalar
                                                        )
                                                        Spacer(modifier = Modifier.height(12.dp * scalar))
                                                        Text(
                                                            "X Position: ${String.format("%.3f", liveXPosition)} m",
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp * scalar
                                                        )
                                                        Spacer(modifier = Modifier.height(12.dp * scalar))
                                                        Text(
                                                            "Y Position: ${String.format("%.3f", liveYPosition)} m",
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp * scalar
                                                        )
                                                        Spacer(modifier = Modifier.height(12.dp * scalar))
                                                        Text(
                                                            "X Velocity: ${
                                                                String.format(
                                                                    "%.3f",
                                                                    liveXVelocity
                                                                )
                                                            } m/s",
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp * scalar
                                                        )
                                                        Spacer(modifier = Modifier.height(12.dp * scalar))
                                                        Text(
                                                            "Y Velocity: ${
                                                                String.format(
                                                                    "%.3f",
                                                                    liveYVelocity
                                                                )
                                                            } m/s",
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp * scalar
                                                        )
                                                        Spacer(modifier = Modifier.height(12.dp * scalar))
                                                        Text(
                                                            "Velocity: ${String.format("%.3f", liveVelocity)} m/s",
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp * scalar
                                                        )
                                                        Spacer(modifier = Modifier.height(12.dp * scalar))
                                                        Text(
                                                            "Gravity: ${String.format("%.3f", liveGravity)} m/s^2",
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp * scalar
                                                        )
                                                    }

                                                    Spacer(modifier = Modifier.height(24.dp))

                                                    if (liveTime >= durationOfSimulationInput.convertToDouble()) {
                                                        OutlinedButton(
                                                            onClick = { onStepChange(Step.RESULTS) },
                                                            border = BorderStroke(1.dp * scalar, mainColourScheme.secondary),
                                                            shape = RoundedCornerShape(8.dp * scalar),
                                                            modifier = Modifier.padding(horizontal = 4.dp * scalar)
                                                        ) {
                                                            Text(
                                                                "Results",
                                                                fontFamily = JetBrainsMono,
                                                                fontSize = 30.sp * scalar,
                                                                color = mainColourScheme.onSurface
                                                            )
                                                        }
                                                    } else {
                                                        OutlinedButton(
                                                            onClick = { },
                                                            border = BorderStroke(1.dp * scalar, mainColourScheme.secondary.copy(alpha = 0.3f)),
                                                            shape = RoundedCornerShape(8.dp * scalar),
                                                            modifier = Modifier.padding(horizontal = 4.dp * scalar)
                                                        ) {
                                                            Text(
                                                                "Results",
                                                                fontFamily = JetBrainsMono,
                                                                fontSize = 30.sp * scalar,
                                                                color = mainColourScheme.onSurface.copy(alpha = 0.3f)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Step.RESULTS -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                "Results",
                                                style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 40.sp * scalar,
                                                color = mainColourScheme.primary
                                            )

                                            Spacer(modifier = Modifier.height(24.dp))

                                            Column(
                                                modifier = Modifier.fillMaxWidth().weight(1.0f),
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = latestHistory?.exportHistory(
                                                        chosenDimension,
                                                        chosenComplexity
                                                    )
                                                        ?: "History Failed to Generate",
                                                    fontFamily = JetBrainsMono,
                                                    fontWeight = FontWeight.Normal,
                                                    modifier = Modifier.padding(horizontal = 12.dp * scalar),
                                                    fontSize = 20.sp * scalar
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(24.dp))

                                            Button(
                                                shape = RoundedCornerShape(8.dp * scalar),
                                                onClick = {
                                                    when (chosenDimension) {
                                                        1 if chosenComplexity == 1 -> {
                                                            latestHistory?.generateGraph(
                                                                "Displacement / Time",
                                                                "Time",
                                                                "Displacement",
                                                                latestHistory?.pullVariable(0),
                                                                latestHistory?.pullVariable(1)
                                                            ) ?: error("Graph failed to generate.")
                                                        }

                                                        1 if chosenComplexity == 2 -> {
                                                            latestHistory?.generateGraph(
                                                                "Displacement / Time",
                                                                "Time",
                                                                "Displacement",
                                                                latestHistory?.pullVariable(0),
                                                                latestHistory?.pullVariable(1)
                                                            ) ?: error("Graph failed to generate.")
                                                        }

                                                        2 if chosenComplexity == 1 -> {
                                                            latestHistory?.generateGraph(
                                                                "Position Y / Position X",
                                                                "Position X",
                                                                "Position Y",
                                                                latestHistory?.pullVariable(1),
                                                                latestHistory?.pullVariable(2)
                                                            ) ?: error("Graph failed to generate.")
                                                        }
                                                    }
                                                }
                                            ) {
                                                Text(
                                                    "Generate Graph",
                                                    fontFamily = JetBrainsMono,
                                                    fontWeight = FontWeight.Normal,
                                                    fontSize = 30.sp * scalar
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(24.dp))

                                            Button(
                                                shape = RoundedCornerShape(8.dp * scalar),
                                                onClick = { onStepChange(Step.TITLE) },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = mainColourScheme.secondary
                                                )
                                            ) {
                                                Text(
                                                    "Reset",
                                                    fontFamily = JetBrainsMono,
                                                    fontWeight = FontWeight.Normal,
                                                    fontSize = 24.sp * scalar,
                                                    color = Color(0xFF000000)
                                                )
                                            }
                                        }
                                    }

                                    Step.CREDITS -> {

                                        Column(
                                            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                "kinematicsSimulatorV0300A\n\n\n" +
                                                        "Coded by Mark B\n\n" +
                                                        "Coded using Kotlin, KMP and MD3\n\n" +
                                                        "[ Kotlin and KMP by JetBrains s.r.o. ]\n" +
                                                        "[ Material Design 3 by Google LLC ]",
                                                fontFamily = JetBrainsMono,
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 24.sp * scalar,
                                                lineHeight = 28.sp * scalar,
                                                textAlign = TextAlign.Center,
                                                color = mainColourScheme.onSurface
                                            )
                                        }
                                    }

                                    Step.HISTORY -> {

                                        listOfRecords = records.pullAllSimulationBasicInfos()

                                        Column(
                                            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                "History",
                                                style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 40.sp * scalar,
                                                color = mainColourScheme.primary,
                                                textAlign = TextAlign.Center
                                            )

                                            Spacer(modifier = Modifier.height(24.dp))

                                            Column(
                                                modifier = Modifier.fillMaxWidth().fillMaxHeight().verticalScroll(scrollState)
                                            ) {
                                                if (listOfRecords != null) {
                                                    for (record in listOfRecords) {
                                                        val index = record[0]
                                                        val dimension = record[1]
                                                        val complexity = record[2]
                                                        val timestamp = record[3]

                                                        Button(
                                                            onClick = {
                                                                historyIndexSelected = index.toInt()
                                                                onStepChange(Step.SELECTEDHISTORY)
                                                            },
                                                            modifier = Modifier.fillMaxWidth(),
                                                            colors = ButtonDefaults.buttonColors(
                                                                containerColor = mainColourScheme.tertiary,
                                                                contentColor = mainColourScheme.onSurface
                                                            ),
                                                            shape = RoundedCornerShape(12.dp * scalar)
                                                        ) {
                                                            Box(
                                                                modifier = Modifier.fillMaxWidth()
                                                                    .padding(16.dp * scalar)
                                                                    .padding(horizontal = 16.dp * scalar)
                                                            ) {
                                                                Row(
                                                                    modifier = Modifier.fillMaxWidth(),
                                                                    verticalAlignment = Alignment.CenterVertically
                                                                ) {
                                                                    Text(
                                                                        "#$index",
                                                                        style = MaterialTheme.typography.titleLarge.copy(
                                                                            fontFamily = JetBrainsMono
                                                                        ),
                                                                        fontWeight = FontWeight.Bold,
                                                                        fontSize = 40.sp * scalar
                                                                    )
                                                                    Spacer(modifier = Modifier.width(28.dp * scalar))
                                                                    Column {
                                                                        Text(
                                                                            "${dimension}D  |  Complexity $complexity",
                                                                            style = MaterialTheme.typography.titleLarge.copy(
                                                                                fontFamily = JetBrainsMono
                                                                            ),
                                                                            fontWeight = FontWeight.Normal,
                                                                            fontSize = 24.sp * scalar
                                                                        )
                                                                        Spacer(modifier = Modifier.width(8.dp * scalar))
                                                                        Text(
                                                                            timestamp,
                                                                            style = MaterialTheme.typography.titleLarge.copy(
                                                                                fontFamily = JetBrainsMono
                                                                            ),
                                                                            fontWeight = FontWeight.Normal,
                                                                            fontSize = 24.sp * scalar
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        Spacer(modifier = Modifier.height(16.dp * scalar))
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Step.SETTINGS -> {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().fillMaxHeight()
                                        ) {
                                            Column(
                                                modifier = Modifier.fillMaxWidth().fillMaxHeight().weight(1.0f),
                                            ) {
                                                Card(
                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = mainColourScheme.tertiary
                                                    )
                                                ) {
                                                    Column(
                                                        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        Text(
                                                            "Themes",
                                                            fontFamily = JetBrainsMono,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 30.sp * scalar,
                                                            textAlign = TextAlign.Center,
                                                            modifier = Modifier.padding(8.dp * scalar),
                                                            color = mainColourScheme.onSurface
                                                        )

                                                        Column(
                                                            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                                                            horizontalAlignment = Alignment.CenterHorizontally,
                                                            verticalArrangement = Arrangement.Center
                                                        ) {
                                                            if (ThemeManager.currentTheme == "Dark Theme") {
                                                                Button(
                                                                    onClick = { ThemeManager.currentTheme = "Light Theme" },
                                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                                    colors = ButtonDefaults.buttonColors(
                                                                        containerColor = mainColourScheme.secondary
                                                                    )
                                                                ) {
                                                                    Text(
                                                                        "Light Theme",
                                                                        fontFamily = JetBrainsMono,
                                                                        fontWeight = FontWeight.Bold,
                                                                        fontSize = 24.sp * scalar,
                                                                        textAlign = TextAlign.Center,
                                                                        modifier = Modifier.padding(8.dp * scalar),
                                                                        color = mainColourScheme.onSurface
                                                                    )
                                                                }
                                                                Spacer(Modifier.height(24.dp * scalar))
                                                                Button(
                                                                    onClick = {},
                                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                                    colors = ButtonDefaults.buttonColors(
                                                                        containerColor = mainColourScheme.primary
                                                                    )
                                                                ) {
                                                                    Text(
                                                                        "Dark Theme",
                                                                        fontFamily = JetBrainsMono,
                                                                        fontWeight = FontWeight.Bold,
                                                                        fontSize = 24.sp * scalar,
                                                                        textAlign = TextAlign.Center,
                                                                        modifier = Modifier.padding(8.dp * scalar),
                                                                        color = mainColourScheme.onSurface
                                                                    )
                                                                }
                                                            } else if (ThemeManager.currentTheme == "Light Theme") {
                                                                Button(
                                                                    onClick = { },
                                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                                    colors = ButtonDefaults.buttonColors(
                                                                        containerColor = mainColourScheme.primary
                                                                    )
                                                                ) {
                                                                    Text(
                                                                        "Light Theme",
                                                                        fontFamily = JetBrainsMono,
                                                                        fontWeight = FontWeight.Bold,
                                                                        fontSize = 24.sp * scalar,
                                                                        textAlign = TextAlign.Center,
                                                                        modifier = Modifier.padding(8.dp * scalar),
                                                                        color = mainColourScheme.onSurface
                                                                    )
                                                                }
                                                                Spacer(Modifier.height(24.dp * scalar))
                                                                Button(
                                                                    onClick = { ThemeManager.currentTheme = "Dark Theme" },
                                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                                    colors = ButtonDefaults.buttonColors(
                                                                        containerColor = mainColourScheme.secondary
                                                                    )
                                                                ) {
                                                                    Text(
                                                                        "Dark Theme",
                                                                        fontFamily = JetBrainsMono,
                                                                        fontWeight = FontWeight.Bold,
                                                                        fontSize = 24.sp * scalar,
                                                                        textAlign = TextAlign.Center,
                                                                        modifier = Modifier.padding(8.dp * scalar),
                                                                        color = mainColourScheme.onSurface
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(32.dp*scalar))
                                            Column(
                                                modifier = Modifier.fillMaxWidth().fillMaxHeight().weight(1.0f),
                                            ) {
                                                Card(
                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = mainColourScheme.tertiary
                                                    )
                                                ) {
                                                    Column(
                                                        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        Text(
                                                            "Timing",
                                                            fontFamily = JetBrainsMono,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 30.sp * scalar,
                                                            textAlign = TextAlign.Center,
                                                            modifier = Modifier.padding(8.dp * scalar),
                                                            color = mainColourScheme.onSurface
                                                        )

                                                        Column(
                                                            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                                                            horizontalAlignment = Alignment.CenterHorizontally,
                                                            verticalArrangement = Arrangement.Center
                                                        ) {
                                                            if (Settings.currentSimulationTiming == "Real-Time") {
                                                                Button(
                                                                    onClick = { Settings.currentSimulationTiming = "Clock-Time" },
                                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                                    colors = ButtonDefaults.buttonColors(
                                                                        containerColor = mainColourScheme.secondary
                                                                    )
                                                                ) {
                                                                    Text(
                                                                        "Clock-Time",
                                                                        fontFamily = JetBrainsMono,
                                                                        fontWeight = FontWeight.Bold,
                                                                        fontSize = 24.sp * scalar,
                                                                        textAlign = TextAlign.Center,
                                                                        modifier = Modifier.padding(8.dp * scalar),
                                                                        color = mainColourScheme.onSurface
                                                                    )
                                                                }
                                                                Spacer(Modifier.height(24.dp * scalar))
                                                                Button(
                                                                    onClick = {},
                                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                                    colors = ButtonDefaults.buttonColors(
                                                                        containerColor = mainColourScheme.primary
                                                                    )
                                                                ) {
                                                                    Text(
                                                                        "Real-Time",
                                                                        fontFamily = JetBrainsMono,
                                                                        fontWeight = FontWeight.Bold,
                                                                        fontSize = 24.sp * scalar,
                                                                        textAlign = TextAlign.Center,
                                                                        modifier = Modifier.padding(8.dp * scalar),
                                                                        color = mainColourScheme.onSurface
                                                                    )
                                                                }
                                                                Spacer(Modifier.height(24.dp * scalar))
                                                                OutlinedTextField(
                                                                    value = Settings.hertz,
                                                                    onValueChange = { Settings.hertz = it },
                                                                    label = { Text(
                                                                        text = "Hertz",
                                                                        style = MaterialTheme.typography.titleLarge.copy(
                                                                            fontFamily = JetBrainsMono
                                                                        ),
                                                                        fontWeight = FontWeight.Light,
                                                                        fontSize = 16.sp * scalar
                                                                    ) },
                                                                    modifier = Modifier.fillMaxWidth(0.6f),
                                                                    textStyle = TextStyle(
                                                                        fontFamily = JetBrainsMono,
                                                                        fontSize = 20.sp,
                                                                        color = mainColourScheme.secondary,
                                                                    ),
                                                                    enabled = false
                                                                )
                                                            } else if (Settings.currentSimulationTiming == "Clock-Time") {
                                                                Button(
                                                                    onClick = { },
                                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                                    colors = ButtonDefaults.buttonColors(
                                                                        containerColor = mainColourScheme.primary
                                                                    )
                                                                ) {
                                                                    Text(
                                                                        "Clock-Time",
                                                                        fontFamily = JetBrainsMono,
                                                                        fontWeight = FontWeight.Bold,
                                                                        fontSize = 24.sp * scalar,
                                                                        textAlign = TextAlign.Center,
                                                                        modifier = Modifier.padding(8.dp * scalar),
                                                                        color = mainColourScheme.onSurface
                                                                    )
                                                                }
                                                                Spacer(Modifier.height(24.dp * scalar))
                                                                Button(
                                                                    onClick = { Settings.currentSimulationTiming = "Real-Time" },
                                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                                    colors = ButtonDefaults.buttonColors(
                                                                        containerColor = mainColourScheme.secondary
                                                                    )
                                                                ) {
                                                                    Text(
                                                                        "Real-Time",
                                                                        fontFamily = JetBrainsMono,
                                                                        fontWeight = FontWeight.Bold,
                                                                        fontSize = 24.sp * scalar,
                                                                        textAlign = TextAlign.Center,
                                                                        modifier = Modifier.padding(8.dp * scalar),
                                                                        color = mainColourScheme.onSurface
                                                                    )
                                                                }
                                                                Spacer(Modifier.height(24.dp * scalar))
                                                                OutlinedTextField(
                                                                    value = Settings.hertz,
                                                                    onValueChange = { Settings.hertz = it },
                                                                    label = { Text(
                                                                        text = "Hertz",
                                                                        style = MaterialTheme.typography.titleLarge.copy(
                                                                            fontFamily = JetBrainsMono
                                                                        ),
                                                                        fontWeight = FontWeight.Light,
                                                                        fontSize = 16.sp * scalar
                                                                    ) },
                                                                    modifier = Modifier.fillMaxWidth(0.6f),
                                                                    textStyle = TextStyle(
                                                                        fontFamily = JetBrainsMono,
                                                                        fontSize = 20.sp,
                                                                        color = mainColourScheme.primary
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(32.dp*scalar))
                                            Column(
                                                modifier = Modifier.fillMaxWidth().fillMaxHeight().weight(1.0f),
                                            ) {
                                                Card(
                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = mainColourScheme.tertiary
                                                    )
                                                ) {
                                                    Column(
                                                        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        Text(
                                                            "Language",
                                                            fontFamily = JetBrainsMono,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 30.sp * scalar,
                                                            textAlign = TextAlign.Center,
                                                            modifier = Modifier.padding(8.dp * scalar),
                                                            color = mainColourScheme.onSurface
                                                        )

                                                        Column(
                                                            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                                                            horizontalAlignment = Alignment.CenterHorizontally,
                                                            verticalArrangement = Arrangement.Center
                                                        ) {
                                                            if (LanguageManager.currentLanguage == "Italiano") {
                                                                Button(
                                                                    onClick = { LanguageManager.currentLanguage = "English" },
                                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                                    colors = ButtonDefaults.buttonColors(
                                                                        containerColor = mainColourScheme.secondary
                                                                    )
                                                                ) {
                                                                    Text(
                                                                        "English",
                                                                        fontFamily = JetBrainsMono,
                                                                        fontWeight = FontWeight.Bold,
                                                                        fontSize = 24.sp * scalar,
                                                                        textAlign = TextAlign.Center,
                                                                        modifier = Modifier.padding(8.dp * scalar),
                                                                        color = mainColourScheme.onSurface
                                                                    )
                                                                }
                                                                Spacer(Modifier.height(24.dp * scalar))
                                                                Button(
                                                                    onClick = {},
                                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                                    colors = ButtonDefaults.buttonColors(
                                                                        containerColor = mainColourScheme.primary
                                                                    )
                                                                ) {
                                                                    Text(
                                                                        "Italiano",
                                                                        fontFamily = JetBrainsMono,
                                                                        fontWeight = FontWeight.Bold,
                                                                        fontSize = 24.sp * scalar,
                                                                        textAlign = TextAlign.Center,
                                                                        modifier = Modifier.padding(8.dp * scalar),
                                                                        color = mainColourScheme.onSurface
                                                                    )
                                                                }
                                                            } else if (LanguageManager.currentLanguage == "English") {
                                                                Button(
                                                                    onClick = { },
                                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                                    colors = ButtonDefaults.buttonColors(
                                                                        containerColor = mainColourScheme.primary
                                                                    )
                                                                ) {
                                                                    Text(
                                                                        "English",
                                                                        fontFamily = JetBrainsMono,
                                                                        fontWeight = FontWeight.Bold,
                                                                        fontSize = 24.sp * scalar,
                                                                        textAlign = TextAlign.Center,
                                                                        modifier = Modifier.padding(8.dp * scalar),
                                                                        color = mainColourScheme.onSurface
                                                                    )
                                                                }
                                                                Spacer(Modifier.height(24.dp * scalar))
                                                                Button(
                                                                    onClick = { LanguageManager.currentLanguage = "Italiano" },
                                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                                    colors = ButtonDefaults.buttonColors(
                                                                        containerColor = mainColourScheme.secondary
                                                                    )
                                                                ) {
                                                                    Text(
                                                                        "Italiano",
                                                                        fontFamily = JetBrainsMono,
                                                                        fontWeight = FontWeight.Bold,
                                                                        fontSize = 24.sp * scalar,
                                                                        textAlign = TextAlign.Center,
                                                                        modifier = Modifier.padding(8.dp * scalar),
                                                                        color = mainColourScheme.onSurface
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Step.SELECTEDHISTORY -> {

                                        listOfRecords = records.pullAllSimulationBasicInfos()

                                        val record = listOfRecords?.get(historyIndexSelected-1)

                                        val index = record?.get(0)
                                        val dimension = record?.get(1)
                                        val complexity = record?.get(2)
                                        val timestamp = record?.get(3)

                                        val newHistory = records.convertCSVIntoHistory(index?.toInt() ?: -1)

                                        Column(
                                            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                                        ) {
                                            Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = mainColourScheme.tertiary,
                                                    contentColor = Color.White
                                                ),
                                                shape = RoundedCornerShape(12.dp * scalar)
                                            ) {
                                                Box(
                                                    modifier = Modifier.fillMaxWidth()
                                                        .padding(16.dp * scalar)
                                                        .padding(horizontal = 16.dp * scalar)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            "#$index",
                                                            style = MaterialTheme.typography.titleLarge.copy(
                                                                fontFamily = JetBrainsMono
                                                            ),
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 40.sp * scalar
                                                        )
                                                        Spacer(modifier = Modifier.width(28.dp * scalar))
                                                        Column {
                                                            Text(
                                                                "${dimension}D  |  Complexity $complexity",
                                                                style = MaterialTheme.typography.titleLarge.copy(
                                                                    fontFamily = JetBrainsMono
                                                                ),
                                                                fontWeight = FontWeight.Normal,
                                                                fontSize = 24.sp * scalar
                                                            )
                                                            Spacer(modifier = Modifier.width(8.dp * scalar))
                                                            Text(
                                                                "$timestamp",
                                                                style = MaterialTheme.typography.titleLarge.copy(
                                                                    fontFamily = JetBrainsMono
                                                                ),
                                                                fontWeight = FontWeight.Normal,
                                                                fontSize = 24.sp * scalar
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            Spacer(Modifier.height(24.dp * scalar))
                                            Column(
                                                modifier = Modifier.fillMaxWidth().weight(1.0f).verticalScroll(scrollState),
                                            ) {
                                                val names = records.pullNamesOfParameters(index?.toInt() ?: -1)
                                                val initialValues = records.pullInitialSimulationParameters(index?.toInt() ?: -1)
                                                val numberOfParameters = names?.size ?: 0
                                                val combinedLists = mutableListOf<List<String>>()

                                                for (i in 0..<numberOfParameters) {
                                                    val listToBeAdded = listOf(names?.get(i).toString(),
                                                        initialValues?.get(i).toString()
                                                    )
                                                    combinedLists.add(listToBeAdded)
                                                }

                                                for (list in combinedLists) {
                                                    Text(
                                                        "${list[0]}: ${list[1]}",
                                                        fontFamily = JetBrainsMono,
                                                        fontWeight = FontWeight.Normal,
                                                        fontSize = 25.sp * scalar,
                                                        lineHeight = 30.sp * scalar
                                                    )
                                                }
                                            }
                                            Spacer(Modifier.height(24.dp))
                                            Column(
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Button(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                    onClick = {
                                                        when (dimension?.toInt()) {
                                                            1 if complexity?.toInt() == 1 -> {
                                                                newHistory.generateGraph(
                                                                    "Displacement / Time",
                                                                    "Time",
                                                                    "Displacement",
                                                                    newHistory.pullVariable(0),
                                                                    newHistory.pullVariable(1)
                                                                )
                                                            }

                                                            1 if complexity?.toInt() == 2 -> {
                                                                newHistory.generateGraph(
                                                                    "Displacement / Time",
                                                                    "Time",
                                                                    "Displacement",
                                                                    newHistory.pullVariable(0),
                                                                    newHistory.pullVariable(1)
                                                                )
                                                            }

                                                            2 if complexity?.toInt() == 1 -> {
                                                                newHistory.generateGraph(
                                                                    "Position Y / Position X",
                                                                    "Position X",
                                                                    "Position Y",
                                                                    newHistory.pullVariable(1),
                                                                    newHistory.pullVariable(2)
                                                                )
                                                            }
                                                        }
                                                    }
                                                ) {
                                                    Text(
                                                        "Generate Graph",
                                                        fontFamily = JetBrainsMono,
                                                        fontWeight = FontWeight.Normal,
                                                        fontSize = 30.sp * scalar
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }

    }
}