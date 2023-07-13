package easier.framework.test.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import easier.framework.core.domain.CodeQo;
import easier.framework.core.domain.CodesQo;
import easier.framework.core.domain.PageParam;
import easier.framework.core.domain.R;
import easier.framework.core.plugin.dict.DictDetail;
import easier.framework.test.eo.User;
import easier.framework.test.qo.ResetPasswordQo;
import easier.framework.test.qo.UserAssignRoleQo;
import easier.framework.test.qo.UserQo;
import easier.framework.test.service.DictService;
import easier.framework.test.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户管理
 *
 * @author lizhian
 * @date 2023/07/05
 */
@Tag(name = "用户管理")
@RestController
@RequiredArgsConstructor
public class UserController {
    /**
     * 字典服务
     */
    private final DictService dictService;
    /**
     * 用户服务
     */
    private final UserService userService;


    /**
     * 字典
     *
     * @param qo 请求对象
     * @return {@link R}<{@link Map}<{@link String}, {@link DictDetail}>>
     */
    @Operation(summary = "加载字典")
    @GetMapping("/user/dict")
    public R<Map<String, DictDetail>> dict(CodesQo qo) {
        Map<String, DictDetail> map = this.dictService.loadDictDetail(qo);
        return R.success(map);
    }


    /**
     * 分页用户
     *
     * @param qo 请求对象
     * @return {@link R}<{@link Page}<{@link User}>>
     */
    @Operation(summary = "查询用户-分页")
    @GetMapping("/user/page")
    public R<Page<User>> pageUser(PageParam pageParam, UserQo qo) {
        Page<User> page = this.userService.pageUser(pageParam, qo);
        return R.success(page);
    }


    /**
     * 添加用户
     *
     * @param entity 实体
     * @return {@link R}<{@link String}>
     */
    @Operation(summary = "添加用户")
    @PostMapping("/user/add")
    public R<String> addUser(@Validated @RequestBody User entity) {
        this.userService.addUser(entity);
        return R.success();
    }


    /**
     * 更新用户
     *
     * @param entity 实体
     * @return {@link R}<{@link String}>
     */
    @Operation(summary = "更新用户")
    @PutMapping("/user/update")
    public R<String> updateUser(@Validated @RequestBody User entity) {
        this.userService.updateUser(entity);
        return R.success();
    }

    /**
     * 更新用户
     *
     * @param qo 请求对象
     * @return {@link R}<{@link String}>
     */
    @Operation(summary = "重置密码")
    @PutMapping("/user/resetPassword")
    public R<String> updateUser(@Validated @RequestBody ResetPasswordQo qo) {
        this.userService.resetPassword(qo);
        return R.success();
    }


    /**
     * 删除用户
     *
     * @param qo 请求对象
     * @return {@link R}<{@link String}>
     */
    @Operation(summary = "删除用户")
    @DeleteMapping("/user/delete")
    public R<String> deleteUser(@Validated CodeQo qo) {
        this.userService.deleteUser(qo.getCode());
        return R.success();
    }


    @Operation(summary = "分配角色")
    @PutMapping("/user/assignRole")
    public R<String> assignRole(@RequestBody UserAssignRoleQo qo) {
        this.userService.assignRole(qo);
        return R.success();
    }


}
