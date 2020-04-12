package me.viluon.kobalt.compiler

data class Chunk(val bytecode: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Chunk
        return bytecode.contentEquals(other.bytecode)
    }

    override fun hashCode(): Int {
        return bytecode.contentHashCode()
    }
}