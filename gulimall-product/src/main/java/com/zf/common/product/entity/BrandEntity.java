package com.zf.common.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 品牌
 * 
 * @author zf
 * @email 429558984@qq.com
 * @date 2022-12-06 20:05:38
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
	private Long brandId;

	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名称不能为空！")
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank(message = "logo地址不能为空！")
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@NotNull(message = "显示状态不能为空!")
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@Pattern(regexp = "^[a-zA-Z]$", message = "首字母必须是一个字母!")
	private String firstLetter;
	/**
	 * 排序
	 */
	private Integer sort;

}
