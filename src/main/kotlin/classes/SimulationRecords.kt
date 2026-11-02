package classes

import convertToDouble
import java.io.File

class SimulationRecords {

    fun resolveSafeFile(fileName: String): File {
        val documentsDir = File(System.getProperty("user.home"), "Documents")
        val appFolder = File(documentsDir, "KinematicsSimulator")
        if (!appFolder.exists()) {
            appFolder.mkdirs()
        }
        return File(appFolder, fileName)
    }

    fun getBasicInfoFromCSV(file: File): List<String>? {

        val firstLine = file.useLines { lines ->
            lines.firstOrNull()
        }

        val basicInfo = firstLine?.split(",")

        return basicInfo
    }

    fun pullAllSimulationBasicInfos(): List<List<String>>? {
        val documentsDir = File(System.getProperty("user.home"), "Documents")
        val directory = File(documentsDir, "KinematicsSimulator")

        val csvFiles = directory.listFiles { file ->
            file.isFile &&
            file.name.startsWith("simulationResults") &&
            file.extension.equals("csv", ignoreCase = true)
        }

        if (csvFiles.isNullOrEmpty()) return null

        val output = mutableListOf<List<String>>()

        for (file in csvFiles.sortedBy { ((it.name).drop(17).dropLast(4)).toInt() }) {
            getBasicInfoFromCSV(file)?.let {
                output.add(it)
            }
        }

        return output
    }

    fun pullNamesOfParameters(index: Int): List<String>? {
        val file = resolveSafeFile("simulationResults$index.csv")

        val output = file.useLines { lines ->
            lines.elementAtOrNull(1)?.split(",")
        }

        return output
    }

    fun pullInitialSimulationParameters(index: Int): List<String>? {
        val file = resolveSafeFile("simulationResults$index.csv")

        val output = file.useLines { lines ->
            lines.elementAtOrNull(2)?.split(",")
        }

        return output
    }

    fun convertCSVIntoHistory(index: Int): DataCollector {
        val file = resolveSafeFile("simulationResults$index.csv")

        val data = file.useLines { lines ->
            lines.drop(2)
                .map {
                    line ->
                    line.split(",")
                        .map { it.convertToDouble() }
                }
                .toList()
        }

        val output = DataCollector()

        output.history = data.toMutableList()

        return output
    }

    fun getNumberOfObjectsFromComplexCSV(index: Int): Int? {
        val file = resolveSafeFile("simulationResults$index.csv")

        val line3 = file.useLines { lines ->
            lines.elementAtOrNull(2)?.split(",")
        }

        if (line3 == null) return null

        val newLine3 = mutableListOf<String>()

        for (item in line3) {
            newLine3.add(item)
        }

        repeat(7) {
            newLine3.add("")
        }

        var index = 0

        while (true) {

            if (newLine3?.get(7 * index) == "") {
                return index - 1
            }

            index++
        }
    }

    fun convertComplexCSVIntoHistory(index: Int): ComplexDataCollector {
        val file = resolveSafeFile("simulationResults$index.csv")

        val data = file.useLines { lines ->
            lines.drop(2)
                .map {
                        line ->
                    line.split(",")
                        .filter { it.isNotBlank() }
                        .map { it.convertToDouble() }
                }
                .toList()
        }

        val output = ComplexDataCollector()

        output.complexHistory = data.toMutableList()

        return output
    }

    fun pullDimension(index: Int) {
        val file = resolveSafeFile("simulationResults$index.csv")
        val info = getBasicInfoFromCSV(file)


    }
}