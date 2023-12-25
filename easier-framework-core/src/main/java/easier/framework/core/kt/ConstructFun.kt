package easier.framework.core.kt

import easier.framework.core.plugin.innerRequest.InnerRequest
import easier.framework.core.plugin.innerRequest.InnerRequestBuilder
import easier.framework.core.plugin.mq.MQ
import easier.framework.core.plugin.mq.MQBuilder
import kotlin.reflect.KClass

/**
 * 拓展构造方法
 */

/**
 * 问号表达式
 * v = bool to v1 or v2
 */
infix fun <T> Pair<Boolean, T>.or(that: T): T = if (this.first) this.second else that

/**
 * 三连体
 * v = a to b and c
 */
infix fun <A, B, C> Pair<A, B>.and(third: C): Triple<A, B, C> = Triple(this.first, this.second, third)
fun emptyByteArray(): ByteArray = ByteArray(0)
fun emptyCharArray(): CharArray = CharArray(0)
fun emptyString(): String = String()

//fun <T : Cache> Class<T>.build(): T = CacheBuilder.build(this)
//fun <T : Cache> KClass<T>.build(): T = CacheBuilder.build(this.java)
//inline fun <reified T : Cache> buildCache(): T = CacheBuilder.build(T::class.java)
fun <T : MQ> Class<T>.build(): T = MQBuilder.build(this)
fun <T : MQ> KClass<T>.build(): T = MQBuilder.build(this.java)
inline fun <reified T : MQ> buildMQ(): T = MQBuilder.build(T::class.java)
fun <T : InnerRequest> Class<T>.build(): T = InnerRequestBuilder.build(this)
fun <T : InnerRequest> KClass<T>.build(): T = InnerRequestBuilder.build(this.java)
inline fun <reified T : InnerRequest> buildInnerRequest(): T = InnerRequestBuilder.build(T::class.java)

