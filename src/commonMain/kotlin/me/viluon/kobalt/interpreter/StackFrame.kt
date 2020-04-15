package me.viluon.kobalt.interpreter

import me.viluon.kobalt.interpreter.runtimeTypes.LuaNil
import me.viluon.kobalt.interpreter.runtimeTypes.LuaValue

sealed class StackFrame(
    var integers: LongArray,
    var doubles: DoubleArray,
    var generic: Array<LuaValue<*>>
)

class StackFrameGeneric : StackFrame(
    LongArray(0),
    DoubleArray(0),
    Array(16) { LuaNil }
)
