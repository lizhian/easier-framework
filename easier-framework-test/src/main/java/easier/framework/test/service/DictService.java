package easier.framework.test.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import easier.framework.core.domain.CodesQo;
import easier.framework.core.domain.IdQo;
import easier.framework.core.domain.PageParam;
import easier.framework.core.plugin.dict.Dict;
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
import easier.framework.test.eo.SysDict;
import easier.framework.test.eo.SysDictItem;
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
    private final Repo<SysDict> _sys_dict = Repos.of(SysDict.class);
    private final Repo<SysDictItem> _sys_dict_item = Repos.of(SysDictItem.class);

    /**
     * 更新枚举字典
     *
     * @param event 事件
     */
    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String packageName = EasierFrameworkTestApplication.class.getPackage().getName();
        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation(packageName, Dict.class);
        for (Class<?> clazz : classes) {
            if (!clazz.isEnum()) {
                continue;
            }
            EnumCodec<?> codec = EnumCodec.of(clazz);
            Dict dict = codec.getDict();
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
            SysDict oldDict = this._sys_dict.getByCode(dictCode);
            if (oldDict == null) {
                SysDict newDict = SysDict.builder()
                        .dictCode(dictCode)
                        .dictName(dictName)
                        .dictType(DictType.enumDict)
                        .status(EnableStatus.enable)
                        .remark(remark)
                        .build();
                this._sys_dict.add(newDict);
            } else {
                SysDict newDict = oldDict.toBuilder()
                        .dictCode(dictCode)
                        .dictName(dictName)
                        .dictType(DictType.enumDict)
                        .status(EnableStatus.enable)
                        .remark(remark)
                        .build();
                this._sys_dict.update(newDict);
            }
            for (int index = 0; index < codec.getEnumDetails().size(); index++) {
                EnumDetail<?> enumDetail = codec.getEnumDetails().get(index);
                String value = enumDetail.getValueAsStr();
                SysDictItem oldItem = this._sys_dict_item.newQuery()
                        .eq(SysDictItem::getDictCode, dictCode)
                        .eq(SysDictItem::getValue, value)
                        .any();
                if (oldItem == null) {
                    SysDictItem item = SysDictItem.builder()
                            .dictCode(dictCode)
                            .value(value)
                            .label(enumDetail.getDescription())
                            .sort(index)
                            .style(enumDetail.getDictProperty1())
                            .status(EnableStatus.enable)
                            .build();
                    this._sys_dict_item.add(item);
                } else {
                    SysDictItem item = oldItem.toBuilder()
                            .dictCode(dictCode)
                            .value(value)
                            .label(enumDetail.getDescription())
                            .sort(index)
                            .style(enumDetail.getDictProperty1())
                            .status(EnableStatus.enable)
                            .build();
                    this._sys_dict_item.update(item);
                }
            }
            List<String> values = codec.getEnumDetails()
                    .stream()
                    .map(EnumDetail::getValueAsStr)
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.toList());
            if (CollUtil.isNotEmpty(values)) {
                this._sys_dict_item.newUpdate()
                        .eq(SysDictItem::getDictCode, dictCode)
                        .notIn(SysDictItem::getValue, values)
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
        return this._sys_dict.withBind()
                .listByCodes(qo.getCodes())
                .stream()
                .map(SysDict::toDictDetail)
                .collect(Collectors.toMap(DictDetail::getCode, it -> it));
    }


    /**
     * 分页字典
     *
     * @param pageParam 分页参数
     * @param dictQo    字典查询对象
     * @return {@link Page}<{@link SysDict}>
     */
    public Page<SysDict> pageDict(PageParam pageParam, DictQo dictQo) {
        return this._sys_dict.newQuery()
                //.bind()
                .whenNotBlank()
                .like(SysDict::getDictName, dictQo.getDictName())
                .like(SysDict::getDictCode, dictQo.getDictCode())
                .whenNotNull()
                .eq(SysDict::getDictType, dictQo.getDictType())
                .eq(SysDict::getStatus, dictQo.getStatus())
                .end()
                .orderByDesc(SysDict::getUpdateTime)
                .page(pageParam.toPage());
    }


    /**
     * 列表字典项
     *
     * @param qo 查询对象
     * @return {@link List}<{@link SysDictItem}>
     */
    public List<SysDictItem> listDictItem(DictItemQo qo) {
        ValidUtil.valid(qo);
        return this._sys_dict_item.newQuery()
                //.bind()
                .eq(SysDictItem::getDictCode, qo.getDictCode())
                .whenNotBlank()
                .like(SysDictItem::getLabel, qo.getLabel())
                .like(SysDictItem::getValue, qo.getValue())
                .whenNotNull()
                .eq(SysDictItem::getStatus, qo.getStatus())
                .end()
                .orderByAsc(SysDictItem::getSort)
                .list();
    }

    /**
     * 添加字典
     *
     * @param entity 实体
     */
    public void addDict(SysDict entity) {
        ValidUtil.valid(entity);
        String dictCode = entity.getDictCode();
        if (this._sys_dict.existsByCode(dictCode)) {
            throw BizException.of("重复的字典编码:{}", dictCode);
        }
        String dictName = entity.getDictName();
        if (this._sys_dict.isNotUnique(SysDict::getDictName, dictName)) {
            throw BizException.of("重复的字典名称:{}", dictCode);
        }
        entity.setDictType(DictType.bizDict);
        this._sys_dict.add(entity);
    }

    /**
     * 更新字典
     *
     * @param entity 实体
     */
    public void updateDict(SysDict entity) {
        ValidUtil.validOnUpdate(entity);
        String dictId = entity.getDictId();
        SysDict old = this._sys_dict.getById(dictId);
        if (old == null) {
            throw BizException.of("无效的字典主键:{}", dictId);
        }
        //枚举字典只能更新
        //字典样式
        if (DictType.enumDict.equals(entity.getDictType())) {
            old.setStyle(entity.getStyle());
            this._sys_dict.update(old);
            return;
        }
        String dictName = entity.getDictName();
        if (this._sys_dict.isNotUnique(SysDict::getDictName, dictName, dictId)) {
            throw BizException.of("重复的字典名称:{}", dictName);
        }
        //业务字典只能更新
        //字典名称
        //状态
        //字典样式
        //备注
        old.setDictName(entity.getDictCode());
        old.setStatus(entity.getStatus());
        old.setStyle(entity.getStyle());
        old.setRemark(entity.getRemark());
        this._sys_dict.update(old);
    }

    /**
     * 删除字典
     *
     * @param qo 请求对象
     */
    @Transactional
    public void deleteDict(IdQo qo) {
        ValidUtil.valid(qo);
        String id = qo.getId();
        SysDict old = this._sys_dict.getById(id);
        if (old == null) {
            throw BizException.of("无效的字典主键:{}", id);
        }
        if (DictType.enumDict.equals(old.getDictType())) {
            throw BizException.of("枚举字典不允许删除");
        }
        this._sys_dict.deleteById(id);
        this._sys_dict_item.deleteBy(SysDictItem::getDictCode, old.getDictCode());
    }

    /**
     * 添加字典项
     *
     * @param entity 实体
     */
    public void addDictItem(SysDictItem entity) {
        ValidUtil.valid(entity);
        String dictCode = entity.getDictCode();
        String label = entity.getLabel();
        String value = entity.getValue();
        SysDict dict = this._sys_dict.getByCode(dictCode);
        if (dict == null) {
            throw BizException.of("无效的字典编码:{}", dictCode);
        }
        if (!DictType.bizDict.equals(dict.getDictType())) {
            throw BizException.of("{}不是业务字典,不允许新增字典项", dictCode);
        }
        boolean sameLabel = this._sys_dict_item.newQuery()
                .eq(SysDictItem::getDictCode, dictCode)
                .eq(SysDictItem::getLabel, label)
                .exists();
        if (sameLabel) {
            throw BizException.of("重复的字典项标签:{}", dictCode);
        }
        boolean sameValue = this._sys_dict_item.newQuery()
                .eq(SysDictItem::getDictCode, dictCode)
                .eq(SysDictItem::getValue, value)
                .exists();
        if (sameValue) {
            throw BizException.of("重复的字典项键值:{}", dictCode);
        }
        this._sys_dict_item.add(entity);
    }


    /**
     * 更新字典项
     *
     * @param entity 实体
     */
    public void updateDictItem(SysDictItem entity) {
        ValidUtil.validOnUpdate(entity);
        String dictItemId = entity.getDictItemId();
        SysDictItem old = this._sys_dict_item.getById(dictItemId);
        if (old == null) {
            throw BizException.of("无效的字典项主键:{}", dictItemId);
        }
        String dictCode = old.getDictCode();
        SysDict dict = this._sys_dict.getByCode(dictCode);
        if (dict == null) {
            throw BizException.of("无效的字典编码:{}", dictCode);
        }
        //枚举字典的字典项只能更新字典项样式和备注
        if (DictType.enumDict.equals(dict.getDictType())) {
            old.setStyle(entity.getStyle());
            old.setRemark(entity.getRemark());
            this._sys_dict_item.update(old);
            return;
        }
        String label = entity.getLabel();
        boolean sameLabel = this._sys_dict_item.newQuery()
                .eq(SysDictItem::getDictCode, dictCode)
                .eq(SysDictItem::getLabel, label)
                .ne(SysDictItem::getDictItemId, dictItemId)
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
        old.setLabel(entity.getLabel());
        old.setStatus(entity.getStatus());
        old.setSort(entity.getSort());
        old.setStyle(entity.getStyle());
        old.setRemark(entity.getRemark());
        this._sys_dict_item.update(old);
    }


    /**
     * 删除字典项
     *
     * @param qo 查询对象
     */
    public void deleteDictItem(IdQo qo) {
        ValidUtil.valid(qo);
        String id = qo.getId();
        SysDictItem old = this._sys_dict_item.getById(id);
        if (old == null) {
            throw BizException.of("无效的字典项主键:{}", id);
        }
        String dictCode = old.getDictCode();
        if (StrUtil.isNotBlank(dictCode)) {
            SysDict dict = this._sys_dict.getByCode(dictCode);
            if (dict != null && DictType.enumDict.equals(dict.getDictType())) {
                throw BizException.of("枚举字典的字典项不允许删除");
            }
        }
        this._sys_dict_item.deleteById(id);
    }
}
