package classes.body

import interfaces.Body
import kotlin.math.pow
import kotlin.math.sqrt

class SimpleBody2D(val velocityX: Double,
                   val velocityY: Double,
                   override val acceleration: Double,
                   val initialPositionX: Double,
                   val initialPositionY: Double,
                   override val initialDisplacement: Double,
                   override val velocity: Double): Body {

    val initialVelocityX = velocityX
    val initialVelocityY = velocityY

    fun getCurrentPositionX(time: Double): Double {
        return initialPositionX + (time * velocityX)
    }

    fun getCurrentPositionY(time: Double): Double {
        return initialPositionY + (time * velocityY) + (0.5 * acceleration * time.pow(2.0))
    }

    fun getCurrentVelocityX(): Double {
        return initialVelocityX
    }

    fun getCurrentVelocityY(time: Double): Double {
        return initialVelocityY + (time * acceleration)
    }

    fun getCurrentCombinedVelocity(time: Double): Double {
        return sqrt(getCurrentVelocityX().pow(2) + getCurrentVelocityY(time).pow(2))
    }

    override fun getDisplacement(time: Double): Double {
        return 0.0
    }

    override fun getCurrentVelocity(time: Double): Double {
        return 0.0
    }
}