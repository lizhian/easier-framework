package tydic.framework.core.kt

import cn.hutool.core.util.RandomUtil
import tydic.framework.core.domain.RCode
import tydic.framework.core.plugin.auth.AuthContext
import java.util.*

data class Test(
    var age: Int? = null,
    var name: String? = null,
    var test: Test? = null
)

fun main() {
    val b = RandomUtil.randomBoolean();

    val velue = b to 1 or 2;
    String.toString()

        .apply {

        }


    AuthContext.login(null)
    /*PageParam.builder()
        .current(111)
        .build()
        .apply {
            this.toPage<String>()
                .records
                .filterNotNull()
                .map {
                    it.length
                }
                .let {

                }
        }
    listOf<String>()
        .apply {

        }
        .filterNotBlank()
        .associate { it.length to it.plus("") }
        .let {

        }
    Test()
        .apply {
            age.requireNotNull { "年龄不能为空" }
                .require { it > 10 }.orElseThrow { "年龄必须大于10岁" }
                .require { it < 100 }.orElseThrow { "年龄必须小于100岁" }
            name.requireNotBlank { "名字不能为空" }
        }
        .apply {
            add(this)
        }*/


    Date()
        .toDateTime()
        .toLocalDateTime()
        .toJsonString()
        .jsonToBean<Date>()
    var toEnumValueString = RCode.success.toEnumLabel()
}





