package classes.body

import interfaces.Body

import kotlin.math.pow

class simpleBody1D(override val velocity: Double, override val acceleration: Double, override val initialDisplacement: Double) : Body {

    val initialVelocity = velocity

    override fun getDisplacement(time: Double): Double {
        return initialDisplacement + (initialVelocity * time) + (0.5 * acceleration * time.pow(2.0))
    }

    override fun getCurrentVelocity(time: Double): Double {
        return initialVelocity + acceleration * time
    }
}