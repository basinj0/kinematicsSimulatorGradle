
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.CheckboxDefaults.colors
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

enum class Step { TITLE, DIMENSION, COMPLEXITY, EXTRAPARAMETERS, INPUTS, RUNNING, RESULTS }

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
fun KinematicsUI() {
    CompositionLocalProvider(
        LocalTextStyle provides LocalTextStyle.current.copy(
            fontFamily = JetBrainsMono
        )
    ) {
        var currentStep by remember { mutableStateOf(Step.TITLE) }
        val focusRequester = remember { FocusRequester() }

        val logic = Logic()

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        MaterialTheme(
            colorScheme = CustomDarkColorScheme
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
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

                var createdObject: Body = SimpleBody1D(0.0, 0.0, 0.0)

                var latestHistory : DataCollector? = null

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
                                containerColor = CustomDarkColorScheme.surface,
                                contentColor = CustomDarkColorScheme.onSurface
                            )
                        ) {

                            Column(
                                modifier = Modifier.padding(32.dp * scalar).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                when (currentStep) {
                                    Step.TITLE -> {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                                            Button(
                                                onClick = { currentStep = Step.DIMENSION },
                                                shape = RoundedCornerShape(8.dp * scalar),
                                                modifier = Modifier.padding(horizontal = 4.dp * scalar),
                                            ) {
                                                Text(
                                                    text = "Start",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = (50 * scalar).sp,
                                                    textAlign = TextAlign.Center,
                                                    fontFamily = JetBrainsMono,
                                                    modifier = Modifier.padding(horizontal = 16.dp * scalar).padding(16.dp * scalar)
                                                )
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
                                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Button(
                                                    onClick = { chosenDimension = 1; currentStep = Step.COMPLEXITY },
                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                    modifier = Modifier.padding(horizontal = 8.dp * scalar)
                                                ) {
                                                    Text(
                                                        text = "1D",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 36.sp * scalar,
                                                        textAlign = TextAlign.Center,
                                                        fontFamily = JetBrainsMono,
                                                        modifier = Modifier.padding(horizontal = 8.dp * scalar).padding(4.dp * scalar)
                                                    )
                                                }
                                                Button(
                                                    onClick = { chosenDimension = 2; currentStep = Step.COMPLEXITY },
                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                    modifier = Modifier.padding(horizontal = 8.dp * scalar)
                                                ) {
                                                    Text(
                                                        text = "2D",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 36.sp * scalar,
                                                        textAlign = TextAlign.Center,
                                                        fontFamily = JetBrainsMono,
                                                        modifier = Modifier.padding(horizontal = 8.dp * scalar).padding(4.dp * scalar)
                                                    )
                                                }
                                                Button(
                                                    onClick = { TODO("Implement in Logic before enabling") },
                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                    modifier = Modifier.padding(horizontal = 8.dp * scalar),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = CustomDarkColorScheme.secondary,
                                                    )
                                                ) {
                                                    Text(
                                                        text = "--",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 36.sp * scalar,
                                                        textAlign = TextAlign.Center,
                                                        fontFamily = JetBrainsMono,
                                                        modifier = Modifier.padding(horizontal = 8.dp * scalar).padding(4.dp * scalar)
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
                                        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Button(
                                                    onClick = {
                                                        chosenComplexity = 1; currentStep = if (chosenDimension == 1) {
                                                        Step.INPUTS
                                                    } else {
                                                        Step.EXTRAPARAMETERS
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
                                                        modifier = Modifier.padding(horizontal = 4.dp * scalar).padding(4.dp * scalar)
                                                    )
                                                }
                                                Button(
                                                    onClick = {
                                                        chosenComplexity = 2; currentStep = if (chosenDimension == 1) {
                                                        Step.INPUTS
                                                    } else {
                                                        Step.EXTRAPARAMETERS
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
                                                        modifier = Modifier.padding(horizontal = 4.dp * scalar).padding(4.dp * scalar)
                                                    )
                                                }
                                                Button(
                                                    onClick = { TODO("Implement in Logic before enabling") },
                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                    modifier = Modifier.padding(horizontal = 4.dp * scalar),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = CustomDarkColorScheme.secondary,
                                                    )
                                                ) {
                                                    Text(
                                                        text = "Complex",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 30.sp * scalar,
                                                        textAlign = TextAlign.Center,
                                                        fontFamily = JetBrainsMono,
                                                        modifier = Modifier.padding(horizontal = 4.dp * scalar).padding(4.dp * scalar)
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
                                                    onClick = { chosenInputType = "Polar"; currentStep = Step.INPUTS },
                                                    shape = RoundedCornerShape(8.dp * scalar),
                                                    modifier = Modifier.padding(horizontal = 4.dp * scalar)
                                                ) {
                                                    Text(
                                                        text = "Polar",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 30.sp * scalar,
                                                        textAlign = TextAlign.Center,
                                                        fontFamily = JetBrainsMono,
                                                        modifier = Modifier.padding(horizontal = 4.dp * scalar).padding(4.dp * scalar)
                                                    )
                                                }
                                                Button(
                                                    onClick = {
                                                        chosenInputType = "Cartesian"; currentStep = Step.INPUTS
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
                                                        modifier = Modifier.padding(horizontal = 4.dp * scalar).padding(4.dp * scalar)
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
                                                            color = CustomDarkColorScheme.primary
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
                                                            color = CustomDarkColorScheme.primary
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
                                                            color = CustomDarkColorScheme.primary
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
                                                            color = CustomDarkColorScheme.primary
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
                                                            currentStep = Step.RUNNING
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        shape = RoundedCornerShape(8.dp * scalar),
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = CustomDarkColorScheme.primary,
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
                                                            color = CustomDarkColorScheme.primary
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
                                                            color = CustomDarkColorScheme.primary
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
                                                            color = CustomDarkColorScheme.primary
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
                                                            color = CustomDarkColorScheme.primary
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
                                                            color = CustomDarkColorScheme.primary
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
                                                            currentStep = Step.RUNNING
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        shape = RoundedCornerShape(8.dp * scalar),
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = CustomDarkColorScheme.primary,
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
                                                            color = CustomDarkColorScheme.primary
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
                                                            color = CustomDarkColorScheme.primary
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
                                                            color = CustomDarkColorScheme.primary
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
                                                            color = CustomDarkColorScheme.primary
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
                                                            color = CustomDarkColorScheme.primary
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
                                                            color = CustomDarkColorScheme.primary
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

                                                            currentStep = Step.RUNNING
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        shape = RoundedCornerShape(8.dp * scalar),
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = CustomDarkColorScheme.primary,
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
                                                            color = CustomDarkColorScheme.primary
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
                                                            color = CustomDarkColorScheme.primary
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
                                                            color = CustomDarkColorScheme.primary
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
                                                            color = CustomDarkColorScheme.primary
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
                                                            color = CustomDarkColorScheme.primary
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
                                                            color = CustomDarkColorScheme.primary
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
                                                            currentStep = Step.RUNNING
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        shape = RoundedCornerShape(8.dp * scalar),
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = CustomDarkColorScheme.primary,
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
                                                        color = CustomDarkColorScheme.primary,
                                                        textAlign = TextAlign.Left
                                                    )
                                                    Spacer(modifier = Modifier.height(16.dp))

                                                    Column(
                                                        modifier = Modifier.fillMaxWidth().weight(1.0f).verticalScroll(scrollState),
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
                                                            onClick = { currentStep = Step.RESULTS },
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
                                                        color = CustomDarkColorScheme.primary
                                                    )
                                                    Spacer(modifier = Modifier.height(24.dp))

                                                    Column(
                                                        modifier = Modifier.fillMaxWidth().weight(1.0f).verticalScroll(scrollState),
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
                                                            onClick = { currentStep = Step.RESULTS },
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
                                                        color = CustomDarkColorScheme.primary
                                                    )
                                                    Spacer(modifier = Modifier.height(24.dp))

                                                    Column(
                                                        modifier = Modifier.fillMaxWidth().weight(1.0f).verticalScroll(scrollState),
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
                                                            "X Velocity: ${String.format("%.3f", liveXVelocity)} m/s",
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 20.sp * scalar
                                                        )
                                                        Spacer(modifier = Modifier.height(12.dp * scalar))
                                                        Text(
                                                            "Y Velocity: ${String.format("%.3f", liveYVelocity)} m/s",
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
                                                            onClick = { currentStep = Step.RESULTS },
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
                                                color = CustomDarkColorScheme.primary
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
                                                                latestHistory?.pullVariable(0) ?: null,
                                                                latestHistory?.pullVariable(1) ?: null
                                                            ) ?: error("Graph failed to generate.")
                                                        }

                                                        1 if chosenComplexity == 2 -> {
                                                            latestHistory?.generateGraph(
                                                                "Displacement / Time",
                                                                "Time",
                                                                "Displacement",
                                                                latestHistory?.pullVariable(0) ?: null,
                                                                latestHistory?.pullVariable(1) ?: null
                                                            ) ?: error("Graph failed to generate.")
                                                        }

                                                        2 if chosenComplexity == 1 -> {
                                                            latestHistory?.generateGraph(
                                                                "Position Y / Position X",
                                                                "Position X",
                                                                "Position Y",
                                                                latestHistory?.pullVariable(1) ?: null,
                                                                latestHistory?.pullVariable(2) ?: null
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
}