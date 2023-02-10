package com.zf.common.product.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.zf.common.product.entity.AttrEntity;
import com.zf.common.product.entity.vo.AttrGroupRelationVo;
import com.zf.common.product.entity.vo.AttrVo;
import com.zf.common.product.service.AttrAttrgroupRelationService;
import com.zf.common.product.service.AttrService;
import com.zf.common.product.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import com.zf.common.product.entity.AttrGroupEntity;
import com.zf.common.product.service.AttrGroupService;
import com.zf.common.utils.PageUtils;
import com.zf.common.utils.R;



/**
 * 属性分组
 *
 * @author zf
 * @email 429558984@qq.com
 * @date 2022-12-06 20:05:39
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService relationService;

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
   // @RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params, @PathVariable("catelogId") Long id){
        PageUtils page = attrGroupService.queryPage(params, id);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    // @RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        Long[] cateLogPath = categoryService.getCateLogPath(catelogId);
        attrGroup.setCatelogPath(cateLogPath);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 根据分类Id获取旗下所有属性分组的所有属性
     * @param cateId
     * @return
     */
    @GetMapping("/{cateLogId}/withattr")
    public R getAttGroupWithAttrs(@PathVariable("cateLogId")Long cateId){
        List<AttrEntity> attrs = attrGroupService.getAttrByCateLogId(cateId);
        List<AttrVo> res = new ArrayList<>();
        if (!CollectionUtils.isEmpty(attrs)){
            res = attrs.stream()
                    .map(attr ->{
                        AttrVo vo = new AttrVo();
                        BeanUtils.copyProperties(attr, vo);
                        return vo;
                    })
                    .collect(Collectors.toList());
        }
        return R.ok().put("data", res);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    @GetMapping("{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") Long groupId){
        List<AttrEntity> res = attrService.getRelationAttr(groupId);
        return R.ok().put("data",res);
    }

    @PostMapping("/attr/relation/delete")
    public R attrRelationDelete(@RequestBody AttrGroupRelationVo[] req){
        attrService.deleteRelation(req);
        return R.ok();
    }

    @GetMapping("{attrgroupId}/noattr/relation")
    public R attrNoRelation(@PathVariable("attrgroupId") Long groupId,
                            @RequestParam Map<String, Object> params){
        PageUtils res = attrService.getNoRelationAttr(groupId,params);
        return R.ok().put("page",res);
    }

    @PostMapping("/attr/relation")
    public R attrRelation(@RequestBody List<AttrGroupRelationVo> relationVos){
        relationService.saveBatch(relationVos);
        return R.ok();
    }
}
