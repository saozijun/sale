package com.cen.controller.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProductDetailDTO implements Serializable {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long userId;
    private String origin; // 产地
    private String manufacturer; // 制造商
    private String contact;
    private LocalDate listedTime; // 上架时间
    private String specification; // 规格
    private String coverImage;
    private String images; // 图片路径
    private LocalDateTime createTime; // 创建时间
} 