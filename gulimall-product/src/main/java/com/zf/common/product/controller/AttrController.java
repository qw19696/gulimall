package com.zf.common.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.zf.common.product.entity.vo.AttrResponseVo;
import com.zf.common.product.entity.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zf.common.product.entity.AttrEntity;
import com.zf.common.product.service.AttrService;
import com.zf.common.utils.PageUtils;
import com.zf.common.utils.R;



/**
 * 商品属性
 *
 * @author zf
 * @email 429558984@qq.com
 * @date 2022-12-06 20:05:39
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    /**
     * 规格参数或销售属性list
     * @param params
     * @param cateLogId
     * @return
     */
    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String, Object> params,
                          @PathVariable("catelogId") Long cateLogId,
                          @PathVariable("attrType") String type){
        PageUtils page = attrService.queryBaseAttrPage(params, cateLogId, type);
        return R.ok().put("page", page);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    // @RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
		AttrResponseVo attr = attrService.getAttrInfo(attrId);

        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr){
		attrService.saveAttr(attr);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attr){
		attrService.updateAttr(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
