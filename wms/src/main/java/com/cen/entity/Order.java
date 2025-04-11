package com.cen.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
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
@TableName("sys_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    // 主键ID
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 订单编号
    private String orderNo;

    // 买家ID
    private Long buyerId;

    // 卖家ID（种植户）
    private Long sellerId;

    // 商品ID
    private Long productId;

    // 库存ID
    private Long inventoryId;

    // 数量
    private Integer quantity;

    // 订单单价
    private BigDecimal unitPrice;

    // 订单总金额
    private BigDecimal totalAmount;

    // 订单状态（pendi创建时间
    //    private LocalDateTime createdAt;ng待付款、paid已付款、shipped已发货、completed已完成、cancelled已取消）
    private String status;

    //
    // 更新时间
    private LocalDateTime updatedAt;
}
