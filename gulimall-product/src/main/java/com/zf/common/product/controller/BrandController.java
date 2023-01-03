package com.zf.common.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.zf.common.product.entity.vo.UploadRequest;
import com.zf.common.product.utils.ImageUtil;
import com.zf.common.product.utils.MinioUtilS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zf.common.product.entity.BrandEntity;
import com.zf.common.product.service.BrandService;
import com.zf.common.utils.PageUtils;
import com.zf.common.utils.R;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;


/**
 * 品牌
 *
 * @author zf
 * @email 429558984@qq.com
 * @date 2022-12-06 20:05:38
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;
    @Autowired
    private MinioUtilS minioUtilS;

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    // @RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:brand:save")
    public R save(@RequestBody @Valid BrandEntity brand){
		brandService.save(brand);

        return R.ok();
    }

    /**
     * 保存
     */
    @PostMapping("/upload/images")
    // @RequiresPermissions("product:brand:save")
    public R saveImages(@RequestBody UploadRequest req){
        try {
            MultipartFile[] file = new MultipartFile[]{ImageUtil.getMultipartFile(req.getImageUrl())};

            List<String> res = minioUtilS.upload(file);
            return R.ok().put("data", res);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:brand:update")
    public R update(@RequestBody BrandEntity brand){
		brandService.updateDetail(brand);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update/status")
    // @RequiresPermissions("product:brand:update")
    public R updateStatus(@RequestBody BrandEntity brand){
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
