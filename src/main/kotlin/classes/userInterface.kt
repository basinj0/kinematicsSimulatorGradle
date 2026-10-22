package classes

import com.sun.org.apache.xalan.internal.lib.ExsltDatetime.time
import interfaces.Body
import java.lang.Thread.sleep
import kotlin.text.toDoubleOrNull
import kotlin.time.TimeSource
import kotlin.math.round

class userInterface() {

    fun start() {
        val dimension = getDimensionality()

        val complexity = getComplexity()

        simulationSetup(dimension, complexity)
    }

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

    fun getDoubleValue(name: String): Double {
        println("Input the value of $name:")
        return safeReadLineDouble()
    }

    fun getDimensionality(): Int {
        while (true) {
            println("How many dimensions should the simulation utilize?   1 - 1D   2 - 2D [ Unavailable ]   3 - 3D [ Unavailable ]")
            val input = readlnOrNull()
            when (input) {
                "1" -> return 1
                "2" -> return 2
                "3" -> return 3
                else -> println("Invalid input \n")
            }
        }
    }

    fun getComplexity(): Int {
        while (true) {
            println("How complex should the simulation be?   Simple  |  Intermediate  [ Unavailable ]  |  Complex  [ Unavailable ]")
            val input = readlnOrNull()
            when (input) {
                "Simple" -> return 1
                "Intermediate" -> return 2
                "Complex" -> return 3
                else -> println("Invalid input \n")
            }
        }
    }

    fun simulationSetup(dimension: Int, complexity: Int) {

        if (complexity == 1 && dimension == 1) {
            val initialVelocity = getDoubleValue("initial velocity")
            val acceleration = getDoubleValue("acceleration")
            val initialDisplacement = getDoubleValue("initial displacement")

            val objectInFocus = simpleBody1D(initialVelocity, acceleration, initialDisplacement)

            val time = getDoubleValue("the amount of time the simulation will last")

            val timeSource = TimeSource.Monotonic
            var lastTime = timeSource.markNow()

            simulation1DSimple(objectInFocus, time, lastTime)
        }
    }

    fun simulation1DSimple(body: Body, simulationDuration: Double, lastTime: TimeSource.Monotonic.ValueTimeMark) {
        val timeSource = TimeSource.Monotonic
        var lastTime = lastTime
        var simulationTime = 0.0
        val history = dataCollector()

        while (true) {
            val elapsed = lastTime.elapsedNow()
            lastTime = timeSource.markNow()

            val dt = elapsed.inWholeNanoseconds / 1_000_000_000.0

            simulationTime += dt

            val displacement = round(body.getDisplacement(simulationTime)*100)/100.0
            val velocity = round(body.getCurrentVelocity(simulationTime)*100)/100.0
            val shownTime = round(simulationTime*100)/100.0
            val shownAcceleration = round(body.acceleration*100)/100.0

            history.addToSimple1DHistory(simulationTime,body.getDisplacement(simulationTime), body.getCurrentVelocity(simulationTime),body.acceleration)

            print("\r Object 1  |  t: ${"%.2f".format(shownTime)}  |  s: ${"%.2f".format(displacement)}  |  v: ${"%.2f".format(velocity)} |  a: ${"%.2f".format(shownAcceleration)}")

            if (simulationTime >= simulationDuration) {
                break
            }

            sleep(5)
        }

        println("")
        println("")
        println("Simulation Over")
        println("")
        history.exportHistory(1,1)
    }
}