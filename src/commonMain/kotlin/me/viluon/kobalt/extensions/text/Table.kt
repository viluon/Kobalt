package me.viluon.kobalt.extensions.text

import me.viluon.kobalt.extensions.Nat

typealias PortNodeConnections = List<Pair<String, Int>>

data class Table(val rows: List<TableRow>) {
    companion object {
        const val maxColumns = 6
    }

    fun toHTML(connections: PortNodeConnections): Pair<String, PortNodeConnections> {
        val (str, conns) = rows.fold(Pair("", connections)) { (str, connAcc), row ->
            val (a, b) = row.toHTML(connAcc)
            Pair(str + "\t" + a, b + connAcc)
        }

        return Pair(
            """
            <table border="1" cellspacing="0" cellpadding="0" align="left" bgcolor="black">
            $str
            </table>
        """.trimIndent(), conns
        )
    }
}

/**
 * An HTML table row for rendering to Dot.
 */
@Suppress("NOTHING_TO_INLINE")
data class TableRow(val data: List<TableCell>) {
    constructor(vararg d: TableCell) : this(d.asList())

    fun toHTML(conns: PortNodeConnections): Pair<String, PortNodeConnections> {
        val (str, id) = data.fold(Pair("", conns)) { (str, connectionAcc), td ->
            val (a, b) = td.toHTML(connectionAcc, if (td == data.last()) Table.maxColumns - data.size else 1)
            Pair(str + a, b + connectionAcc)
        }
        return Pair("<tr>$str</tr>", id)
    }

    inline operator fun plus(d: TableCell): TableRow {
        return TableRow(data + d)
    }
}

data class TableCell(val txt: Text, val connections: List<Int> = listOf()) {
    constructor(txt: Text, connection: Nat) : this(txt, listOf(connection.value))

    fun toHTML(conns: PortNodeConnections, colspan: Int): Pair<String, PortNodeConnections> {
        val port = "data_" + conns.size
        val portAttr = if (connections.isNotEmpty()) "port=\"$port\"" else ""
        return Pair(
            "<td colspan=\"$colspan\" align=\"left\" bgcolor=\"black\" $portAttr>${txt.toHTML()}</td>",
            connections.map { Pair(port, it) })
    }
}
