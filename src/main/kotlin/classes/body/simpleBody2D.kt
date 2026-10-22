package classes.body

import kotlin.math.pow
import kotlin.math.sqrt

class simpleBody2D(val velocityX: Double, val velocityY: Double, val gravity: Double, val initialPositionX: Double, val initialPositionY: Double) {

    val initialVelocityX = velocityX
    val initialVelocityY = velocityY

    fun getCurrentPositionX(time: Double): Double {
        return initialPositionX + (time * velocityX)
    }

    fun getCurrentPositionY(time: Double): Double {
        return initialPositionY + (time * velocityY) + (0.5 * gravity * time.pow(2.0))
    }

    fun getCurrentVelocityX(time: Double): Double {
        return initialVelocityX
    }

    fun getCurrentVelocityY(time: Double): Double {
        return initialVelocityY + (time * gravity)
    }

    fun getCurrentCombinedVelocity(time: Double): Double {
        return sqrt(getCurrentVelocityX(time).pow(2) + getCurrentVelocityY(time).pow(2))
    }
}