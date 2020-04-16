package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.testUtils.assertValid
import kotlin.test.Test

class BlockBuilderTest {
    @Test
    fun basicAlloc() {
        val ir = RootBlock().open {
            val x0 = alloc("x", TyDouble)
            val y0 = alloc("y", TyDouble)
            val z0 = alloc("z", TyDouble)

            val x1 = x0.add(y0, z0)
            ret(x1)
        }

        ir.assertValid()
    }
}
