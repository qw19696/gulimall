package com.zf.common.product.feign;

import com.zf.common.to.SkuReductionTo;
import com.zf.common.to.SpuBoundTo;
import com.zf.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * @author 42955
 */
@FeignClient("coupon")
public interface CouponFeignService {

    @PostMapping("/coupon/spubounds/save")
    R savaSpuBounds(SpuBoundTo spuBoundTo);

    @PostMapping("/coupon/skufullreduction/saveInfo")
    R savaSkuReduction(List<SkuReductionTo> skuReductionTos);
}
