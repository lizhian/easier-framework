package tydic.framework.core.kt

import tydic.framework.core.util.StrUtil

fun Any?.isNull(): Boolean = this == null
fun Any?.isNotNull(): Boolean = this != null
fun CharSequence?.isBlank(): Boolean = this.isNullOrBlank()
fun Any?.isBlankIfStr(): Boolean = StrUtil.isBlankIfStr(this)
fun CharSequence?.isNotBlank(): Boolean = !this.isNullOrBlank()