package me.viluon.kobalt.extensions.text

@Suppress("NOTHING_TO_INLINE")
data class Text(val fragments: List<Pair<Formatting, String>>) {
    companion object {
        inline operator fun invoke(): Text = Text("")

        inline operator fun invoke(str: String): Text {
            return Text(None, str)
        }

        inline operator fun invoke(fmt: Formatting, str: String): Text {
            return Text(listOf(Pair(fmt, str)))
        }
    }

    inline operator fun plus(str: String): Text = this + Text(fragments.last().first, str)
    inline operator fun plus(txt: Text): Text = Text(fragments + txt.fragments)
    inline operator fun plus(fmt: Formatting): Text = this + Text(fmt, "")

    private inline fun convert(f: (fmt: Formatting, str: String) -> String): String {
        return fragments.filter { it.second.isNotEmpty() }.fold("") { acc, (fmt, str) ->
            acc + f(fmt, str)
        }
    }

    fun toANSI(): String = convert { fmt, str -> fmt.codeANSI + str }
    fun toHTML(): String = convert { fmt, str ->
        fmt.startHTML + str
            .replace("\n", "<br align=\"left\"/>\n")
            .replace(";", "&#59;")
            .replace("\t", "&nbsp;") +
                fmt.endHTML
    }
}
