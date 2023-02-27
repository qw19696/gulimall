package com.zf.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 42955
 */
@Data
public class SpuBoundTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
