package me.viluon.kobalt.extensions.text

sealed class Formatting {
    abstract val codeANSI: String
    abstract val startHTML: String
    val endHTML: String = "</font>"
}

object None : Formatting() {
    override val codeANSI: String = "\u001b[0m"
    override val startHTML: String = "<font color=\"white\">"
}

sealed class Colour(codeHTML: String, numberANSI: Short) : Formatting() {
    override val codeANSI: String = "\u001b[${numberANSI}m"
    override val startHTML: String = "<font color=\"$codeHTML\">"
}

object Red : Colour("red", 31)
object Green : Colour("green", 32)
object Yellow : Colour("yellow", 33)
object Blue : Colour("blue", 34)
object Magenta : Colour("magenta", 35)
object Cyan : Colour("cyan", 36)
object Grey : Colour("grey", 90)
