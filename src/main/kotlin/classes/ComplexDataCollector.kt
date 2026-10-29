package classes

import classes.body.ComplexBody1D
import java.io.File
import java.time.LocalDateTime

class ComplexDataCollector {

    var complexHistory = mutableListOf<List<Double?>>()

    fun resolveSafeFile(fileName: String): File {
        val documentsDir = File(System.getProperty("user.home"), "Documents")
        val appFolder = File(documentsDir, "KinematicsSimulator")
        if (!appFolder.exists()) {
            appFolder.mkdirs()
        }
        return File(appFolder, fileName)
    }

    fun addToComplex1DHistory(allBodies: List<ComplexBody1D>, time: Double) {
        val currentSnapshot = mutableListOf<Double?>()

        for (body in allBodies) {

            currentSnapshot.add(body.id?.toDouble())

            currentSnapshot.add(time)

            currentSnapshot.add(body.position)
            currentSnapshot.add(body.velocity)
            currentSnapshot.add(body.acceleration)
            currentSnapshot.add(body.mass)
            currentSnapshot.add(body.width)
        }

        complexHistory.add(currentSnapshot)
    }

    fun getNextAvailableFileName(baseName: String, extension: String): Int {
        var index = 0
        var file: File

        do {
            index++
            file = resolveSafeFile("$baseName$index.$extension")
        } while (file.exists())

        return index
    }

    fun exportComplexHistory(dimensionality: Int, numberOfObjects: Int): String {
        val index = getNextAvailableFileName("simulationResults","csv")

        val file = resolveSafeFile("simulationResults$index.csv")

        val currentTime = "${LocalDateTime.now()}"

        val initialLine = "$index,$dimensionality,3,${currentTime.substringBefore(".")}"

        when (dimensionality) {
            1 -> {

                file.printWriter().use { out ->
                    out.println(initialLine)
                    out.println("objectIndex,time,displacement,velocity,acceleration,mass,width,spacer,")

                    for (row in complexHistory) {
                        var i = 0

                        while (i < numberOfObjects) {
                            out.print("${row[0 + (7 * i)]},${row[1 + (7 * i)]},${row[2 + (7 * i)]},${row[3 + (7 * i)]},${row[4 + (7 * i)]},${row[5 + (7 * i)]},${row[6 + (7 * i)]},   ,")
                            i++
                        }

                        out.print("\n")
                    }
                }

                return "CSV results saved to ${file.absolutePath}"
            }
            2 -> { TODO() }
            3 -> { TODO() }
        }
        return ""
    }
}