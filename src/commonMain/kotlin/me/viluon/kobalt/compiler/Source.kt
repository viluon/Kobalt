package me.viluon.kobalt.compiler

@Suppress("DataClassPrivateConstructor")
data class Source private constructor(val name: String, val contents: CharArray) {
    @ExperimentalStdlibApi
    companion object {
        fun fromString(name: String, contents: String): Source = Source(name, contents.toCharArray())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Source
        if (name != other.name) return false
        return contents.contentEquals(other.contents)
    }

    override fun hashCode(): Int {
        return 31 * name.hashCode() + contents.contentHashCode()
    }
}
