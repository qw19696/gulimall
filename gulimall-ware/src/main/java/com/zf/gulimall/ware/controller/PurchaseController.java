package com.zf.gulimall.ware.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zf.gulimall.ware.entity.vo.MergeVo;
import com.zf.gulimall.ware.entity.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zf.gulimall.ware.entity.PurchaseEntity;
import com.zf.gulimall.ware.service.PurchaseService;
import com.zf.common.utils.PageUtils;
import com.zf.common.utils.R;



/**
 * 采购信息
 *
 * @author zf
 * @email 429558984@qq.com
 * @date 2022-12-08 13:37:15
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("ware:purchase:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 未领取列表
     */
    @RequestMapping("/unreceive/list")
    // @RequiresPermissions("ware:purchase:list")
    public R unreceiveList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageUnreceive(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("ware:purchase:info")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("ware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase){
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
        purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("ware:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("ware:purchase:delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 合并采购单
     */
    @PostMapping("/merge")
    // @RequiresPermissions("ware:purchase:save")
    public R merge(@RequestBody MergeVo purchase){
        purchaseService.mergePurchase(purchase);

        return R.ok();
    }

    /**
     * 领取采购单
     */
    @PostMapping("/received")
    public R receivePurchase(@RequestBody List<Long> ids){
        purchaseService.receive(ids);
        return R.ok();
    }

    /**
     * 结束采购单
     */
    @PostMapping("/done")
    public R donePurchase(@RequestBody PurchaseDoneVo vo){
        purchaseService.done(vo);
        return R.ok();
    }
}
