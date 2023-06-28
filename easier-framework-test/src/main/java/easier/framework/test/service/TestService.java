package easier.framework.test.service;

import easier.framework.test.SysAppMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class TestService {

    @Resource
    private SysAppMapper sysAppMapper;

    public void test() {


    }
}
