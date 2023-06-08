package tydic.framework.test.service;

import org.springframework.stereotype.Component;
import tydic.framework.test.SysAppMapper;

import javax.annotation.Resource;

@Component
public class TestService {

    @Resource
    private SysAppMapper sysAppMapper;

    public void test() {


    }
}
