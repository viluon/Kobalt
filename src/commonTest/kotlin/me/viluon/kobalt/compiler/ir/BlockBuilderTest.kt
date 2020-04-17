package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.compiler.syntax.TkIdentifier
import me.viluon.kobalt.extensions.*
import me.viluon.kobalt.testUtils.assertValid
import kotlin.test.BeforeTest
import kotlin.test.Test
import me.viluon.kobalt.compiler.ir.TypeValidationCertificate.Companion.check0
import me.viluon.kobalt.compiler.ir.TypeValidationCertificate.Companion.check1

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
            val y1 = y0 loadK one
            val z1 = z0 loadK one

            val x1 = x0.add(y1, z1)
            ret(x1)
        }

        println(ir.asDigraph())
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

            eqI(x1, y1, HNil.check0, HNil.check0, block(HNil) {
                ret(x1)
            }, block(HNil) {
                val x2 = x1.add(x1, y1)
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

            val i1 = i0 loadK zero
            val limit = limit0 loadK twelve
            val step = step0 loadK one

            val i1p = hvectOf<Proxy<TyInteger, S<Z>>, Proxy<TyInteger, *>>(i1)
            val signature = hvectOf(Variable(TkIdentifier("i"), 0, TyInteger))
            jmp(signature.check1(i1p), block(signature) {
                val i2 = phi1()
                val i3 = i2.add(i2, step)

                val i3p = HCons(i3, HNil)
                val limitp = hvectOf(limit)
                val sign = hvectOf(Variable(TkIdentifier("l"), 0, TyInteger))
                eqI(i3, limit, sign.check1(limitp), signature.check1(i3p), block(sign) {
                    ret(phi1())
                }, self)
            })
        }

        println(ir.asDigraph())
        ir.assertValid()
    }
}
