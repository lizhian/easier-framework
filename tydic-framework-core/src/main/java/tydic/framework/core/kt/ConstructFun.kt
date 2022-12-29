package tydic.framework.core.kt


infix fun <T> Pair<Boolean, T>.or(that: T): T = if (this.first) this.second else that
fun emptyByteArray(): ByteArray = ByteArray(0)
fun emptyCharArray(): CharArray = CharArray(0)
fun emptyString(): String = String()
