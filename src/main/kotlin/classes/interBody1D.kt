package classes

import interfaces.Body

import kotlin.math.pow

class interBody1D(override val velocity: Double, override val acceleration: Double, override val initialDisplacement: Double, val jerk: Double) : Body {

    val initialVelocity = velocity
    val initialAcceleration = acceleration

    override fun getDisplacement(time: Double): Double {
        return initialDisplacement + (initialVelocity * time) + (0.5 * acceleration * time.pow(2.0)) + ((1.0/6.0) * jerk * time.pow(3.0))
    }

    override fun getCurrentVelocity(time: Double): Double {
        return initialVelocity + acceleration * time + (0.5 * jerk * time.pow(2.0))
    }

    fun getCurrentAcceleration(time: Double): Double {
        return initialAcceleration + (jerk * time)
    }
}