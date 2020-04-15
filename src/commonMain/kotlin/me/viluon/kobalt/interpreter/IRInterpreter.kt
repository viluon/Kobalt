package me.viluon.kobalt.interpreter

class IRInterpreter {
    companion object {
        const val MAX_CALL_STACK_SIZE = 1024
    }

    private val stack = Array<StackFrame?>(MAX_CALL_STACK_SIZE) { null }
}
