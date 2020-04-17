package me.viluon.kobalt.extensions.text

interface Tabular {
    fun asTable(): Table
}

interface TabularRow {
    fun asRow(): TableRow
}

interface TabularCell {
    fun asCell(): TableCell
}
