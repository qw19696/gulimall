package com.zf.gulimall.member.remotequest;

import com.zf.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("coupon")
public interface CouponRmiService {

    @GetMapping("/coupon/coupon/member/list")
    R memberCoupons();
}
