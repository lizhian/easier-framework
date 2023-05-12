package tydic.framework.test.controller

import io.javalin.Javalin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import tydic.framework.core.kt.*
import tydic.framework.test.eo.SysApp

val servletRequest: Any

@RestController
class Test {
    @PostMapping
    fun test(@RequestBody sysApp: SysApp): SysApp? = null;


}

fun main() {

    var request = SysApp()

    val takenValue = request.getParameter("token")
        .requireNotBlank { "没有token字段" }
        .let { parseToken(it) }
        .requireNotNull { "token解析错误" }
    val groupId = request.getParameter("groupId")
        .requireNotBlank { "没有groupId字段" }

    println("$takenValue $groupId")



    eo.appAddr
        .requireNotBlank { "地址不能为空" }
        .require { it.length > 10 }.orElseThrow { "地址长度必须大于10" }


    val app = Javalin.create().start(7000)
    app.get("/") {
        it.json("haha")
    }
}

fun parseToken(it: String) :String?{
    TODO("Not yet implemented")
}
