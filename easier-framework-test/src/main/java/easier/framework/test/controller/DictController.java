package easier.framework.test.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import easier.framework.core.domain.CodesQo;
import easier.framework.core.domain.IdQo;
import easier.framework.core.domain.PageParam;
import easier.framework.core.domain.R;
import easier.framework.core.plugin.dict.DictDetail;
import easier.framework.test.eo.SysDict;
import easier.framework.test.eo.SysDictItem;
import easier.framework.test.qo.DictItemQo;
import easier.framework.test.qo.DictQo;
import easier.framework.test.service.DictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * 字典控制器
 *
 * @author lizhian
 * @date 2023年07月05日
 */
@Tag(name = "字典管理")
@RestController
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    /**
     * 加载字典
     *
     * @param qo 请求对象
     * @return {@link R}<{@link Map}<{@link String}, {@link DictDetail}>>
     */
    @Operation(summary = "加载字典")
    @GetMapping("/dict/dict")
    public R<Map<String, DictDetail>> dict(CodesQo qo) {
        Map<String, DictDetail> map = this.dictService.loadDictDetail(qo);
        return R.success(map);
    }

    /**
     * 查询字典-分页
     *
     * @param pageParam 分页参数
     * @param dictQo    字典请求对象
     * @return {@link R}<{@link Page}<{@link SysDict}>>
     */
    @Operation(summary = "查询字典-分页")
    @GetMapping("/dict/page")
    public R<Page<SysDict>> pageDict(PageParam pageParam, DictQo dictQo) {
        Page<SysDict> page = this.dictService.pageDict(pageParam, dictQo);
        return R.success(page);
    }

    /**
     * 添加字典
     *
     * @param entity 实体
     * @return {@link R}<{@link String}>
     */
    @Operation(summary = "添加字典")
    @PostMapping("/dict/add")
    public R<String> addDict(@RequestBody SysDict entity) {
        this.dictService.addDict(entity);
        return R.success();
    }

    /**
     * 更新字典
     *
     * @param entity 实体
     * @return {@link R}<{@link String}>
     */
    @Operation(summary = "更新字典")
    @PutMapping("/dict/update")
    public R<String> updateDict(@RequestBody SysDict entity) {
        this.dictService.updateDict(entity);
        return R.success();
    }

    /**
     * 删除字典
     *
     * @param qo 请求对象
     * @return {@link R}<{@link String}>
     */
    @Operation(summary = "删除字典")
    @DeleteMapping("/dict/delete")
    public R<String> deleteDict(IdQo qo) {
        this.dictService.deleteDict(qo);
        return R.success();
    }

    /**
     * 查询字典项-列表
     *
     * @param qo 请求对象
     * @return {@link R}<{@link List}<{@link SysDictItem}>>
     */
    @Operation(summary = "查询字典项-列表")
    @GetMapping("/dict/item/list")
    public R<List<SysDictItem>> listDictItem(DictItemQo qo) {
        List<SysDictItem> list = this.dictService.listDictItem(qo);
        return R.success(list);
    }

    /**
     * 添加字典项
     *
     * @param entity 实体
     * @return {@link R}<{@link String}>
     */
    @Operation(summary = "添加字典项")
    @PostMapping("/dict/item/add")
    public R<String> addDictItem(@RequestBody SysDictItem entity) {
        this.dictService.addDictItem(entity);
        return R.success();
    }

    /**
     * 更新字典项
     *
     * @param entity 实体
     * @return {@link R}<{@link String}>
     */
    @Operation(summary = "更新字典项")
    @PutMapping("/dict/item/update")
    public R<String> updateDictItem(@RequestBody SysDictItem entity) {
        this.dictService.updateDictItem(entity);
        return R.success();
    }

    /**
     * 删除字典项
     *
     * @param qo 请求对象
     * @return {@link R}<{@link String}>
     */
    @Operation(summary = "删除字典项")
    @DeleteMapping("/dict/item/delete")
    public R<String> deleteDictItem(IdQo qo) {
        this.dictService.deleteDictItem(qo);
        return R.success();
    }
}
