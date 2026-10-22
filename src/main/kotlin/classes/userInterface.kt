package classes

// Import other classes
import classes.body.interBody1D
import classes.body.simpleBody1D
import classes.body.simpleBody2D

import java.lang.Math.toRadians
import java.lang.Thread.sleep
import kotlin.text.toDoubleOrNull
import kotlin.time.TimeSource
import kotlin.math.round
import kotlin.system.exitProcess
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sqrt

class userInterface() {

    // Get initial parameters and start program
    fun start() {
        val dimension = getDimensionality()

        val complexity = getComplexity()

        simulationSetup(dimension, complexity)
    }

    // Used in getDoubleValue() to make sure input is valid
    fun safeReadLineDouble(): Double {
        while (true) {
            val line = readlnOrNull()

            if (line == null) {
                println("Input a value \n")
                continue
            }

            val output = line.toDoubleOrNull()

            if (output == null) {
                println("Input is invalid \n")
                continue
            }

            return output
        }
    }

    // To be used in all cases where a string input is needed
    fun safeReadLineString(): String {
        while (true) {
            val line = readlnOrNull()

            if (line == null) {
                println("Input a value \n")
                continue
            }

            return line
        }
    }

    // To be used in all cases where a double input is needed
    fun getDoubleValue(name: String): Double {
        println("Input the value of $name:")
        return safeReadLineDouble()
    }

    // Get initial parameter of dimensionality
    fun getDimensionality(): Int {
        while (true) {
            println("How many dimensions should the simulation utilize?   1 - 1D  |  2 - 2D  |  3 - 3D [ Unavailable ]")
            val input = readlnOrNull()
            when (input) {
                "1" -> return 1
                "2" -> return 2
                "3" -> return 3
                else -> println("Invalid input \n")
            }
        }
    }

    // Get initial parameter of complexity
    fun getComplexity(): Int {
        while (true) {
            println("How complex should the simulation be?   Simple  |  Intermediate [ Available only for 1D ]  |  Complex  [ Unavailable ]")
            val input = readlnOrNull()
            when (input) {
                "Simple" -> return 1
                "Intermediate" -> return 2
                "Complex" -> return 3
                else -> println("Invalid input \n")
            }
        }
    }

    // Used to convert a vector in Polar form [magnitude, direction] into a vector in Cartesian form [X, Y]
    fun convertPolarToCartesian(magnitude: Double, direction: Double): List<Double> {
        val initialXY = mutableListOf<Double>()

        val directionInRadians = toRadians(direction)

        val initialX = magnitude * (cos(directionInRadians))
        val initialY = magnitude * (sin(directionInRadians))

        initialXY.add(initialX)
        initialXY.add(initialY)
        return initialXY
    }

    // Takes in initial parameters [dimensionality, complexity], asks user for all needed parameters and creates a new object of class Body depending on initial parameters
    // Then runs correct function to start the simulation
    fun simulationSetup(dimension: Int, complexity: Int) {

        if (complexity == 1 && dimension == 1) {
            val initialVelocity = getDoubleValue("initial velocity")
            val acceleration = getDoubleValue("acceleration")
            val initialDisplacement = getDoubleValue("initial displacement")

            val objectInFocus = simpleBody1D(initialVelocity, acceleration, initialDisplacement)

            val time = getDoubleValue("the amount of time the simulation will last")

            val timeSource = TimeSource.Monotonic
            val lastTime = timeSource.markNow()

            simulation1DSimple(objectInFocus, time, lastTime)
        } else if (complexity == 2 && dimension == 1) {
            val initialVelocity = getDoubleValue("initial velocity")
            val initialAcceleration = getDoubleValue("initial acceleration")
            val initialDisplacement = getDoubleValue("initial displacement")
            val jerk = getDoubleValue("jerk")

            val objectInFocus = interBody1D(initialVelocity, initialAcceleration, initialDisplacement, jerk)

            val time = getDoubleValue("the amount of time the simulation will last")

            val timeSource = TimeSource.Monotonic
            val lastTime = timeSource.markNow()

            simulation1DInter(objectInFocus, time, lastTime)
        } else if (complexity == 3 && dimension == 1) {
            TODO("To be implemented")
        } else if (complexity == 1 && dimension == 2) {
            var initialVelocityX = 0.0
            var initialVelocityY = 0.0

            while (true) {
                println("How would you like to define velocity?")
                println("Polar  |  Cartesian")
                val input = safeReadLineString()

                if (input == "Polar") {
                    val initialMagnitude = getDoubleValue("initial magnitude")
                    val initialDirection = getDoubleValue("initial direction")

                    val initialXY = convertPolarToCartesian(initialMagnitude, initialDirection)

                    initialVelocityX = initialXY[0]
                    initialVelocityY = initialXY[1]
                    break
                } else if (input == "Cartesian") {
                    initialVelocityX = getDoubleValue("initial X velocity")
                    initialVelocityY = getDoubleValue("initial Y velocity")
                    break
                } else {
                    println("Invalid input")
                    continue
                }
            }

            val gravity = getDoubleValue("gravity")
            val initialPositionX = getDoubleValue("initial X position")
            val initialPositionY = getDoubleValue("initial Y position")

            val objectInFocus =
                simpleBody2D(initialVelocityX, initialVelocityY, gravity, initialPositionX, initialPositionY)

            val time = getDoubleValue("the amount of time the simulation will last")

            val timeSource = TimeSource.Monotonic
            val lastTime = timeSource.markNow()

            simulation2DSimple(objectInFocus, time, lastTime)
        }
    }

