package com.zf.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 42955
 */
@Data
public class SkuReductionTo {
    private Long skuId;
    private Integer fullCount;
    private BigDecimal discount;
    private Integer countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer priceStatus;
    private List<MemberPrice> memberPrice;
}
