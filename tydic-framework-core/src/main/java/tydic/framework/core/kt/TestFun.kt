package tydic.framework.core.kt

import tydic.framework.core.domain.RCode

data class Test(
    var age: Int? = null,
    var name: String? = null,
    var test: Test? = null
)

fun main() {
    Test()
        .apply {
            age.requireNotNull { "年龄不能为空" }
                .require { it > 10 }.orElseThrow { "年龄必须大于10岁" }
                .require { it < 100 }.orElseThrow { "年龄必须小于100岁" }
            name.requireNotBlank { "名字不能为空" }
        }
        .apply {
            add(this)
        }

    var toEnumValueString = RCode.success.toEnumLabel()
}

fun add(it: Test) {
    TODO("Not yet implemented")
}



