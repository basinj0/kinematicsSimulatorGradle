package classes.body

import interfaces.ComplexBody

class ComplexBody1D(
    var velocity: Double,
    val acceleration: Double,
    var position: Double,
    val mass: Double,
    val width: Double,
    override var id: Int?
) : ComplexBody {

    override fun setID(ID: Int) {
        id = ID
    }

    override fun updateValues(hertz: Int) {
        val timePassed = 1.0 / hertz

        position += velocity * timePassed
        velocity += acceleration * timePassed
    }
}