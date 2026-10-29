import classes.ComplexLogic
import classes.SimulationRecords
import classes.body.ComplexBody1D


fun main() {

    val complexLogic = ComplexLogic()

    val body1 = ComplexBody1D(10.0,0.0, 0.0, 1.0, 1.0, 0)
    val body2 = ComplexBody1D(0.0,0.0, 8.0, 3.0, 1.0, 1)
    val body3 = ComplexBody1D(0.0,0.0, 18.0, 9.0, 1.0, 2)

    complexLogic.addBody(body1)
    complexLogic.addBody(body2)
    complexLogic.addBody(body3)

    complexLogic.simulation1DComplex(5.0)
}
