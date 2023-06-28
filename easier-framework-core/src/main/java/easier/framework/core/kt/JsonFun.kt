package easier.framework.core.kt

import easier.framework.core.plugin.jackson.ObjectMapperHolder
import easier.framework.core.util.JacksonUtil

/**
 * 拓展Json转换方法
 */

/**
 * json字符串转bean
 */
inline fun <reified T> String?.jsonToBean(): T? {
    val json = this
    if (json.isNullOrBlank()) {
        return null;
    }
    val objectMapper = if (T::class.java == Any::class.java)
        ObjectMapperHolder.withTyping() else ObjectMapperHolder.get()
    return objectMapper.readValue(json, T::class.java)
}

/**
 * json字符串转list
 */
inline fun <reified T> String?.jsonToList(): List<T> {
    val json = this
    if (json.isNullOrBlank()) {
        return listOf()
    }
    val objectMapper = if (T::class.java == Any::class.java)
        ObjectMapperHolder.withTyping() else ObjectMapperHolder.get()
    val listType = ObjectMapperHolder.get().typeFactory
        .constructParametricType(List::class.java, T::class.java);
    return objectMapper.readValue(json, listType)
}

inline fun <reified T> String?.jsonToSet(): Set<T> {
    val json = this
    if (json.isNullOrBlank()) {
        return setOf()
    }
    val objectMapper = if (T::class.java == Any::class.java)
        ObjectMapperHolder.withTyping() else ObjectMapperHolder.get()
    val listType = ObjectMapperHolder.get().typeFactory
        .constructParametricType(Set::class.java, T::class.java);
    return objectMapper.readValue(json, listType)
}

fun String?.jsonToMap(): Map<String, Any> {
    val json = this
    json.let { }
    if (json.isNullOrBlank()) {
        return mapOf()
    }
    return JacksonUtil.toMapObject(json).nullToEmpty()
}


fun <T> T.toJsonString(): String =
    if (this == null) String() else JacksonUtil.toString(this)

fun <T> T.toJsonTypingString(): String =
    if (this == null) String() else JacksonUtil.toTypingString(this)

fun <T> T.toJsonBytes(): ByteArray =
    if (this == null) emptyByteArray() else JacksonUtil.toBytes(this)

fun <T> T.toJsonTypingBytes(): ByteArray =
    if (this == null) emptyByteArray() else JacksonUtil.toTypingBytes(this)
