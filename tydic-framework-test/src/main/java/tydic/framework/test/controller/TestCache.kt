package tydic.framework.test.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import tydic.framework.core.plugin.auth.annotation.MustPermission
import tydic.framework.core.plugin.cache.Cache
import tydic.framework.test.eo.SysApp

@RestController
class TestCache : Cache {

    @MustPermission("1:a:2")
    @PostMapping
    fun test(@RequestBody sysApp: SysApp): SysApp? = null;
}