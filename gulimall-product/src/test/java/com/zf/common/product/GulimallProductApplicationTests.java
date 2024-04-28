package com.zf.common.product;


import com.zf.common.product.service.BrandService;
import com.zf.common.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;


@SpringBootTest
@Slf4j
class GulimallProductApplicationTests {

    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;

    @Test
    void contextLoads() {
        Long[] cateLogPath = categoryService.getCateLogPath(225L);
        log.info("完整路径:{}",Arrays.asList(cateLogPath));
    }

}
