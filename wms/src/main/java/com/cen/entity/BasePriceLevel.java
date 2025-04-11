package com.cen.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

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
@TableName("sys_base_price_level")
public class BasePriceLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    // 主键ID
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 等级名称（A级、B级等）
    private String levelName;

    // 说明
    private String description;

    // 用户id
    private Long userId;
}
