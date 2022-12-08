package com.zf.common.product;

import com.zf.common.product.entity.BrandEntity;
import com.zf.common.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    private BrandService brandService;

    @Test
    void contextLoads() {
        BrandEntity brand = new BrandEntity();
        brand.setDescript("Apple");
        brand.setBrandId(1L);
        brandService.updateById(brand);

    }

}
