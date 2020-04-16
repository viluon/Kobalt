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

            val one = const(1.0)
            val y1 = y0.loadK(one)
            val z1 = z0.loadK(one)

            val x1 = x0.add(y1, z1)
            ret(x1)
        }

        println(ir.asDot())
        ir.assertValid()
    }

    @Test
    fun basicLoop() {
        val ir = RootBlock().open {
            val x0 = alloc("x", TyInteger)
            val y0 = alloc("y", TyInteger)
            val one = const(1)
            val five = const(5)

            val x1 = x0 loadK one
            val y1 = y0 loadK five

            eqI(x1, y1, block {
                ret(x1)
            }, block {
                val x2 = x1.add(x1, y1)
                ret(x2)
            })
        }

        println(ir.asDot())
        ir.assertValid()
    }
}
