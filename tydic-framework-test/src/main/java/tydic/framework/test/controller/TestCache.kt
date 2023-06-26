package tydic.framework.test.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import tydic.framework.core.domain.R
import tydic.framework.core.plugin.cache.Cache
import tydic.framework.test.eo.SysApp
import tydic.framework.test.eo.SysDict

@Tag(name = "测试接口")
@RestController
class TestCache : Cache {


    //@MustPermission("1:a:2")
    @Operation(summary = "这是一个测试接口")
    @PostMapping
    fun test(@RequestBody sysApp: SysApp): R<SysDict>? = R.success(SysDict());
}