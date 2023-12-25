package easier.framework.test.controller;

import easier.framework.core.Easier;
import easier.framework.core.domain.R;
import easier.framework.starter.mybatis.repo.Repos;
import easier.framework.test.eo.App;
import easier.framework.test.eo.User;
import easier.framework.test.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 登录控制器
 *
 * @author lizhian
 * @date 2023年07月26日
 */
@Slf4j
@Tag(name = "首页")
@RestController
@RequiredArgsConstructor
public class IndexController {
    private final LoginService loginService;


    @Operation(summary = "用户详情")
    @GetMapping("/index/user")
    public R<User> login() {
        String account = Easier.Auth.getAccount();
        return R.success(Repos.of(User.class).getByCode(account));
    }

    @Operation(summary = "test")
    @GetMapping("/index/test")
    public R<String[]> test(String[] list, String aa) {
        return R.success(list);
    }


    @Operation(summary = "应用列表")
    @GetMapping("/index/app/list")
    public R<List<App>> appList() {
        return R.success(Repos.of(App.class).listAll());
    }


}
