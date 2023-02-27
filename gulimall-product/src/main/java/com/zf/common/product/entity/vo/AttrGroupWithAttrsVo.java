package com.zf.common.product.entity.vo;

import com.zf.common.product.entity.AttrEntity;
import com.zf.common.product.entity.AttrGroupEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author 42955
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AttrGroupWithAttrsVo extends AttrGroupEntity {
    private List<AttrEntity> attrs;
}
