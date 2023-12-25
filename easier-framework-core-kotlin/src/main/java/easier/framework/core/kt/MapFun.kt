package easier.framework.core.kt

import cn.hutool.core.date.DateTime
import easier.framework.core.domain.R
import easier.framework.core.domain.RCode
import easier.framework.core.plugin.enums.EnumCodec
import easier.framework.core.util.StrUtil
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

/**
 * 拓展转换方法
 */
/**
 * 转换成R
 */
fun <T> T.toR(
    code: Int = RCode.success.toEnumValueInt(),
    message: String = RCode.success.toEnumDesc()
): R<T> = R.of(code, message, this)

fun <T> T.toR(code: RCode = RCode.success): R<T> = R.of(code, this)
fun Date.toDateTime(): DateTime = DateTime(this)
fun Date.toLocalTime(): LocalTime = this.toDateTime().toLocalTime()
fun Date.toLocalDate(): LocalDate = this.toDateTime().toLocalDate()
fun Date.toLocalDateTime(): LocalDateTime = this.toDateTime().toLocalDateTime()
fun LocalDateTime.toDateTime(): DateTime = DateTime(this)
fun LocalDateTime.toJdkDate(): Date = this.toDateTime().toJdkDate()
fun String?.smartSplit(): List<String> = StrUtil.smartSplit(this);

fun String.toUnderlineCase(): String = StrUtil.toUnderlineCase(this);


inline fun <E, reified T : Enum<E>> T.toEnumValue(): Any = EnumCodec.of(T::class.java).getEnumValue(this)
inline fun <E, reified T : Enum<E>> T.toEnumValueString(): String = toEnumValue().toString()
inline fun <E, reified T : Enum<E>> T.toEnumValueInt(): Int = toEnumValue() as Int
inline fun <E, reified T : Enum<E>> T.toEnumDesc(): String = EnumCodec.of(T::class.java).getEnumDesc(this)




