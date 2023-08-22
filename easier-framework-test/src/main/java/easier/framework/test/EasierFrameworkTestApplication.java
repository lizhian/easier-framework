package easier.framework.test;

import cn.dev33.satoken.oauth2.logic.SaOAuth2Template;
import cn.dev33.satoken.oauth2.model.SaClientModel;
import cn.hutool.extra.spring.EnableSpringUtil;
import com.fasterxml.jackson.databind.JavaType;
import com.tangzc.mpe.autotable.EnableAutoTable;
import easier.framework.core.plugin.dict.DictDetail;
import easier.framework.core.plugin.dict.DictItemDetail;
import easier.framework.core.plugin.enums.EnumCodec;
import easier.framework.core.plugin.jackson.annotation.ShowDeptDetail;
import easier.framework.core.plugin.jackson.annotation.ShowDictDetail;
import easier.framework.core.plugin.jackson.annotation.ShowUserDetail;
import easier.framework.core.util.SpringUtil;
import easier.framework.core.util.StrUtil;
import easier.framework.starter.auth.EnableEasierAuth;
import easier.framework.starter.discovery.EnableEasierDiscovery;
import easier.framework.starter.doc.EnableEasierDoc;
import easier.framework.starter.job.EnableEasierJob;
import easier.framework.starter.logging.EnableEasierLogging;
import easier.framework.starter.mybatis.EnableEasierMybatis;
import easier.framework.starter.mybatis.repo.Repo;
import easier.framework.starter.mybatis.repo.Repos;
import easier.framework.starter.rpc.EnableEasierRpc;
import easier.framework.starter.web.EnableEasierWeb;
import easier.framework.test.cache.UserCenterCaches;
import easier.framework.test.enums.SexType;
import easier.framework.test.eo.Dept;
import easier.framework.test.eo.MetadataJcdManagerEntity;
import easier.framework.test.eo.User;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@Slf4j
@MapperScan(annotationClass = Mapper.class)
@SpringBootApplication
@EnableEasierMybatis
@EnableEasierJob
@EnableEasierWeb
@EnableEasierLogging
@EnableEasierAuth
@EnableEasierDoc
@EnableAutoTable(activeProfile = SpringUtil.dev)
@EnableSpringUtil
@EnableEasierDiscovery
@EnableEasierRpc
public class EasierFrameworkTestApplication extends SaOAuth2Template implements
        ShowDictDetail.ShowDictDetailBean
        , ShowUserDetail.ShowUserDetailBean
        , ShowDeptDetail.ShowDeptDetailBean {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(EasierFrameworkTestApplication.class, args);
        Repo<MetadataJcdManagerEntity> repo = Repos.of(MetadataJcdManagerEntity.class);
        List<MetadataJcdManagerEntity> metadataJcdManagerEntities = repo.listAll();
        log.info("");
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
            dictDetail = UserCenterCaches.DICT_DETAIL.get(dictCode);
        }
        if (dictDetail == null && currentType.getRawClass().isEnum()) {
            EnumCodec<?> codec = EnumCodec.of(currentType.getRawClass());
            if (codec.getDict() != null) {
                dictDetail = UserCenterCaches.DICT_DETAIL.get(codec.getDict().code());
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
