package interfaces

interface Body {
    val velocity: Double
    val acceleration: Double
    val initialDisplacement: Double

    fun getDisplacement(time: Double): Double

    fun getCurrentVelocity(time: Double): Double
}