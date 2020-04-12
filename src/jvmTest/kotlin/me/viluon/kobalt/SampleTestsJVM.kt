package me.viluon.kobalt

import me.viluon.kobalt.compiler.syntax.Keyword
import kotlin.test.Test
import kotlin.test.assertTrue

class SampleTestsJVM {
    @Test
    fun testHello() {
        assertTrue("JVM" in hello())
    }
}