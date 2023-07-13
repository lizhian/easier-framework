package easier.framework.test;

import com.fasterxml.jackson.databind.JavaType;
import com.tangzc.mpe.autotable.EnableAutoTable;
import easier.framework.core.plugin.cache.CacheBuilder;
import easier.framework.core.plugin.dict.DictDetail;
import easier.framework.core.plugin.dict.DictItemDetail;
import easier.framework.core.plugin.dict.ShowDictDetail;
import easier.framework.core.plugin.enums.EnumCodec;
import easier.framework.core.plugin.jackson.annotation.ShowUserDetail;
import easier.framework.core.util.SpringUtil;
import easier.framework.core.util.StrUtil;
import easier.framework.starter.auth.EnableEasierAuth;
import easier.framework.starter.doc.EnableEasierDoc;
import easier.framework.starter.job.EnableEasierJob;
import easier.framework.starter.logging.EnableEasierLogging;
import easier.framework.starter.mybatis.EnableEasierMybatis;
import easier.framework.starter.web.EnableEasierWeb;
import easier.framework.test.cache.DictCache;
import easier.framework.test.enums.SexType;
import easier.framework.test.eo.User;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@MapperScan
@SpringBootApplication
@EnableEasierMybatis
@EnableEasierJob
@EnableEasierWeb
@EnableEasierLogging
@EnableEasierAuth
@EnableEasierDoc
@EnableAutoTable(activeProfile = SpringUtil.dev)
public class EasierFrameworkTestApplication implements
        ShowDictDetail.ShowDetailDetailBean
        , ShowUserDetail.ShowUserDetailBean {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(EasierFrameworkTestApplication.class, args);
    }

    @Override
    public DictDetail getDictDetail(Object currentValue, String dictCode, JavaType currentType) {
        DictCache dictCache = CacheBuilder.build(DictCache.class);
        DictDetail dictDetail = null;
        if (StrUtil.isNotBlank(dictCode)) {
            dictDetail = dictCache.getDictDetail(dictCode);
        }
        if (dictDetail == null && currentType.getRawClass().isEnum()) {
            EnumCodec<?> codec = EnumCodec.of(currentType.getRawClass());
            if (codec.getDict() != null) {
                dictDetail = dictCache.getDictDetail(codec.getDict().code());
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
        return null;
    }
}
