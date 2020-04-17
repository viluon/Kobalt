package me.viluon.kobalt.extensions.text

interface Pretty {
    fun pretty(): Text
}

interface Tabular {
    fun asTable(): Table
}

interface RowData {
    fun asRow(): TableRow
}

interface TableCell {
    fun asCell(): TableData
}

interface PrettyRowData : Pretty, RowData {
    override fun asRow(): TableRow = TableRow(listOf(TableData(pretty())))
}
