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
 * @since 2025-04-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_order_p")
public class OrderP implements Serializable {

    private static final long serialVersionUID = 1L;

    // 主键ID
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 订单编号
    private String orderNo;

    // 买家ID
    private Long buyerId;

    // 卖家ID
    private Long sellerId;

    // 订单总金额
    private BigDecimal totalAmount;

    // 订单状态（pending待付款、paid已付款、shipped已发货、completed已完成、cancelled已取消、refunded已退款、refunding退款中，returned已退货、returning退货中）
    private String status;

    // 商品id
    private Long productId;

    // 库存id
    private Long inventoryId;

    // 数量
    private Integer quantity;

    // 单价
    private BigDecimal unitPrice;

    // 创建时间
    private LocalDateTime createdAt;

    // 更新时间
    private LocalDateTime updatedAt;
}
