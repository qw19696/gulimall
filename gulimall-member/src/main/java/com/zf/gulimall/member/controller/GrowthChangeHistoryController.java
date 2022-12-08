package com.zf.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.zf.gulimall.member.remotequest.CouponRmiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zf.gulimall.member.entity.GrowthChangeHistoryEntity;
import com.zf.gulimall.member.service.GrowthChangeHistoryService;
import com.zf.common.utils.PageUtils;
import com.zf.common.utils.R;



/**
 * 成长值变化历史记录
 *
 * @author zf
 * @email 429558984@qq.com
 * @date 2022-12-08 11:12:28
 */
@RestController
@RequestMapping("member/growthchangehistory")
public class GrowthChangeHistoryController {

    @Autowired
    private CouponRmiService couponRmiService;

    @Autowired
    private GrowthChangeHistoryService growthChangeHistoryService;

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("member:growthchangehistory:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = growthChangeHistoryService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("member:growthchangehistory:info")
    public R info(@PathVariable("id") Long id){
		GrowthChangeHistoryEntity growthChangeHistory = growthChangeHistoryService.getById(id);

        return R.ok().put("growthChangeHistory", growthChangeHistory);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("member:growthchangehistory:save")
    public R save(@RequestBody GrowthChangeHistoryEntity growthChangeHistory){
		growthChangeHistoryService.save(growthChangeHistory);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("member:growthchangehistory:update")
    public R update(@RequestBody GrowthChangeHistoryEntity growthChangeHistory){
		growthChangeHistoryService.updateById(growthChangeHistory);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("member:growthchangehistory:delete")
    public R delete(@RequestBody Long[] ids){
		growthChangeHistoryService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @GetMapping("/test")
    public R test(){
        return R.ok().put("请求成功",couponRmiService.memberCoupons());
    }
}
