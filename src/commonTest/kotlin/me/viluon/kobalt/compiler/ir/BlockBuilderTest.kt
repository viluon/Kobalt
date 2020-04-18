package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.compiler.ir.TypelevelParamChecks.One.check
import me.viluon.kobalt.compiler.ir.TypelevelParamChecks.Three.check
import me.viluon.kobalt.compiler.ir.TypelevelParamChecks.Two.check
import me.viluon.kobalt.compiler.ir.Variable.Companion.arg
import me.viluon.kobalt.extensions.Z
import me.viluon.kobalt.extensions.hvectOf
import me.viluon.kobalt.testUtils.assertValid
import kotlin.test.BeforeTest
import kotlin.test.Test

class BlockBuilderTest {
    @BeforeTest
    fun resetBlockCounter() {
        BasicBlock.i = Z
    }

    @Test
    fun basicAlloc() {
        val ir = RootBlock().open {
            val x0 = alloc("x", TyDouble)
            val y0 = alloc("y", TyDouble)
            val z0 = alloc("z", TyDouble)

            val one = const(1.0)
            val y1 = loadK(y0, one)
            val z1 = loadK(z0, one)

            val x1 = add(x0, y1, z1)
            ret(x1)
        }

        println(ir.asDigraph())
        ir.assertValid()
    }

    @Suppress("NAME_SHADOWING")
    @Test
    fun basicBranch() {
        val ir = RootBlock().open {
            val x0 = alloc("x", TyInteger)
            val y0 = alloc("y", TyInteger)
            val one = const(1)
            val five = const(5)

            val x1 = loadK(x0, one)
            val y1 = loadK(y0, five)

            val x1p = hvectOf(x1)
            val params = hvectOf(x1, y1)
            val sig1 = hvectOf(arg("x", TyInteger))
            val sig2 = hvectOf(arg("x", TyInteger), arg("y", TyInteger))
            eqI(x1, y1, sig1 check x1p, sig2 check params, block(sig1) {
                ret(phi())
            }, block(sig2) {
                val (x1, y1) = phi()
                val x2 = add(x1, x1, y1)
                ret(x2)
            })
        }

        println(ir.asDigraph())
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

            val i1 = loadK(i0, zero)
            val limit = loadK(limit0, twelve)
            val step = loadK(step0, one)

            val params = hvectOf(i1, step, limit)
            val signature = hvectOf(arg("i", TyInteger), arg("step", TyInteger), arg("limit", TyInteger))
            jmp(signature check params, block(signature) {
                val (i2, step, limit) = phi()
                val i3 = add(i2, i2, step)

                val parameters = hvectOf(i3, step, limit)
                val limitp = hvectOf(limit)
                val sign = hvectOf(arg("l", TyInteger))
                eqI(i3, limit, sign check limitp, signature check parameters, block(sign) {
                    ret(phi())
                }, self)
            })
        }

        println(ir.asDigraph())
        ir.assertValid()
    }
}
