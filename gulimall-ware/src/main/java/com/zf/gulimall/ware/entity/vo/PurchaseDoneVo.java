package com.zf.gulimall.ware.entity.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author 42955
 */
@Data
public class PurchaseDoneVo {
    @NotNull
    private Long id;//采购单ID
    private List<PurchaseItemDoneVo> items;

}
