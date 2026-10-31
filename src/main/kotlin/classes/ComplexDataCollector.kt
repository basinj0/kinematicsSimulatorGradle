package classes

import classes.body.ComplexBody1D
import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.SwingWrapper
import org.knowm.xchart.XYChartBuilder
import org.knowm.xchart.internal.series.Series
import org.knowm.xchart.style.markers.SeriesMarkers
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

    fun generateGraph(variableToShow: String, numberOfObjects: Int): String {

        var xAxisName: String
        var yAxisName: String
        var chartTitle: String
        var indexToPull: Int

        if (variableToShow == "Position") {
            xAxisName = "Time"
            yAxisName = "Position"
            chartTitle = "Position / Time"
            indexToPull = 2
        } else {
            xAxisName = "Time"
            yAxisName = "Velocity"
            chartTitle = "Velocity / Time"
            indexToPull = 3
        }

        val chart = XYChartBuilder()
            .width(1280)
            .height(720)
            .xAxisTitle(xAxisName)
            .yAxisTitle(yAxisName)
            .title(chartTitle)
            .build()

        val xAxis = pullVariable(1)

        var dataSeries = mutableListOf<List<Double?>>()

        for (i in 1..numberOfObjects) {
            val variableData = pullVariable(variableIndex = indexToPull + (7 * (i - 1)))
            if (variableData != null) {
                dataSeries.add(variableData)
            }
        }

        for (i in dataSeries.indices) {
            val series = chart.addSeries(
                "$yAxisName ${i + 1}",
                xAxis,
                dataSeries[i]
            )

            series.setMarker(SeriesMarkers.NONE)
        }
        val index = getNextAvailableFileName("simulationGraph","png")

        val fileName = resolveSafeFile("simulationGraph$index.png").absolutePath

        BitmapEncoder.saveBitmap(chart, fileName, BitmapEncoder.BitmapFormat.PNG)

        Thread {
            val frame = SwingWrapper(chart).displayChart()
            frame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE)
        }.start()

        return "Graph saved successfully as $fileName!"
    }

    fun pullVariable(variableIndex: Int): List<Double?> {
        val output = mutableListOf<Double?>()

        for (row in complexHistory) {
            output.add(row[variableIndex])
        }

        return output
    }
}