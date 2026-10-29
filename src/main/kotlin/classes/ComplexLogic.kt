package classes

import classes.body.ComplexBody1D
import interfaces.ComplexBody
import kotlin.math.abs

class ComplexLogic {

    var allBodies = mutableListOf<ComplexBody>()

    fun resetList() {
        allBodies = mutableListOf<ComplexBody>()
    }

    fun addBody(body: ComplexBody) {
        body.setID(allBodies.lastIndex + 1)
        allBodies.add(body)
    }

    fun updateAllBodies(hertz: Int) {
        for (body in allBodies) {
            body.updateValues(hertz)
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

    fun simulation1DComplex(duration: Double) {

        val history = ComplexDataCollector()

        val hertz = Settings.hertz.toInt()

        var simTime = 0.0

        while (simTime < duration) {

            updateAllBodies(hertz)

            checkAll1DBodies(allBodies)

            simTime += (1.0 / hertz)

            history.addToComplex1DHistory(allBodies as List<ComplexBody1D>, simTime)
        }

        println(history.exportComplexHistory(1,allBodies.size))
    }
}