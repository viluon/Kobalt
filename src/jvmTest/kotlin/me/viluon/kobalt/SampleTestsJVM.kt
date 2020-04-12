package me.viluon.kobalt

import me.viluon.kobalt.compiler.syntax.Keyword
import kotlin.test.Test
import kotlin.test.assertTrue

class SampleTestsJVM {
    @Test
    fun testHello() {
        println(Keyword.KwAnd.keyword)
        assertTrue("JVM" in hello())
    }
}