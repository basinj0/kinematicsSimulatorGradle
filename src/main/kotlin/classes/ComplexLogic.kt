package classes

import classes.body.ComplexBody1D
import classes.body.SimpleBody1D
import interfaces.ComplexBody
import kotlin.math.abs
import kotlin.math.round
import kotlin.time.TimeSource

class ComplexLogic {

    var allBodies = mutableListOf<ComplexBody>()

    fun resetList() {
        allBodies = mutableListOf<ComplexBody>()
    }

    fun addBody(body: ComplexBody) {
        body.setID(allBodies.lastIndex + 1)
        allBodies.add(body)
    }

    fun updateAllBodies(timePassed: Double) {
        for (body in allBodies) {
            body.updateValues(timePassed)
        }
    }

    fun are1DBodiesColliding(object1: ComplexBody1D, object2: ComplexBody1D): Boolean {
        if ((0.5 * object1.width + 0.5 * object2.width) > abs(object1.position - object2.position)) {
            collision1D(object1, object2, 1.0)
            return true
        } else {
            return false
        }
    }

    fun collision1D(object1: ComplexBody1D, object2: ComplexBody1D, elasticity: Double) {
        val velocity1 = object1.velocity
        val velocity2 = object2.velocity
        val mass1 = object1.mass
        val mass2 = object2.mass

        object1.velocity = (((mass1 - (elasticity * mass2)) * velocity1) + mass2 * (1 + elasticity) * velocity2) / (mass1 + mass2)
        object2.velocity = (((mass2 - (elasticity * mass1)) * velocity2) + mass1 * (1 + elasticity) * velocity1) / (mass1 + mass2)
    }

    fun pull1DBody(bodyIndex: Int): ComplexBody1D {
        return allBodies[bodyIndex] as ComplexBody1D
    }

    fun checkAll1DBodies(allBodies: List<ComplexBody>) {

        val bodies = allBodies as List<ComplexBody1D>

        var index = 0

        var comparisonBody: ComplexBody1D
        var comparisonIndex = 0

        while (index < allBodies.size - 1) {
            comparisonBody = bodies[index]
            comparisonIndex = index

            index++

            while (index < allBodies.size) {
                are1DBodiesColliding(comparisonBody, bodies[index])
                index++
            }

            index = comparisonIndex + 1
        }
    }

    fun simulation1DComplex(
        duration: Double,
        lastTime: TimeSource.Monotonic.ValueTimeMark,
        onTick: (allBod: List<ComplexBody>, time: Double) -> Unit
    ): ComplexDataCollector {

        val timeSource = TimeSource.Monotonic
        var lastTime = lastTime

        val history = ComplexDataCollector()

        val hertz = Settings.hertz.toInt()
        val lengthOfTimePassed = 1.0 / hertz

        for (body in allBodies) {
            val body = body as ComplexBody1D
            body.outputAllValues()
        }

        if (Settings.currentSimulationTiming == "Real-Time") {
            var simTime = 0.0

            while (simTime < duration) {

                val elapsed = lastTime.elapsedNow()
                lastTime = timeSource.markNow()

                val dt = elapsed.inWholeNanoseconds / 1000000000.0

                simTime += dt

                updateAllBodies(dt)

                checkAll1DBodies(allBodies)

                history.addToComplex1DHistory(allBodies as List<ComplexBody1D>, (round(simTime*1000))/1000)

                onTick(allBodies, simTime)

                Thread.sleep(5)
            }

            return history

        } else {

            var simTime = 0.0

            while (simTime < duration) {

                updateAllBodies(lengthOfTimePassed)

                checkAll1DBodies(allBodies)

                simTime += (1.0 / Settings.hertz.toInt())

                history.addToComplex1DHistory(allBodies as List<ComplexBody1D>, simTime)

                onTick(allBodies as List<ComplexBody1D>, simTime)
            }

            return history
        }
    }
}