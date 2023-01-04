package tydic.framework.core.kt

import tydic.framework.core.util.StrUtil
/**
 * 拓展判断方法
 */
class PredicateFun

fun Any?.isNull(): Boolean = this == null
fun Any?.isNotNull(): Boolean = this != null
fun CharSequence?.isBlank(): Boolean = this.isNullOrBlank()
fun Any?.isBlankIfStr(): Boolean = StrUtil.isBlankIfStr(this)
fun CharSequence?.isNotBlank(): Boolean = !this.isNullOrBlank()
fun <E : CharSequence, T : Collection<E?>> T.filterNotBlank(): List<E> = this.filterNotNull().filter { it.isNotBlank() }