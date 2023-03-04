package com.zf.gulimall.ware.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * 合并采购单Vo
 * @author 42955
 */
@Data
public class MergeVo {
    private Long purchaseId;//采购单Id
    private List<Long> items;//将要合并的采购需求ID
}
