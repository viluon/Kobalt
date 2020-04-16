package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.testUtils.assertValid
import kotlin.test.BeforeTest
import kotlin.test.Test

class BlockBuilderTest {
    @BeforeTest
    fun resetBlockCounter() {
        BasicBlock.i = 0
    }

    @Test
    fun basicAlloc() {
        val ir = RootBlock().open {
            val x0 = alloc("x", TyDouble)
            val y0 = alloc("y", TyDouble)
            val z0 = alloc("z", TyDouble)

            val one = const(1.0)
            val y1 = y0 loadK one
            val z1 = z0 loadK one

            val x1 = x0.add(y1, z1)
            ret(x1)
        }

        println(ir.asDot())
        ir.assertValid()
    }

    @Test
    fun basicBranch() {
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

    @Suppress("NAME_SHADOWING")
    @Test
    fun basicLoop() {
        val ir = RootBlock().open {
            val i0 = alloc("i", TyInteger)
            val limit0 = alloc("limit", TyInteger)
            val step0 = alloc("step", TyInteger)

            val zero = const(0)
            val one = const(1)
            val twelve = const(12)

            val i1 = i0 loadK zero
            val limit = limit0 loadK twelve
            val step = step0 loadK one

            val i2 = i1.next
            val i3 = i2.next

            jmp(block {
                val i2 = i1.phiI(i1, i3)
                val i3 = i2.add(i2, step)

                eqI(i3, limit, block {
                    ret(limit)
                }, self)
            })
        }

        println(ir.asDot())
        ir.assertValid()
    }
}
