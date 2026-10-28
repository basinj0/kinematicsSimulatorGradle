
package classes

import Settings.hertz
import classes.body.InterBody1D
import classes.body.SimpleBody1D
import classes.body.SimpleBody2D

import kotlin.time.TimeSource

class Logic {

    // Simulator for Simple 1D
    fun simulation1DSimple (
        body: SimpleBody1D,
        simulationDuration: Double,
        lastTime: TimeSource.Monotonic.ValueTimeMark,
        onTick: (time: Double, displacement: Double, velocity: Double, acceleration: Double) -> Unit
    ): DataCollector {
        val timeSource = TimeSource.Monotonic
        var lastTime = lastTime
        var simulationTime = 0.0
        val history = DataCollector()

        history.addToSimple1DHistory(simulationDuration, body.getDisplacement(0.0), body.getCurrentVelocity(0.0), body.acceleration)

        if (Settings.currentSimulationTiming == "Real-Time") {
            while (true) {
                val elapsed = lastTime.elapsedNow()
                lastTime = timeSource.markNow()

                val dt = elapsed.inWholeNanoseconds / 1000000000.0
                simulationTime += dt

                val displacement = body.getDisplacement(simulationTime)
                val velocity = body.getCurrentVelocity(simulationTime)
                val shownAcceleration = body.acceleration

                history.addToSimple1DHistory(simulationTime, displacement, velocity, shownAcceleration)

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
                    return history
                }

                Thread.sleep(5)
            }
        } else {
            while (true) {

                simulationTime += 1.0 / hertz.toInt()

                val displacement = body.getDisplacement(simulationTime)
                val velocity = body.getCurrentVelocity(simulationTime)
                val shownAcceleration = body.acceleration

                history.addToSimple1DHistory(simulationTime, displacement, velocity, shownAcceleration)

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
                    return history
                }
            }
        }


    }


    // Simulator for Intermediate 1D
    fun simulation1DInter(
        body: InterBody1D,
        simulationDuration: Double,
        lastTime: TimeSource.Monotonic.ValueTimeMark,
        onTick: (time: Double, displacement: Double, velocity: Double, acceleration: Double, jerk: Double) -> Unit
    ): DataCollector {
        val timeSource = TimeSource.Monotonic
        var lastTime = lastTime
        var simulationTime = 0.0
        val history = DataCollector()

        history.addToInter1DHistory(simulationDuration, body.getDisplacement(0.0), body.getCurrentVelocity(0.0), body.getCurrentAcceleration(0.0), body.jerk)

        if (Settings.currentSimulationTiming == "Real-Time") {
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
                    return history
                }

                Thread.sleep(5)
            }
        } else {
            while (true) {

                simulationTime += 1.0 / hertz.toInt()

                val displacement = body.getDisplacement(simulationTime)
                val velocity = body.getCurrentVelocity(simulationTime)
                val shownAcceleration = body.getCurrentAcceleration(simulationTime)
                val shownJerk = body.jerk

                history.addToInter1DHistory(simulationTime, displacement, velocity, shownAcceleration, shownJerk)

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
                    return history
                }
            }
        }
    }

    // Simulator for Simple 2D
    fun simulation2DSimple(
        body: SimpleBody2D,
        simulationDuration: Double,
        lastTime: TimeSource.Monotonic.ValueTimeMark,
        onTick: (time: Double, xPosition: Double, yPosition: Double, xVelocity: Double, yVelocity: Double, velocity: Double, gravity: Double) -> Unit
    ): DataCollector {
        val timeSource = TimeSource.Monotonic
        var lastTime = lastTime
        var simulationTime = 0.0
        val history = DataCollector()

        history.addToSimple2DHistory(simulationDuration, body.getCurrentPositionX(0.0), body.getCurrentPositionY(0.0), body.getCurrentVelocity(0.0), body.getCurrentVelocityX(), body.getCurrentVelocityY(0.0), body.acceleration)

        if (Settings.currentSimulationTiming == "Real-Time") {
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
                    return history
                }

                Thread.sleep(5)
            }
        } else {
            while (true) {

                simulationTime += 1.0 / hertz.toInt()

                val positionX = body.getCurrentPositionX(simulationTime)
                val positionY = body.getCurrentPositionY(simulationTime)
                val velocity = body.getCurrentCombinedVelocity(simulationTime)
                val velocityX = body.getCurrentVelocityX()
                val velocityY = body.getCurrentVelocityY(simulationTime)
                val gravity = body.acceleration

                history.addToSimple2DHistory(simulationTime, positionX, positionY, velocity, velocityX, velocityY, gravity)

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
                    return history
                }
            }
        }
    }
}