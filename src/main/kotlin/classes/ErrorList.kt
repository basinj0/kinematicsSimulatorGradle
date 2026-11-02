package classes

import Step

class ErrorList(val onNavigate: (Step) -> Unit) {

    var errorID = ""
    var errorName = ""
    var errorMessage = ""

    fun error(id: String) {
        val errorInfo = pullError(id)

        errorID = id
        errorName = errorInfo?.first ?: ""
        errorMessage = errorInfo?.second ?: ""

        onNavigate(Step.ERROR)
    }

    fun pullError(id: String): Pair<String,String>? {

        when (id) {
            "G01" -> return Pair("E-GRAPHFAILEDINIT", "The graph has failed to render. \n This could be due to a tampered csv file. \n [Please report if the csv file was not tampered with.]")
            "I01" -> return Pair("E-INVALIDINPUTS", "One of your inputs was invalid. \n Please re-enter your parameters in a valid format. \n [Do not use commas for separation.]")
            "I02" -> return Pair("E-INPUTOUTOFBOUNDS", "One of your inputs was not in the valid range. \n Please re-enter your parameters within the valid range. \n [Do not use commas for separation.]")
        }

        return null
    }
}