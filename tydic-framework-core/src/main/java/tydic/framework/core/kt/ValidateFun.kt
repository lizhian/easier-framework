package tydic.framework.core.kt

import tydic.framework.core.plugin.exception.biz.BizException
import tydic.framework.core.util.ValidUtil

/**
 * 拓展校验方法
 */
class ValidateFun

fun <T : Any> T.validate(vararg groups: Class<Any>): T = this.also { ValidUtil.valid(it, *groups) }
fun <T : Any> T.validOnUpdate(): T = this.also { ValidUtil.validOnUpdate(it) }
data class Require<out T>(val matched: Boolean, val value: T)

fun <T> T.require(block: (T) -> Boolean): Require<T> = Require(block.invoke(this), this)
fun <T> Require<T>.orElse(block: () -> T): T = if (this.matched) this.value else block()

fun <T> Require<T>.orElseThrow(
    exceptionCreator: (String) -> Throwable = BizException.Creator,
    block: () -> String
): T = if (this.matched) this.value else throw exceptionCreator.invoke(block())

fun String?.requireMatches(regex: Regex): Require<String> {
    val success = this != null && regex.matches(this.nullToEmpty())
    return Require(success, this.nullToEmpty())
}

fun String?.requireMatches(block: () -> Regex): Require<String> {
    val success = this != null && block().matches(this.nullToEmpty())
    return Require(success, this.nullToEmpty())
}

fun String?.requireNotBlank(block: () -> String): String {
    return if (this.isNullOrBlank())
        throw BizException.of(block())
    else this
}

fun <T> T?.requireNotNull(block: () -> String): T {
    return this ?: throw BizException.of(block())
}

fun <E, T : Collection<E>> T?.requireNotEmpty(block: () -> String): T {
    return if (this.isNullOrEmpty())
        throw BizException.of(block())
    else this
}

fun <K, V, T : Map<K, V>> T?.requireNotEmpty(block: () -> String): T {
    return if (this.isNullOrEmpty())
        throw BizException.of(block())
    else this
}

fun Any?.println() {
    println(this)
}


