package com.cen.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;

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
@TableName("sys_product_inventory")
public class ProductInventory implements Serializable {

    private static final long serialVersionUID = 1L;

    // 主键ID
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 商品ID
    private Long productId;

    // 价格等级ID
    private Long priceLevelId;

    // 单价
    private BigDecimal price;

    // 总库存（斤）
    private Integer totalStock;

    // 已预定库存
    private Integer reservedStock;

    // 阶梯折扣规则(JSON，如 [{"amount":100,"discount":0.95},{"amount":500,"discount":0.9}])
    private String ladderDiscount;
}
