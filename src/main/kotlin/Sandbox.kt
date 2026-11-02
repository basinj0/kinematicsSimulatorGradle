import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import classes.ComplexDataCollector
import classes.ComplexLogic
import classes.SimulationRecords
import classes.body.ComplexBody1D
import interfaces.ComplexBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.TimeSource


fun main() {

    val records = SimulationRecords()

    val index = 40
    val historyIndexSelected = 41


    val names = records.pullNamesOfParameters(index?.toInt() ?: -1)
    val numberOfObjects = records.getNumberOfObjectsFromComplexCSV(historyIndexSelected-1) ?: 0
    var initialValues = records.pullInitialSimulationParameters(index?.toInt() ?: -1)
    val numberOfParameters = names?.size?.times(numberOfObjects) ?: 0
    val combinedLists = mutableListOf<List<String>>()

    val filteredInitialValues = mutableListOf<String>()

    if (initialValues != null) {
        for (value in initialValues) {
            if (value != "   ") {
                filteredInitialValues.add(value)
            }
        }
    }

    for (i in 0..<numberOfParameters) {
        val listToBeAdded = listOf(names?.get((i % names.size)).toString(),
            filteredInitialValues?.get(i).toString()
        )
        combinedLists.add(listToBeAdded)
    }

    println(combinedLists)
}
