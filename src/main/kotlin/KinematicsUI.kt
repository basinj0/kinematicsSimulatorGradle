
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.*
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
import androidx.compose.ui.modifier.*

enum class Step { TITLE, DIMENSION, COMPLEXITY, EXTRAPARAMETERS, INPUTS, RUNNING }
@Composable
fun KinematicsUI() {
    CompositionLocalProvider(
        LocalTextStyle provides LocalTextStyle.current.copy(
            fontFamily = JetBrainsMono // Or your custom FontFamily(...) object
        )
    ) {
        var currentStep by remember { mutableStateOf(Step.TITLE) }
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        // 1. Wrap in MaterialTheme with dark colors for a clean technical look
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

                var initialDisplacementInput by remember { mutableStateOf("0.0") }
                var initialVelocityInput by remember { mutableStateOf("0.0") }
                var initialAccelerationInput by remember { mutableStateOf("0.0") }
                var durationOfSimulationInput by remember { mutableStateOf("0.0") }
                var initialJerkInput by remember { mutableStateOf("0.0") }

                var initialMagnitudeInput by remember { mutableStateOf("0.0") }
                var initialAngleInput by remember { mutableStateOf("0.0") }
                var initialXVelocityInput by remember { mutableStateOf("0.0") }
                var initialYVelocityInput by remember { mutableStateOf("0.0") }
                var initialXPositionInput by remember { mutableStateOf("0.0") }
                var initialYPositionInput by remember { mutableStateOf("0.0") }
                var initialGravityInput by remember { mutableStateOf("0.0") }

                val scrollState = rememberScrollState()

                // 2. Center everything on the screen instead of top-left corner
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // App Header Title
                    Text(
                        text = "Kinematics Simulator",
                        style = MaterialTheme.typography.headlineLarge.copy(fontFamily = JetBrainsMono),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 40.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // 3. Put your steps inside a Card container so it looks like a clean panel
                    Card(
                        modifier = Modifier.width(600.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = CustomDarkColorScheme.surface,
                            contentColor = CustomDarkColorScheme.onSurface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            when (currentStep) {
                                Step.TITLE -> {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Button(
                                            onClick = { currentStep = Step.DIMENSION },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.padding(horizontal = 4.dp),
                                        ) {
                                            Text(
                                                text = "Start",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 24.sp,
                                                textAlign = TextAlign.Center,
                                                fontFamily = JetBrainsMono,
                                                modifier = Modifier.padding(horizontal = 8.dp)
                                            )
                                        }
                                    }
                                }

                                Step.DIMENSION -> {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            "Select Number of Dimensions",
                                            style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 30.sp
                                        )
                                        Spacer(modifier = Modifier.height(20.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Button(
                                                onClick = { chosenDimension = 1; currentStep = Step.COMPLEXITY },
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            ) {
                                                Text(
                                                    text = "1D",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 24.sp,
                                                    textAlign = TextAlign.Center,
                                                    fontFamily = JetBrainsMono
                                                )
                                            }
                                            Button(
                                                onClick = { chosenDimension = 2; currentStep = Step.COMPLEXITY },
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            ) {
                                                Text(
                                                    text = "2D",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 24.sp,
                                                    textAlign = TextAlign.Center,
                                                    fontFamily = JetBrainsMono
                                                )
                                            }
                                            Button(
                                                onClick = { chosenDimension = 3; currentStep = Step.COMPLEXITY },
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.padding(horizontal = 4.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = CustomDarkColorScheme.secondary,
                                                )
                                            ) {
                                                Text(
                                                    text = "--",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 24.sp,
                                                    textAlign = TextAlign.Center,
                                                    fontFamily = JetBrainsMono
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
                                        fontSize = 30.sp
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Button(
                                            onClick = { chosenComplexity = 1; if (chosenDimension == 1) { currentStep = Step.INPUTS } else { currentStep = Step.EXTRAPARAMETERS } },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        ) {
                                            Text(
                                                text = "Simple",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 20.sp,
                                                textAlign = TextAlign.Center,
                                                fontFamily = JetBrainsMono
                                            )
                                        }
                                        Button(
                                            onClick = { chosenComplexity = 2; if (chosenDimension == 1) { currentStep = Step.INPUTS } else { currentStep = Step.EXTRAPARAMETERS } },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        ) {
                                            Text(
                                                text = "Intermediate",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 20.sp,
                                                textAlign = TextAlign.Center,
                                                fontFamily = JetBrainsMono
                                            )
                                        }
                                        Button(
                                            onClick = { chosenComplexity = 3; if (chosenDimension == 1) { currentStep = Step.INPUTS } else { currentStep = Step.EXTRAPARAMETERS } },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.padding(horizontal = 4.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = CustomDarkColorScheme.secondary,
                                            )
                                        ) {
                                            Text(
                                                text = "Complex",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 20.sp,
                                                textAlign = TextAlign.Center,
                                                fontFamily = JetBrainsMono
                                            )
                                        }
                                    }
                                }

                                Step.EXTRAPARAMETERS -> {
                                    Text(
                                        "Select Input Method",
                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 30.sp
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Button(
                                            onClick = { chosenInputType = "Polar"; currentStep = Step.INPUTS },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        ) {
                                            Text(
                                                text = "Polar",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 20.sp,
                                                textAlign = TextAlign.Center,
                                                fontFamily = JetBrainsMono
                                            )
                                        }
                                        Button(
                                            onClick = { chosenInputType = "Cartesian"; currentStep = Step.INPUTS },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        ) {
                                            Text(
                                                text = "Cartesian",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 20.sp,
                                                textAlign = TextAlign.Center,
                                                fontFamily = JetBrainsMono
                                            )
                                        }
                                    }
                                }

                                Step.INPUTS -> {
                                    if (chosenDimension == 1 && chosenComplexity == 1) {
                                        Text(
                                            "Enter Initial Parameters",
                                            style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 30.sp
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
                                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                        fontWeight = FontWeight.Light,
                                                        fontSize = 12.sp
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            OutlinedTextField(
                                                value = initialVelocityInput,
                                                onValueChange = { initialVelocityInput = it },
                                                label = {
                                                    Text(
                                                        "Initial Velocity (m/s)",
                                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                        fontWeight = FontWeight.Light,
                                                        fontSize = 12.sp
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            OutlinedTextField(
                                                value = initialAccelerationInput,
                                                onValueChange = { initialAccelerationInput = it },
                                                label = {
                                                    Text(
                                                        "Initial Acceleration (m/s^2)",
                                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                        fontWeight = FontWeight.Light,
                                                        fontSize = 12.sp
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            OutlinedTextField(
                                                value = durationOfSimulationInput,
                                                onValueChange = { durationOfSimulationInput = it },
                                                label = {
                                                    Text(
                                                        "Duration of Simulation (s)",
                                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                        fontWeight = FontWeight.Light,
                                                        fontSize = 12.sp
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Spacer(modifier = Modifier.height(24.dp))

                                            Button(
                                                onClick = { currentStep = Step.RUNNING },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = CustomDarkColorScheme.primary,
                                                )
                                            ) {
                                                Text(
                                                    "Run Simulation",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 20.sp,
                                                    textAlign = TextAlign.Center,
                                                    fontFamily = JetBrainsMono
                                                )
                                            }
                                        }
                                    } else if (chosenDimension == 1 && chosenComplexity == 2) {
                                        Text(
                                            "Enter Initial Parameters",
                                            style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 30.sp
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
                                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                        fontWeight = FontWeight.Light,
                                                        fontSize = 12.sp
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            OutlinedTextField(
                                                value = initialVelocityInput,
                                                onValueChange = { initialVelocityInput = it },
                                                label = {
                                                    Text(
                                                        "Initial Velocity (m/s)",
                                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                        fontWeight = FontWeight.Light,
                                                        fontSize = 12.sp
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            OutlinedTextField(
                                                value = initialAccelerationInput,
                                                onValueChange = { initialAccelerationInput = it },
                                                label = {
                                                    Text(
                                                        "Initial Acceleration (m/s^2)",
                                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                        fontWeight = FontWeight.Light,
                                                        fontSize = 12.sp
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            OutlinedTextField(
                                                value = initialJerkInput,
                                                onValueChange = { initialJerkInput = it },
                                                label = {
                                                    Text(
                                                        "Initial Jerk (m/s^3)",
                                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                        fontWeight = FontWeight.Light,
                                                        fontSize = 12.sp
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            OutlinedTextField(
                                                value = durationOfSimulationInput,
                                                onValueChange = { durationOfSimulationInput = it },
                                                label = {
                                                    Text(
                                                        "Duration of Simulation (s)",
                                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                        fontWeight = FontWeight.Light,
                                                        fontSize = 12.sp
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Spacer(modifier = Modifier.height(24.dp))

                                            Button(
                                                onClick = { currentStep = Step.RUNNING },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = CustomDarkColorScheme.primary,
                                                )
                                            ) {
                                                Text(
                                                    "Run Simulation",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 20.sp,
                                                    textAlign = TextAlign.Center,
                                                    fontFamily = JetBrainsMono
                                                )
                                            }
                                        }
                                    } else if (chosenDimension == 2 && chosenComplexity == 1 && chosenInputType == "Polar") {
                                        Text(
                                            "Enter Initial Parameters",
                                            style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 30.sp
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
                                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                        fontWeight = FontWeight.Light,
                                                        fontSize = 12.sp
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            OutlinedTextField(
                                                value = initialMagnitudeInput,
                                                onValueChange = { initialMagnitudeInput = it },
                                                label = {
                                                    Text(
                                                        "Initial Magnitude (m/s)",
                                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                        fontWeight = FontWeight.Light,
                                                        fontSize = 12.sp
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            OutlinedTextField(
                                                value = initialGravityInput,
                                                onValueChange = { initialGravityInput = it },
                                                label = {
                                                    Text(
                                                        "Initial Gravity (m/s^2) [Negative is down]",
                                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                        fontWeight = FontWeight.Light,
                                                        fontSize = 12.sp
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            OutlinedTextField(
                                                value = initialAccelerationInput,
                                                onValueChange = { initialAccelerationInput = it },
                                                label = {
                                                    Text(
                                                        "Initial X Position (m)",
                                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                        fontWeight = FontWeight.Light,
                                                        fontSize = 12.sp
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            OutlinedTextField(
                                                value = initialYPositionInput,
                                                onValueChange = { initialYPositionInput = it },
                                                label = {
                                                    Text(
                                                        "Initial Y Position (m)",
                                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                        fontWeight = FontWeight.Light,
                                                        fontSize = 12.sp
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            OutlinedTextField(
                                                value = durationOfSimulationInput,
                                                onValueChange = { durationOfSimulationInput = it },
                                                label = {
                                                    Text(
                                                        "Duration of Simulation (s)",
                                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                        fontWeight = FontWeight.Light,
                                                        fontSize = 12.sp
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Spacer(modifier = Modifier.height(24.dp))

                                            Button(
                                                onClick = { currentStep = Step.RUNNING },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = CustomDarkColorScheme.primary,
                                                )
                                            ) {
                                                Text(
                                                    "Run Simulation",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 20.sp,
                                                    textAlign = TextAlign.Center,
                                                    fontFamily = JetBrainsMono
                                                )
                                            }
                                        }
                                    } else if (chosenDimension == 2 && chosenComplexity == 1 && chosenInputType == "Cartesian") {
                                        Text(
                                            "Enter Initial Parameters",
                                            style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 30.sp
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
                                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                        fontWeight = FontWeight.Light,
                                                        fontSize = 12.sp
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            OutlinedTextField(
                                                value = initialYVelocityInput,
                                                onValueChange = { initialYVelocityInput = it },
                                                label = {
                                                    Text(
                                                        "Initial Y Velocity (m/s)",
                                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                        fontWeight = FontWeight.Light,
                                                        fontSize = 12.sp
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            OutlinedTextField(
                                                value = initialGravityInput,
                                                onValueChange = { initialGravityInput = it },
                                                label = {
                                                    Text(
                                                        "Initial Gravity (m/s^2) [Negative is down]",
                                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                        fontWeight = FontWeight.Light,
                                                        fontSize = 12.sp
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            OutlinedTextField(
                                                value = initialXPositionInput,
                                                onValueChange = { initialXPositionInput = it },
                                                label = {
                                                    Text(
                                                        "Initial X Position (m)",
                                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                        fontWeight = FontWeight.Light,
                                                        fontSize = 12.sp
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            OutlinedTextField(
                                                value = initialYPositionInput,
                                                onValueChange = { initialYPositionInput = it },
                                                label = {
                                                    Text(
                                                        "Initial Y Position (m)",
                                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                        fontWeight = FontWeight.Light,
                                                        fontSize = 12.sp
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            OutlinedTextField(
                                                value = durationOfSimulationInput,
                                                onValueChange = { durationOfSimulationInput = it },
                                                label = {
                                                    Text(
                                                        "Duration of Simulation (s)",
                                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono),
                                                        fontWeight = FontWeight.Light,
                                                        fontSize = 12.sp
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Spacer(modifier = Modifier.height(24.dp))

                                            Button(
                                                onClick = { currentStep = Step.RUNNING },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = CustomDarkColorScheme.primary,
                                                )
                                            ) {
                                                Text(
                                                    "Run Simulation",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 20.sp,
                                                    textAlign = TextAlign.Center,
                                                    fontFamily = JetBrainsMono
                                                )
                                            }
                                        }
                                    }
                                }

                                Step.RUNNING -> {
                                    Text("Simulation Running...", style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("Dimensions: $chosenDimension-D  |  Complexity: Level $chosenComplexity")
                                    Text("Velocity: $initialVelocityInput m/s  |  Angle: ${initialAngleInput}°")

                                    Spacer(modifier = Modifier.height(24.dp))
                                    OutlinedButton(onClick = { currentStep = Step.DIMENSION }) {
                                        Text("Reset Simulator")
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