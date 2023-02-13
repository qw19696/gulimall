package com.zf.common.product.entity.vo;

import com.zf.common.product.entity.AttrEntity;
import com.zf.common.product.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

@Data
public class AttrGroupWithAttrsVo extends AttrGroupEntity {
    private List<AttrEntity> attrs;
}
