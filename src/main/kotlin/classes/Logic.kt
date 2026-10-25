
package classes

// Import other classes
import classes.body.InterBody1D
import classes.body.SimpleBody1D
import classes.body.SimpleBody2D

import kotlin.time.TimeSource

class Logic {

    // Simulator for Simple 1D
    fun simulation1DSimple(
        body: SimpleBody1D,
        simulationDuration: Double,
        lastTime: TimeSource.Monotonic.ValueTimeMark,
        onTick: (time: Double, displacement: Double, velocity: Double, acceleration: Double) -> Unit // 👈 Add this callback
    ) {
        val timeSource = TimeSource.Monotonic
        var lastTime = lastTime
        var simulationTime = 0.0
        val history = DataCollector()

        while (true) {
            val elapsed = lastTime.elapsedNow()
            lastTime = timeSource.markNow()

            val dt = elapsed.inWholeNanoseconds / 1000000000.0
            simulationTime += dt

            val displacement = body.getDisplacement(simulationTime)
            val velocity = body.getCurrentVelocity(simulationTime)
            val shownAcceleration = body.acceleration

            history.addToSimple1DHistory(simulationTime, displacement, velocity, shownAcceleration)

            // 👇 Send live data back to whatever is listening (your UI!)
            onTick(simulationTime, displacement, velocity, shownAcceleration)

            print(
                "\r Object 1  |  t: ${"%.2f".format(args = arrayOf(simulationTime))}  |  s: ${
                    "%.2f".format(
                        args = arrayOf(
                            displacement
                        )
                    )
                }  |  v: ${"%.2f".format(args = arrayOf(velocity))}  |  a: ${
                    "%.2f".format(
                        args = arrayOf(
                            shownAcceleration
                        )
                    )
                }"
            )

            if (simulationTime >= simulationDuration) {
                break
            }

            Thread.sleep(5)
        }

        println("\n \n Simulation Over \n")
        history.exportHistory(1, 1)
        println("")
        history.generateGraph("Displacement / Time", "Time", "Displacement", history.pullVariable(0), history.pullVariable(1))
        println("")

    }


    // Simulator for Intermediate 1D
    fun simulation1DInter(
        body: InterBody1D,
        simulationDuration: Double,
        lastTime: TimeSource.Monotonic.ValueTimeMark,
        onTick: (time: Double, displacement: Double, velocity: Double, acceleration: Double, jerk: Double) -> Unit
    ) {
        val timeSource = TimeSource.Monotonic
        var lastTime = lastTime
        var simulationTime = 0.0
        val history = DataCollector()

        while (true) {
            val elapsed = lastTime.elapsedNow()
            lastTime = timeSource.markNow()

            val dt = elapsed.inWholeNanoseconds / 1000000000.0
            simulationTime += dt

            val displacement = body.getDisplacement(simulationTime)
            val velocity = body.getCurrentVelocity(simulationTime)
            val shownAcceleration = body.getCurrentAcceleration(simulationTime)
            val shownJerk = body.jerk

            history.addToInter1DHistory(simulationTime, displacement, velocity, shownAcceleration, shownJerk)

            // 👇 Send live data back to whatever is listening (your UI!)
            onTick(simulationTime, displacement, velocity, shownAcceleration, shownJerk)

            print(
                "\r Object 1  |  t: ${"%.2f".format(args = arrayOf(simulationTime))}  |  s: ${
                    "%.2f".format(
                        args = arrayOf(
                            displacement
                        )
                    )
                }  |  v: ${"%.2f".format(args = arrayOf(velocity))}  |  a: ${
                    "%.2f".format(
                        args = arrayOf(
                            shownAcceleration
                        )
                    )
                }"
            )

            if (simulationTime >= simulationDuration) {
                break
            }

            Thread.sleep(5)
        }

        println("\n \n Simulation Over \n")
        history.exportHistory(1, 2)
        println("")
        history.generateGraph("Displacement / Time", "Time", "Displacement", history.pullVariable(0), history.pullVariable(1))
        println("")
    }

    // Simulator for Simple 2D
    fun simulation2DSimple(
        body: SimpleBody2D,
        simulationDuration: Double,
        lastTime: TimeSource.Monotonic.ValueTimeMark,
        onTick: (time: Double, xPosition: Double, yPosition: Double, xVelocity: Double, yVelocity: Double, velocity: Double, gravity: Double) -> Unit
    ) {
        val timeSource = TimeSource.Monotonic
        var lastTime = lastTime
        var simulationTime = 0.0
        val history = DataCollector()

        while (true) {
            val elapsed = lastTime.elapsedNow()
            lastTime = timeSource.markNow()

            val dt = elapsed.inWholeNanoseconds / 1000000000.0
            simulationTime += dt

            val positionX = body.getCurrentPositionX(simulationTime)
            val positionY = body.getCurrentPositionY(simulationTime)
            val velocity = body.getCurrentCombinedVelocity(simulationTime)
            val velocityX = body.getCurrentVelocityX()
            val velocityY = body.getCurrentVelocityY(simulationTime)
            val gravity = body.acceleration

            history.addToSimple2DHistory(simulationTime, positionX, positionY, velocity, velocityX, velocityY, gravity)

            // 👇 Send live data back to whatever is listening (your UI!)
            onTick(simulationTime, positionX, positionY, velocityX, velocityY, velocity, gravity)

            print(
                "\r Object 1  |  t: ${"%.2f".format(args = arrayOf(simulationTime))}  |  s: ${
                    "%.2f".format(
                        args = arrayOf(
                            positionX
                        )
                    )
                }  |  v: ${"%.2f".format(args = arrayOf(velocity))}  |  a: ${
                    "%.2f".format(
                        args = arrayOf(
                            gravity
                        )
                    )
                }"
            )

            if (simulationTime >= simulationDuration) {
                break
            }

            Thread.sleep(5)
        }

        println("\n \n Simulation Over \n")
        history.exportHistory(2, 1)
        println("")
        history.generateGraph(
            "Position Y / Position X",
            "Position X",
            "Position Y",
            history.pullVariable(1),
            history.pullVariable(2)
                )
        println("")
    }
}