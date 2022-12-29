package tydic.framework.core.kt

import cn.hutool.core.date.DateTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

/**
 * null 转 非null 的方法
 */
infix fun <T> T?.nullTo(block: () -> T): T = this ?: block.invoke()
infix fun <T> T?.nullTo(that: T): T = this ?: that

fun Date?.nullToNow(): Date = this nullTo { Date() }
fun DateTime?.nullToNow(): DateTime = this nullTo { DateTime.now() }
fun LocalDate?.nullToNow(): LocalDate = this nullTo { LocalDate.now() }
fun LocalTime?.nullToNow(): LocalTime = this nullTo { LocalTime.now() }
fun LocalDateTime?.nullToNow(): LocalDateTime = this nullTo { LocalDateTime.now() }
fun Boolean?.nullToTrue(): Boolean = this nullTo true
fun Boolean?.nullToFalse(): Boolean = this nullTo false
fun String?.nullToEmpty(): String = this nullTo { String() }
inline fun <reified T> Array<T>?.nullToEmpty(): Array<T> = this nullTo { emptyArray() }
fun <E> List<E>?.nullToEmpty(): List<E> = this nullTo { listOf() }
fun <E> Set<E>?.nullToEmpty(): Set<E> = this nullTo { setOf() }
fun <K, V> Map<K, V>?.nullToEmpty(): Map<K, V> = this nullTo { mapOf() }




