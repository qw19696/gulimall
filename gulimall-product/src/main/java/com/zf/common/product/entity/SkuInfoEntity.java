package com.zf.common.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.zf.common.product.entity.vo.spu.Attr;
import com.zf.common.product.entity.vo.spu.Images;
import lombok.Data;

/**
 * sku信息
 * 
 * @author zf
 * @email 429558984@qq.com
 * @date 2022-12-06 20:05:38
 */
@Data
@TableName("pms_sku_info")
public class SkuInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * skuId
	 */
	@TableId
	private Long skuId;
	/**
	 * spuId
	 */
	private Long spuId;
	/**
	 * sku名称
	 */
	private String skuName;
	/**
	 * sku介绍描述
	 */
	private String skuDesc;
	/**
	 * 所属分类id
	 */
	private Long catalogId;
	/**
	 * 品牌id
	 */
	private Long brandId;
	/**
	 * 默认图片
	 */
	private String skuDefaultImg;
	/**
	 * 标题
	 */
	private String skuTitle;
	/**
	 * 副标题
	 */
	private String skuSubtitle;
	/**
	 * 价格
	 */
	private BigDecimal price;
	/**
	 * 销量
	 */
	private Long saleCount;
	/**
	 * 图片集合
	 */
	@TableField(exist = false)
	private List<Images> images;

	/**
	 * Attr集合
	 */
	@TableField(exist = false)
	private List<Attr> attr;
	/**
	 * 临时数据收集
	 */
	@TableField(exist = false)
	private Map<String,Object> tmp;

}
