package easier.framework.test;

import cn.dev33.satoken.oauth2.logic.SaOAuth2Template;
import cn.dev33.satoken.oauth2.model.SaClientModel;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.Console;
import cn.hutool.core.thread.ConcurrencyTester;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.spring.EnableSpringUtil;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tangzc.mpe.autotable.EnableAutoTable;
import easier.framework.core.Easier;
import easier.framework.core.plugin.dict.DictDetail;
import easier.framework.core.plugin.dict.DictItemDetail;
import easier.framework.core.plugin.enums.EnumCodec;
import easier.framework.core.plugin.jackson.annotation.ShowDeptDetail;
import easier.framework.core.plugin.jackson.annotation.ShowDictDetail;
import easier.framework.core.plugin.jackson.annotation.ShowUserDetail;
import easier.framework.core.plugin.message.MessageContainer;
import easier.framework.core.util.SpringUtil;
import easier.framework.core.util.StrUtil;
import easier.framework.starter.auth.EnableEasierAuth;
import easier.framework.starter.discovery.EnableEasierDiscovery;
import easier.framework.starter.doc.EnableEasierDoc;
import easier.framework.starter.job.EnableEasierJob;
import easier.framework.starter.logging.EnableEasierLogging;
import easier.framework.starter.mybatis.EnableEasierMybatis;
import easier.framework.starter.mybatis.repo.Repos;
import easier.framework.starter.rpc.EnableEasierRpc;
import easier.framework.starter.web.EnableEasierWeb;
import easier.framework.test.enums.SexType;
import easier.framework.test.eo.Dept;
import easier.framework.test.eo.User;
import easier.framework.test.listener.TestMessageBean;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@MapperScan(annotationClass = Mapper.class)
@SpringBootApplication
@EnableEasierMybatis
@EnableEasierJob
@EnableEasierWeb
@EnableEasierLogging
@EnableEasierAuth
@EnableEasierDoc
@EnableAutoTable(activeProfile = {SpringUtil.dev, SpringUtil.local})
@EnableSpringUtil
@EnableEasierDiscovery
@EnableEasierRpc
public class EasierFrameworkTestApplication extends SaOAuth2Template implements
        ShowDictDetail.ShowDictDetailBean
        , ShowUserDetail.ShowUserDetailBean
        , ShowDeptDetail.ShowDeptDetailBean {
    public static final MessageContainer<User> container = Easier.Message
            .newMessage(User.class)
            .asQueue()
            .name("Easier.Message.test")
            .build();

    public static final TestMessageBean container2 = Easier.Message.build(TestMessageBean.class);

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(EasierFrameworkTestApplication.class, args);
        Map<String, Object> detail = new HashMap<>();
        DictDetail xx = DictDetail.builder()
                .name("1213213")
                .items(CollUtil.newArrayList(DictItemDetail.builder().value("123123").build()))
                .build();
        detail.put("x", xx);
        detail.put("time", DateTime.now());
        ObjectMapper mapper0 = Easier.Json.getObjectMapper();
        String s1 = mapper0.writeValueAsString(detail);
        log.info("");
        TestMessageBean build = Easier.Message.build(TestMessageBean.class);
        build.sendXXX2("", "");
        build.send343(User.builder().build(), detail);
        // 模拟1000个线程并发
        ConcurrencyTester ct = new ConcurrencyTester(1000);
        ct.test(() -> {
            // 需要并发测试的业务代码
            build.sendXXX(Thread.currentThread().getName(), DateTime.now().toString());
        });

        Console.log(ct.getInterval() + "######");
        ct.close();

    }


    // @LoopJob(delay = 2, timeUnit = TimeUnit.SECONDS)
    public void send() {
        User build = User.builder()
                .userId(RandomUtil.randomString(RandomUtil.randomInt(100, 1000)))
                .username(DateTime.now().toString())
                .build();
        container.send(build);
    }

    // @LoopJob(delay = 2, timeUnit = TimeUnit.SECONDS)
    public void producer() {
        User take = container.asConsumer().poll();
        log.info("asConsumer {}", take);
    }

    @Override
    public SaClientModel getClientModel(String clientId) {
        return new SaClientModel()
                .setClientId(clientId)
                .setClientSecret(clientId)
                .setAllowUrl("*")
                .setIsAutoMode(true);
    }

    @Override
    public DictDetail getDictDetail(Object currentValue, String dictCode, JavaType currentType) {
        DictDetail dictDetail = null;
        if (StrUtil.isNotBlank(dictCode)) {
            // dictDetail = UserCenterCaches.DICT_DETAIL.get(dictCode);
        }
        if (dictDetail == null && currentType.getRawClass().isEnum()) {
            EnumCodec<?> codec = EnumCodec.of(currentType.getRawClass());
            if (codec.getDict() != null) {
                // dictDetail = UserCenterCaches.DICT_DETAIL.get(codec.getDict().code());
            }
        }
        if (dictDetail == null) {
            return null;
        }
        if (currentValue == null) {
            return dictDetail;
        }
        if (dictDetail.getItems() == null) {
            return null;
        }
        DictItemDetail selected = dictDetail.getItems()
                .stream()
                .filter(it -> {
                    String value = it.getValue();
                    Class<?> currentValueClass = currentValue.getClass();
                    if (currentValueClass.isEnum()) {
                        EnumCodec<?> codec = EnumCodec.of(currentValueClass);
                        return value.equals(codec.getEnumValue(currentValue));
                    }
                    return value.equals(currentValue.toString());
                })
                .findAny()
                .orElse(null);
        dictDetail.setSelected(selected);
        return dictDetail;

    }

    @Override
    public Object getUserDetail(Object fieldValue) {
        User sysUser = new User();
        sysUser.setUsername(fieldValue.toString());
        sysUser.setSexType(SexType.man);
        return new UserDetail();
    }

    @Override
    public Object getDeptDetail(Object fieldValue) {
        if (fieldValue == null) {
            return new DeptDetail();
        }
        Dept dept = Repos.of(Dept.class).getById(fieldValue.toString());
        if (dept == null) {
            return new DeptDetail();

        }
        return DeptDetail.builder()
                .deptId(dept.getDeptId())
                .deptName(dept.getDeptName())
                .leader(dept.getLeader())
                .email(dept.getEmail())
                .phone(dept.getPhone())
                .status(dept.getStatus())
                .build();
    }
}
