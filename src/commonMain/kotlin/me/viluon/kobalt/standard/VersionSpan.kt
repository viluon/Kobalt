package me.viluon.kobalt.standard

data class VersionSpan(val since: Version, val until: Version) {
    operator fun contains(version: Version): Boolean = version in since..until

    companion object {
        val permanent = VersionSpan(Version.Lua51, Version.Lua54)
        val since52 = VersionSpan(Version.Lua52, permanent.until)
        val since53 = VersionSpan(Version.Lua53, permanent.until)
    }
}
