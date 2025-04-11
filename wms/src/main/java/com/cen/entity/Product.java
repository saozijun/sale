package com.cen.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author volcano
 * @since 2025-04-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    // 主键ID
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 种植户ID
    private Long userId;

    // 商品名称
    private String name;

    // 产地ID
    private Long originPlaceId;

    // 预计上市时间
    private LocalDate listedTime;

    // 封面图片
    private String coverImage;

    // 多张现货图片
    private String images;

    // 描述
    private String description;

    // 状态：1上架，0下架
    private Integer status;

    // 创建时间
    private LocalDateTime createdAt;

    // 更新时间
    private LocalDateTime updatedAt;
}
