package tydic.framework.test.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import tydic.framework.test.eo.SysApp

@RestController
class Test {
    @PostMapping
    fun test(@RequestBody sysApp: SysApp): SysApp? = null;
}