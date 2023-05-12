package tydic.framework.core.kt

import tydic.framework.core.plugin.exception.BaseException
import tydic.framework.core.plugin.exception.biz.BizException
import tydic.framework.core.util.ValidUtil

/**
 * 拓展校验方法
 */
class ValidateFun

fun <T : Any> T.validate(vararg groups: Class<Any>): T = this.also { ValidUtil.valid(it, *groups) }
fun <T : Any> T.validOnUpdate(): T = this.also { ValidUtil.validOnUpdate(it) }
data class Require<out T>(val validated: Boolean, val thisValue: T)
fun <T> T.require(validateFun: (T) -> Boolean): Require<T> = Require(validateFun(this), this)
fun <T> Require<T>.orElse(otherValue: () -> T): T = if (this.validated) this.thisValue else otherValue()
fun <T> Require<T>.orElseThrow(
    exceptionCreator: (String) -> Throwable = { it -> BizException.of(it) },
    message: () -> String
): T = if (this.validated) this.thisValue else throw exceptionCreator(message())

fun String?.requireMatches(regex: Regex): Require<String> {
    val thisValue = this.nullToEmpty()
    val validated = this != null && regex.matches(thisValue)
    return Require(validated, thisValue)
}

fun String?.requireMatches(regex: () -> Regex): Require<String> {
    val thisValue = this.nullToEmpty()
    val validated = this != null && regex().matches(thisValue)
    return Require(validated, thisValue)
}

fun String?.requireNotBlank(message: () -> String): String {
    return if (this.isNullOrBlank())
        throw BizException.of(message())
    else this
}

fun <T> T?.requireNotNull(message: () -> String): T {
    return this ?: throw BizException.of(message())
}

fun <E, T : Collection<E>> T?.requireNotEmpty(message: () -> String): T {
    return if (this.isNullOrEmpty())
        throw BizException.of(message())
    else this
}

fun <K, V, T : Map<K, V>> T?.requireNotEmpty(message: () -> String): T {
    return if (this.isNullOrEmpty())
        throw BizException.of(message())
    else this
}

fun Any?.println() {
    println(this)
}


