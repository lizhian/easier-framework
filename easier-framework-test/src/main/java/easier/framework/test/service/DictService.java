package easier.framework.test.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import easier.framework.core.domain.CodesQo;
import easier.framework.core.domain.PageParam;
import easier.framework.core.plugin.dict.DictDetail;
import easier.framework.core.plugin.enums.EnumCodec;
import easier.framework.core.plugin.enums.EnumDetail;
import easier.framework.core.plugin.exception.biz.BizException;
import easier.framework.core.util.ClassUtil;
import easier.framework.core.util.StrUtil;
import easier.framework.core.util.ValidUtil;
import easier.framework.starter.mybatis.repo.Repo;
import easier.framework.starter.mybatis.repo.Repos;
import easier.framework.test.EasierFrameworkTestApplication;
import easier.framework.test.enums.DictType;
import easier.framework.test.enums.EnableStatus;
import easier.framework.test.eo.Dict;
import easier.framework.test.eo.DictItem;
import easier.framework.test.qo.DictItemQo;
import easier.framework.test.qo.DictQo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 字典服务
 *
 * @author lizhian
 * @date 2023年07月05日
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DictService {
    private final Repo<Dict> _dict = Repos.of(Dict.class);
    private final Repo<DictItem> _dict_item = Repos.of(DictItem.class);

    /**
     * 更新枚举字典
     *
     * @param event 事件
     */
    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String packageName = EasierFrameworkTestApplication.class.getPackage().getName();
        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation(packageName, easier.framework.core.plugin.dict.Dict.class);
        for (Class<?> clazz : classes) {
            if (!clazz.isEnum()) {
                continue;
            }
            EnumCodec<?> codec = EnumCodec.of(clazz);
            easier.framework.core.plugin.dict.Dict dict = codec.getDict();
            if (dict == null) {
                continue;
            }
            String dictCode = dict.code();
            if (StrUtil.isBlank(dictCode)) {
                continue;
            }
            String dictName = dict.name();
            String remark = dict.remark();
            //有则更新,无则新增
            Dict oldDict = this._dict.getByCode(dictCode);
            if (oldDict == null) {
                Dict newDict = Dict.builder()
                        .dictCode(dictCode)
                        .dictName(dictName)
                        .dictType(DictType.enumDict)
                        .status(EnableStatus.enable)
                        .remark(remark)
                        .build();
                this._dict.add(newDict);
            } else {
                Dict newDict = oldDict.toBuilder()
                        .dictCode(dictCode)
                        .dictName(dictName)
                        .dictType(DictType.enumDict)
                        .status(EnableStatus.enable)
                        .remark(remark)
                        .build();
                this._dict.update(newDict);
            }
            for (int index = 0; index < codec.getEnumDetails().size(); index++) {
                EnumDetail<?> enumDetail = codec.getEnumDetails().get(index);
                String value = enumDetail.getValueAsStr();
                DictItem oldItem = this._dict_item.newQuery()
                        .eq(DictItem::getDictCode, dictCode)
                        .eq(DictItem::getValue, value)
                        .any();
                if (oldItem == null) {
                    DictItem item = DictItem.builder()
                            .dictCode(dictCode)
                            .value(value)
                            .label(enumDetail.getDescription())
                            .sort(index)
                            .style(enumDetail.getDictProperty1())
                            .status(EnableStatus.enable)
                            .build();
                    this._dict_item.add(item);
                } else {
                    DictItem item = oldItem.toBuilder()
                            .dictCode(dictCode)
                            .value(value)
                            .label(enumDetail.getDescription())
                            .sort(index)
                            .style(enumDetail.getDictProperty1())
                            .status(EnableStatus.enable)
                            .build();
                    this._dict_item.update(item);
                }
            }
            List<String> values = codec.getEnumDetails()
                    .stream()
                    .map(EnumDetail::getValueAsStr)
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.toList());
            if (CollUtil.isNotEmpty(values)) {
                this._dict_item.newUpdate()
                        .eq(DictItem::getDictCode, dictCode)
                        .notIn(DictItem::getValue, values)
                        .remove();
            }
            log.info("已更新枚举字典:{},{}", dictCode, clazz.getName());
        }
    }


    /**
     * 加载字典详情
     *
     * @param qo 查询对象
     * @return {@link Map}<{@link String}, {@link DictDetail}>
     */
    public Map<String, DictDetail> loadDictDetail(CodesQo qo) {
        ValidUtil.valid(qo);
        List<String> codes = qo.getCodes();
        Map<String, DictDetail> result = this._dict.withBind()
                .listByCodes(codes)
                .stream()
                .map(Dict::toDictDetail)
                .collect(Collectors.toMap(DictDetail::getCode, it -> it));
        for (String code : codes) {
            if (result.containsKey(code)) {
                continue;
            }
            result.put(code, new DictDetail());
        }
        return result;
    }


    /**
     * 分页字典
     *
     * @param pageParam 分页参数
     * @param dictQo    字典查询对象
     * @return {@link Page}<{@link Dict}>
     */
    public Page<Dict> pageDict(PageParam pageParam, DictQo dictQo) {
        return this._dict.newQuery()
                .whenNotBlank()
                .like(Dict::getDictName, dictQo.getDictName())
                .like(Dict::getDictCode, dictQo.getDictCode())
                .whenNotNull()
                .eq(Dict::getDictType, dictQo.getDictType())
                .eq(Dict::getStatus, dictQo.getStatus())
                .end()
                .orderByDesc(Dict::getUpdateTime)
                .page(pageParam.toPage());
    }


    /**
     * 列表字典项
     *
     * @param qo 查询对象
     * @return {@link List}<{@link DictItem}>
     */
    public List<DictItem> listDictItem(DictItemQo qo) {
        ValidUtil.valid(qo);
        return this._dict_item.newQuery()
                .eq(DictItem::getDictCode, qo.getDictCode())
                .whenNotBlank()
                .like(DictItem::getLabel, qo.getLabel())
                .like(DictItem::getValue, qo.getValue())
                .whenNotNull()
                .eq(DictItem::getStatus, qo.getStatus())
                .end()
                .orderByAsc(DictItem::getSort)
                .list();
    }

    /**
     * 添加字典
     *
     * @param entity 实体
     */
    public void addDict(Dict entity) {
        ValidUtil.valid(entity);
        String dictCode = entity.getDictCode();
        if (this._dict.existsByCode(dictCode)) {
            throw BizException.of("重复的字典编码:{}", dictCode);
        }
        String dictName = entity.getDictName();
        if (this._dict.existsBy(Dict::getDictName, dictName)) {
            throw BizException.of("重复的字典名称:{}", dictName);
        }
        entity.setDictType(DictType.bizDict);
        this._dict.add(entity);
    }

    /**
     * 更新字典
     *
     * @param entity 实体
     */
    public void updateDict(Dict entity) {
        ValidUtil.validOnUpdate(entity);
        String dictId = entity.getDictId();
        Dict old = this._dict.getById(dictId);
        if (old == null) {
            throw BizException.of("无效的字典主键:{}", dictId);
        }
        //枚举字典只能更新
        //字典样式/备注
        if (DictType.isEnumDict(entity.getDictType())) {
            if (EnableStatus.isDisable(entity.getStatus())) {
                throw BizException.of("不允许禁用枚举字典");
            }
            this._dict.newUpdate()
                    .set(Dict::getStyle, entity.getStyle())
                    .set(Dict::getRemark, entity.getRemark())
                    .updateById(dictId);
            return;
        }
        String dictName = entity.getDictName();
        if (this._dict.isNotUnique(Dict::getDictName, dictName, dictId)) {
            throw BizException.of("重复的字典名称:{}", dictName);
        }
        //业务字典只能更新
        //字典名称/状态/字典样式/备注
        this._dict.newUpdate()
                .set(Dict::getDictName, entity.getDictName())
                .set(Dict::getStatus, entity.getStatus())
                .set(Dict::getStyle, entity.getStyle())
                .set(Dict::getRemark, entity.getRemark())
                .updateById(dictId);
    }


    /**
     * 删除字典
     *
     * @param dictCode 字典代码
     */
    @Transactional
    public void deleteDict(String dictCode) {
        Dict old = this._dict.getByCode(dictCode);
        if (old == null) {
            throw BizException.of("无效的字典编码:{}", dictCode);
        }
        if (DictType.isEnumDict(old.getDictType())) {
            throw BizException.of("枚举字典不允许删除");
        }
        this._dict.deleteByCode(dictCode);
        this._dict_item.deleteBy(DictItem::getDictCode, dictCode);
    }

    /**
     * 添加字典项
     *
     * @param entity 实体
     */
    public void addDictItem(DictItem entity) {
        ValidUtil.valid(entity);
        String dictCode = entity.getDictCode();
        String label = entity.getLabel();
        String value = entity.getValue();
        Dict dict = this._dict.getByCode(dictCode);
        if (dict == null) {
            throw BizException.of("无效的字典编码:{}", dictCode);
        }
        if (!DictType.isBizDict(dict.getDictType())) {
            throw BizException.of("{}不是业务字典,不允许新增字典项", dictCode);
        }
        boolean sameLabel = this._dict_item.newQuery()
                .eq(DictItem::getDictCode, dictCode)
                .eq(DictItem::getLabel, label)
                .exists();
        if (sameLabel) {
            throw BizException.of("重复的字典项标签:{}", dictCode);
        }
        boolean sameValue = this._dict_item.newQuery()
                .eq(DictItem::getDictCode, dictCode)
                .eq(DictItem::getValue, value)
                .exists();
        if (sameValue) {
            throw BizException.of("重复的字典项键值:{}", value);
        }
        this._dict_item.add(entity);
    }


    /**
     * 更新字典项
     *
     * @param entity 实体
     */
    public void updateDictItem(DictItem entity) {
        ValidUtil.validOnUpdate(entity);
        String dictItemId = entity.getDictItemId();
        DictItem old = this._dict_item.getById(dictItemId);
        if (old == null) {
            throw BizException.of("无效的字典项主键:{}", dictItemId);
        }

        String dictCode = old.getDictCode();
        Dict dict = this._dict.getByCode(dictCode);
        if (dict == null) {
            throw BizException.of("无效的字典编码:{}", dictCode);
        }
        //枚举字典的字典项只能更新字典项样式和备注
        if (DictType.isEnumDict(dict.getDictType())) {
            if (EnableStatus.isDisable(entity.getStatus())) {
                throw BizException.of("不允许禁用枚举字典的字典项");
            }
            this._dict_item.newUpdate()
                    .set(DictItem::getStyle, entity.getStyle())
                    .set(DictItem::getRemark, entity.getRemark())
                    .updateById(dictItemId);
            return;
        }
        String label = entity.getLabel();
        boolean sameLabel = this._dict_item.newQuery()
                .eq(DictItem::getDictCode, dictCode)
                .eq(DictItem::getLabel, label)
                .ne(DictItem::getDictItemId, dictItemId)
                .exists();
        if (sameLabel) {
            throw BizException.of("重复的字典项标签:{}", label);
        }
        //只能更新
        //字典项标签
        //状态
        //排序
        //字典项样式
        //备注
        this._dict_item.newUpdate()
                .set(DictItem::getLabel, entity.getLabel())
                .set(DictItem::getStatus, entity.getStatus())
                .set(DictItem::getSort, entity.getSort())
                .set(DictItem::getStyle, entity.getStyle())
                .set(DictItem::getRemark, entity.getRemark())
                .updateById(dictItemId);
    }


    /**
     * 删除字典项
     *
     * @param qo 查询对象
     */
    public void deleteDictItem(String dictItemId) {
        DictItem old = this._dict_item.getById(dictItemId);
        if (old == null) {
            throw BizException.of("无效的字典项主键:{}", dictItemId);
        }
        String dictCode = old.getDictCode();
        if (StrUtil.isNotBlank(dictCode)) {
            Dict dict = this._dict.getByCode(dictCode);
            if (dict != null && DictType.isEnumDict(dict.getDictType())) {
                throw BizException.of("枚举字典的字典项不允许删除");
            }
        }
        this._dict_item.deleteById(dictItemId);
    }
}
