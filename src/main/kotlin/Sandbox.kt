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

    val complexLogic = ComplexLogic()

    val body1 = ComplexBody1D(10.0,0.0, 0.0, 1.0, 1.0, 0)
    val body2 = ComplexBody1D(0.0,0.0, 8.0, 3.0, 1.0, 1)
    val body3 = ComplexBody1D(0.0,0.0, 16.0, 9.0, 1.0, 2)
    val body4 = ComplexBody1D(0.0,0.0, 24.0, 27.0, 1.0, 3)
    val body5 = ComplexBody1D(0.0,0.0, 32.0, 81.0, 1.0, 4)
    val body6 = ComplexBody1D(0.0,0.0, 40.0, 243.0, 1.0, 5)

    complexLogic.addBody(body1)
    complexLogic.addBody(body2)
    complexLogic.addBody(body3)
    complexLogic.addBody(body4)
    complexLogic.addBody(body5)
    complexLogic.addBody(body6)

    val timeSource = TimeSource.Monotonic
    val startTime = timeSource.markNow()

    var liveBodies : List<ComplexBody>
    var liveTime = 0.0

    var latestComplexHistory: ComplexDataCollector


    latestComplexHistory = complexLogic.simulation1DComplex(
        10.0,
        startTime,
    ) { allBod, time ->
        liveBodies = allBod
        liveTime = time
    }

    latestComplexHistory.exportComplexHistory(1, 6)

}
