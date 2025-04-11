package com.cen.controller.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDetailDTO {
    // 订单基本信息
    private Long id;
    private String orderNo;
    private String status;
    private LocalDateTime updatedAt;
    
    // 商品信息
    private Long productId;
    private String productName;
    private String productImage;
    
    // 价格信息
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal totalAmount;
    
    // 买家信息
    private Long buyerId;
    private String buyerName;
    private String buyerPhone;
    
    // 卖家信息
    private Long sellerId;
    private String sellerName;
    private String sellerPhone;
} 