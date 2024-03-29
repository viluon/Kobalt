package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.extensions.*
import me.viluon.kobalt.extensions.text.*
import kotlin.native.concurrent.ThreadLocal

typealias BlockParams = HVector<Proxy<*, *>, *>
typealias BlockSignature = HVector<Variable<*>, *>

sealed class BasicBlock<out S : BlockSignature>(val signature: S) : Verifiable, Tabular {
    @ThreadLocal
    companion object {
        internal var i: Nat = Z
    }

    internal val id = i.also { i = S(i) }
    open val constants: MutableList<Constant<*>> = ArrayList(4)
    open val variables: MutableList<Variable<*>> = ArrayList(4)
    open val instructions: MutableList<Instruction> = ArrayList(8)
    open val predecessors: MutableList<BasicBlock<*>> = ArrayList(2)
    val followers: MutableList<BasicBlock<*>> = ArrayList(2)
    override val verifiableChildren: Iterable<Verifiable>
        get() = (variables as Iterable<Verifiable>) + instructions + predecessors + followers
    override val invariants
        get() = define
            .invariant(instructions.isNotEmpty() && instructions.firstOrNull { it is Terminator } == instructions.last()) {
                "There should be exactly one terminator instruction in a block, and it should be the last one."
            }
            .invariant(predecessors.isNotEmpty()) {
                "There should be at least one predecessor."
            }
            .invariant(instructions.isNotEmpty()) {
                "The instruction list should not be empty."
            }
            .invariant(followers.toSet().size == followers.size) {
                "Followers should be unique."
            }
            .invariant(predecessors.toSet().size == predecessors.size) {
                "Predecessors should be unique."
            }
            .invariant(constants.toSet().size == constants.size) {
                "Constants should be unique."
            }

    fun asDigraph() = """digraph {
        bgcolor=black;
        fontcolor=white;
        color=white;
        ${toDot()}
    }""".trimIndent()

    private fun toDot(processed: Set<BasicBlock<*>> = setOf()): String {
        val (tableHTML, nodeConnections) = asTable().toHTML(listOf())

        val prefix = """
            node$id [
                shape=plain
                label=<<font face="Iosevka SS08, monospace">
                    <table border="0" bgcolor="white" cellpadding="0"><tr><td>
                    $tableHTML
                    </td></tr></table>
                    </font>>
            ];
            
        """.trimIndent()

        return followers
            .filterNot { it === this || processed.contains(it) }
            .fold(prefix) { str, block -> str + block.toDot(processed + this) } +
                nodeConnections.fold("") { acc, (port, nodeId) ->
                    acc + "node$id:$port -> node$nodeId:n [color=white];\n"
                }
    }
}

class InnerBlock<out S : BlockSignature>(signature: S) : BasicBlock<S>(signature) {
    inline fun open(builder: InnerBlockBuilder<S>.() -> Terminator): InnerBlock<S> {
        builder(InnerBlockBuilder(this))
        return this
    }

    override fun asTable(): Table {
        val rows = mutableListOf(TableRow(TableCell(Text() + ";; " + Magenta + "block #$id")))

        if (signature.isNotEmpty()) {
            rows.add(TableRow(TableCell(Text() + Grey + ";; arguments")))
            for (v in signature) {
                rows.add(v.asRow())
            }
        }

        if (variables.isNotEmpty()) {
            rows.add(TableRow(TableCell(Text() + Grey + ";; variables")))
            for (v in variables)
                rows.add(v.asRow())
        }

        rows.add(TableRow(TableCell(Text() + Grey + ";; instructions")))
        for (i in instructions) rows.add(i.asRow())

        return Table(rows)
    }
}

@Suppress("UNCHECKED_CAST")
class RootBlock : BasicBlock<HNil>(HNil) {
    companion object {
        private val empty: MutableList<out Any> = ArrayList(0)
    }

    override val variables: MutableList<Variable<*>> = empty as MutableList<Variable<*>>
    override val instructions: MutableList<Instruction> = empty as MutableList<Instruction>
    override val predecessors: MutableList<BasicBlock<*>> = empty as MutableList<BasicBlock<*>>

    inline fun open(builder: InnerBlockBuilder<HNil>.() -> Terminator): RootBlock {
        if (followers.isNotEmpty()) {
            val first = followers.first() as InnerBlock<HNil>
            first.open(builder)
            return this
        }

        val first = InnerBlock(HNil)
        this.followers.add(first)
        first.predecessors.add(this)

        first.open(builder)
        return this
    }

    override val invariants
        get() = define
            .invariant(empty.isEmpty()) {
                "The root block should have no variables, no instructions, and no predecessors."
            }
            .invariant(followers.size == 1) {
                "The root block should have exactly one follower."
            }

    override fun asTable(): Table =
        Table(listOf(TableRow(TableCell(Text() + ";;; root", listOf(followers.first().id.value)))))
}
