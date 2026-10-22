package classes

import jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyles
import java.io.File

class dataCollector {

    val history = mutableListOf<List<Double>>()

    fun addToSimple1DHistory(time: Double, displacement: Double, velocity: Double, acceleration: Double) {
        val currentSnapshot = mutableListOf<Double>()

        currentSnapshot.add(time)
        currentSnapshot.add(displacement)
        currentSnapshot.add(velocity)
        currentSnapshot.add(acceleration)

        history.add(currentSnapshot)
    }

    fun getNextAvailableFileName(baseName: String, extension: String): String {
        var index = 1
        var file: File

        do {
            file = File("$baseName$index.$extension")
            index++
        } while (file.exists())

        return file.absolutePath
    }

    fun exportHistory(dimensionality: Int, complexity: Int) {
        val file = File(getNextAvailableFileName("simulationResults","csv"))

        if (dimensionality == 1 && complexity == 1) {

            file.printWriter().use { out ->
                out.println("time,displacement,velocity,acceleration")

                for (row in history) {
                    out.println("${row[0]},${row[1]},${row[2]},${row[3]}")
                }
            }

            println("CSV results saved to ${file.absolutePath}")
        }
    }
}