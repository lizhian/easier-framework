package easier.framework.test.controller

import easier.framework.core.domain.R
import easier.framework.core.kt.toR
import easier.framework.core.plugin.auth.annotation.BaseAuth
import easier.framework.core.plugin.auth.annotation.MustPermission
import easier.framework.core.plugin.cache.Cache
import easier.framework.test.enums.EnableStatus
import easier.framework.test.eo.SysApp
import easier.framework.test.eo.SysDict
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@BaseAuth("xxx:ccc")
@Tag(name = "测试接口")
@RestController
class TestCache : Cache {


    //@MustPermission("1:a:2")
    @Operation(summary = "这是一个测试接口")
    @PostMapping
    fun test(@RequestBody sysApp: SysApp): R<SysDict>? = R.success(
        SysDict()
            .apply {
                dictId = "1"
                //createBy = "111"
                updateBy = "111"
                enableStatus = EnableStatus.disable
                deleted = false
            }
    );

    @Operation(summary = "test2")
    @PostMapping("test2")
    fun test2(sysApp: SysApp): R<SysDict>? = R.success(
        SysDict()
            .apply {
                dictId = "1"
                //createBy = "111"
                updateBy = "111"
                enableStatus = EnableStatus.disable
                deleted = false
            }
    );

    @Operation(summary = "test3")
    @PostMapping("test3")
    fun test3(myEnableStatus: EnableStatus): R<SysDict>? = R.success(
        SysDict()
            .apply {
                dictId = "1"
                //createBy = "111"
                updateBy = "111"
                deleted = false
            }
    );


    @MustPermission("123123")
    @Operation(summary = "test4", description = "哈哈哈")
    @GetMapping("test4")
    fun test4(myEnableStatus: List<EnableStatus?>?): R<SysDict>? =
        SysDict()
            .apply {
                dictId = "1"
                //createBy = "111"
                updateBy = "111"
                deleted = false
            }
            .toR()

}