    // Simulator for Simple 1D
    fun simulation1DSimple(body: simpleBody1D, simulationDuration: Double, lastTime: TimeSource.Monotonic.ValueTimeMark) {
        val timeSource = TimeSource.Monotonic
        var lastTime = lastTime
        var simulationTime = 0.0
        val history = dataCollector()

        while (true) {
            val elapsed = lastTime.elapsedNow()
            lastTime = timeSource.markNow()

            val dt = elapsed.inWholeNanoseconds / 1_000_000_000.0

            simulationTime += dt

            val displacement = body.getDisplacement(simulationTime)
            val velocity = body.getCurrentVelocity(simulationTime)
            val shownTime = simulationTime
            val shownAcceleration = body.acceleration

            history.addToSimple1DHistory(simulationTime, displacement, velocity, shownAcceleration)

            print("\r Object 1  |  t: ${"%.2f".format(shownTime)}  |  s: ${"%.2f".format(displacement)}  |  v: ${"%.2f".format(velocity)} |  a: ${"%.2f".format(shownAcceleration)}")

            if (simulationTime >= simulationDuration) {
                break
            }

            sleep(5)
        }

        println("\n \n Simulation Over \n")
        history.exportHistory(1,1)
        println("")
        history.generateGraph("Displacement / Time", "Time", "Displacement", history.pullVariable(0), history.pullVariable(1))
        println("")
        endOfProgram()
    }

    // Simulator for Intermediate 1D
    fun simulation1DInter(body: interBody1D, simulationDuration: Double, lastTime: TimeSource.Monotonic.ValueTimeMark) {
        val timeSource = TimeSource.Monotonic
        var lastTime = lastTime
        var simulationTime = 0.0
        val history = dataCollector()

        while (true) {
            val elapsed = lastTime.elapsedNow()
            lastTime = timeSource.markNow()

            val dt = elapsed.inWholeNanoseconds / 1_000_000_000.0

            simulationTime += dt

            val displacement = body.getDisplacement(simulationTime)
            val velocity = body.getCurrentVelocity(simulationTime)
            val shownTime = simulationTime
            val shownAcceleration = body.getCurrentAcceleration(simulationTime)
            val shownJerk = body.jerk

            history.addToInter1DHistory(simulationTime, displacement, velocity, shownAcceleration, shownJerk)

            print("\r Object 1  |  t: ${"%.2f".format(shownTime)}  |  s: ${"%.2f".format(displacement)}  |  v: ${"%.2f".format(velocity)}  |  a: ${"%.2f".format(shownAcceleration)}  |  j: ${"%.2f".format(shownJerk)}")

            if (simulationTime >= simulationDuration) {
                break
            }

            sleep(5)
        }

        println("\n \n Simulation Over \n")
        history.exportHistory(1,2)
        println("")
        history.generateGraph("Displacement / Time", "Time", "Displacement", history.pullVariable(0), history.pullVariable(1))
        println("")
        endOfProgram()
    }

    // Simulator for Simple 2D
    fun simulation2DSimple(body: simpleBody2D, simulationDuration: Double, lastTime: TimeSource.Monotonic.ValueTimeMark) {
        val timeSource = TimeSource.Monotonic
        var lastTime = lastTime
        var simulationTime = 0.0
        val history = dataCollector()

        while (true) {
            val elapsed = lastTime.elapsedNow()
            lastTime = timeSource.markNow()

            val dt = elapsed.inWholeNanoseconds / 1_000_000_000.0

            simulationTime += dt

            val positionX = body.getCurrentPositionX(simulationTime)
            val positionY = body.getCurrentPositionY(simulationTime)
            val velocity = body.getCurrentCombinedVelocity(simulationTime)
            val velocityX = body.getCurrentVelocityX(simulationTime)
            val velocityY = body.getCurrentVelocityY(simulationTime)
            val time = simulationTime
            val gravity = body.gravity

            history.addToSimple2DHistory(simulationTime, positionX, positionY, velocity, velocityX, velocityY, gravity)

            print("\r Object 1  |  t: ${"%.2f".format(time)}  |  sX: ${"%.2f".format(positionX)}  |  sY: ${"%.2f".format(positionY)}  |  v: ${"%.2f".format(velocity)}  |  vX: ${"%.2f".format(velocityX)}  |  vY: ${"%.2f".format(velocityY)}  |  g: ${"%.2f".format(gravity)}")

            if (simulationTime >= simulationDuration) {
                break
            }

            sleep(5)
        }

        println("\n \n Simulation Over \n")
        history.exportHistory(2,1)
        println("")
        history.generateGraph("Position Y / Position X", "Position X", "Position Y", history.pullVariable(1), history.pullVariable(2))
        println("")
        endOfProgram()
    }

    // Pauses the program so user can see the created graph in the window, waits for user to press Enter and then closes the program
    fun endOfProgram() {
        println("Program execution finished. \n \nPress Enter when you would like to close this program.")
        readln()
        exitProcess(0)
    }
}