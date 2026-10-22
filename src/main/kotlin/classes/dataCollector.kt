package classes

import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.SwingWrapper
import org.knowm.xchart.XYChartBuilder
import org.knowm.xchart.style.markers.SeriesMarkers
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

    fun addToInter1DHistory(time: Double, displacement: Double, velocity: Double, acceleration: Double, jerk: Double) {
        val currentSnapshot = mutableListOf<Double>()

        currentSnapshot.add(time)
        currentSnapshot.add(displacement)
        currentSnapshot.add(velocity)
        currentSnapshot.add(acceleration)
        currentSnapshot.add(jerk)

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
        } else if (dimensionality == 1 && complexity == 2) {

            file.printWriter().use { out ->
                out.println("time,displacement,velocity,acceleration,jerk")

                for (row in history) {
                    out.println("${row[0]},${row[1]},${row[2]},${row[3]},${row[4]}")
                }
            }

            println("CSV results saved to ${file.absolutePath}")
        }
    }

    fun pullVariable(variableIndex: Int): List<Double> {
        val output = mutableListOf<Double>()

        for (row in history) {
            output.add(row[variableIndex])
        }

        return output
    }

    fun generateGraph(chartTitle: String, xAxisName: String, yAxisName: String, xAxis: List<Double>, yAxis: List<Double>) {

        // Setup the chart
        val chart = XYChartBuilder()
            .width(1280)
            .height(720)
            .xAxisTitle(xAxisName)
            .yAxisTitle(yAxisName)
            .title(chartTitle)
            .build()

        // Add data
        val series = chart.addSeries(yAxisName, xAxis, yAxis)

        // Remove dots
        series.marker = SeriesMarkers.NONE

        BitmapEncoder.saveBitmap(chart, getNextAvailableFileName("simulation_graph", "png"), BitmapEncoder.BitmapFormat.PNG)
        println("Graph saved successfully as simulation_graph.png!")

        SwingWrapper(chart).displayChart()
    }
}