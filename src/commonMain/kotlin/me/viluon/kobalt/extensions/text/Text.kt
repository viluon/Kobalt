package me.viluon.kobalt.extensions.text

@Suppress("NOTHING_TO_INLINE")
data class Text(val fragments: List<Fragment>) {
    companion object {
        inline operator fun invoke(): Text = Text("")

        inline operator fun invoke(str: String): Text {
            return Text(None, str)
        }

        inline operator fun invoke(fmt: Formatting, str: String): Text {
            return Text(listOf(Formatted(fmt, str)))
        }
    }

    inline operator fun plus(str: String): Text = this + Text((fragments.last() as? Formatted)?.fmt ?: None, str)
    inline operator fun plus(txt: Text): Text = Text(fragments + txt.fragments)
    inline operator fun plus(fmt: Formatting): Text = this + Text(fmt, "")
    inline operator fun plus(frag: Fragment): Text = Text(fragments + frag)

    private inline fun convert(f: (fmt: Formatting, str: String) -> String): String {
        return fragments.filter { it.str.isNotEmpty() }.fold("") { acc, frag ->
            acc + when (frag) {
                is Raw -> frag.str
                is Formatted -> f(frag.fmt, frag.str)
            }
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

sealed class Fragment {
    abstract val str: String
}

data class Raw(override val str: String) : Fragment()
data class Formatted(val fmt: Formatting, override val str: String) : Fragment()
