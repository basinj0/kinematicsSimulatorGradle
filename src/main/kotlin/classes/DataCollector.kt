package classes

import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.SwingWrapper
import org.knowm.xchart.XYChartBuilder
import org.knowm.xchart.style.markers.SeriesMarkers
import java.io.File
import java.time.LocalDateTime

class DataCollector {

    var history = mutableListOf<List<Double>>()

    fun resolveSafeFile(fileName: String): File {
        val documentsDir = File(System.getProperty("user.home"), "Documents")
        val appFolder = File(documentsDir, "KinematicsSimulator")
        if (!appFolder.exists()) {
            appFolder.mkdirs()
        }
        return File(appFolder, fileName)
    }

    fun addToSimple1DHistory(time: Double, displacement: Double, velocity: Double, acceleration: Double) {
        val currentSnapshot = mutableListOf<Double>()

        currentSnapshot.add(time)
        currentSnapshot.add(displacement)
        currentSnapshot.add(velocity)
        currentSnapshot.add(acceleration)

        history.add(currentSnapshot)
    }

    fun addToInter1DHistory(time: Double, displacement: Double, velocity: Double, acceleration: Double, jerk: Double) {
        val currentSnapshot = mutableListOf<Double>()

        currentSnapshot.add(time)
        currentSnapshot.add(displacement)
        currentSnapshot.add(velocity)
        currentSnapshot.add(acceleration)
        currentSnapshot.add(jerk)

        history.add(currentSnapshot)
    }

    fun addToSimple2DHistory(time: Double, positionX: Double, positionY: Double, velocity: Double, velocityX: Double, velocityY: Double, gravity: Double) {
        val currentSnapshot = mutableListOf<Double>()

        currentSnapshot.add(time)
        currentSnapshot.add(positionX)
        currentSnapshot.add(positionY)
        currentSnapshot.add(velocity)
        currentSnapshot.add(velocityX)
        currentSnapshot.add(velocityY)
        currentSnapshot.add(gravity)

        history.add(currentSnapshot)
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

    fun exportHistory(dimensionality: Int, complexity: Int): String {
        val index = getNextAvailableFileName("simulationResults","csv")

        val file = resolveSafeFile("simulationResults$index.csv")

        val currentTime = "${LocalDateTime.now()}"

        val initialLine = "$index,$dimensionality,$complexity,${currentTime.substringBefore(".")}"

        when (dimensionality) {
            1 if complexity == 1 -> {

                file.printWriter().use { out ->
                    out.println(initialLine)
                    out.println("time,displacement,velocity,acceleration")

                    for (row in history) {
                        out.println("${row[0]},${row[1]},${row[2]},${row[3]}")
                    }
                }

                return "CSV results saved to ${file.absolutePath}"
            }
            1 if complexity == 2 -> {

                file.printWriter().use { out ->
                    out.println(initialLine)
                    out.println("time,displacement,velocity,acceleration,jerk")

                    for (row in history) {
                        out.println("${row[0]},${row[1]},${row[2]},${row[3]},${row[4]}")
                    }
                }

                return "CSV results saved to ${file.absolutePath}"
            }
            1 if complexity == 3 -> {
                TODO("To be implemented")
            }
            2 if complexity == 1 -> {

                file.printWriter().use { out ->
                    out.println(initialLine)
                    out.println("time,position x,position y,combined velocity,velocity x,velocity y,gravity")

                    for (row in history) {
                        out.println("${row[0]},${row[1]},${row[2]},${row[3]},${row[4]},${row[5]},${row[6]}")
                    }
                }

                return "CSV results saved to ${file.absolutePath}"
            }
        }
        return ""
    }

    fun pullVariable(variableIndex: Int): List<Double> {
        val output = mutableListOf<Double>()

        for (row in history) {
            output.add(row[variableIndex])
        }

        return output
    }

    fun generateGraph(chartTitle: String, xAxisName: String, yAxisName: String, xAxis: List<Double>?, yAxis: List<Double>?): String {

        val chart = XYChartBuilder()
            .width(1280)
            .height(720)
            .xAxisTitle(xAxisName)
            .yAxisTitle(yAxisName)
            .title(chartTitle)
            .build()

        val series = chart.addSeries(yAxisName, xAxis, yAxis)

        series.marker = SeriesMarkers.NONE

        val index = getNextAvailableFileName("simulationGraph","png")

        val fileName = resolveSafeFile("simulationGraph$index.png").absolutePath

        BitmapEncoder.saveBitmap(chart, fileName, BitmapEncoder.BitmapFormat.PNG)

        Thread {
            val frame = SwingWrapper(chart).displayChart()
            frame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE)
        }.start()

        return "Graph saved successfully as $fileName!"
    }
}