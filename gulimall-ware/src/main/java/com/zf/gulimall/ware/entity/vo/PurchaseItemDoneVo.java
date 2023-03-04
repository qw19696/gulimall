package com.zf.gulimall.ware.entity.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
@Data
public class PurchaseItemDoneVo {
    private Long id;
    private Integer status;
    private String reason;
}
