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
 * 数据字典信息
 *
 * @author ruoyi
 */
@Tag(name = "字典管理")
@RestController
@RequiredArgsConstructor
public class DictController {
    private final DictService dictService;


    @Operation(summary = "加载字典")
    @GetMapping("/dict/dict")
    public R<Map<String, DictDetail>> dict(CodesQo qo) {
        Map<String, DictDetail> map = dictService.loadDictDetail(qo);
        return R.success(map);
    }


    //@MustPermission("system:dict:list")
    @Operation(summary = "查询字典-分页")
    @GetMapping("/dict/page")
    public R<Page<SysDict>> pageDict(PageParam pageParam, DictQo dictQo) {
        Page<SysDict> page = dictService.pageDict(pageParam, dictQo);
        return R.success(page);
    }

    @Operation(summary = "新增字典")
    @PostMapping("/dict/add")
    public R<String> addDict(@RequestBody SysDict entity) {
        dictService.addDict(entity);
        return R.success();
    }

    @Operation(summary = "修改字典")
    @PutMapping("/dict/update")
    public R<String> updateDict(@RequestBody SysDict entity) {
        dictService.updateDict(entity);
        return R.success();
    }

    @Operation(summary = "删除字典")
    @DeleteMapping("/dict/delete")
    public R<String> deleteDict(IdQo qo) {
        dictService.deleteDict(qo);
        return R.success();
    }

    @Operation(summary = "查询字典项-列表")
    @GetMapping("/dict/item/list")
    public R<List<SysDictItem>> listDictItem(DictItemQo qo) {
        List<SysDictItem> list = dictService.listDictItem(qo);
        return R.success(list);
    }

    @Operation(summary = "新增字典项")
    @PostMapping("/dict/item/add")
    public R<String> addDictItem(@RequestBody SysDictItem entity) {
        dictService.addDictItem(entity);
        return R.success();
    }

    @Operation(summary = "修改字典项")
    @PutMapping("/dict/item/update")
    public R<String> updateDictItem(@RequestBody SysDictItem entity) {
        dictService.updateDictItem(entity);
        return R.success();
    }

    @Operation(summary = "删除字典项")
    @DeleteMapping("/dict/item/delete")
    public R<String> deleteDictItem(IdQo qo) {
        dictService.deleteDictItem(qo);
        return R.success();
    }
}
