package me.viluon.kobalt.testUtils

import me.viluon.kobalt.compiler.ir.Verifiable
import kotlin.test.assertTrue

fun Verifiable.assertValid() {
    val errors = checkInvariants()
    if (errors.isEmpty()) return

    assertTrue(errors.isEmpty(), "The following invariants were violated:\n${
    errors.reduce { a, b -> a + "\n" + b }
    }\n")
}
