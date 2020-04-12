package me.viluon.kobalt.extensions

fun Char.isValidInsideIdentifier(): Boolean = when (this) {
    '_', in 'a'..'z', in 'A'..'Z', in '0'..'9' -> true
    else -> false
}
