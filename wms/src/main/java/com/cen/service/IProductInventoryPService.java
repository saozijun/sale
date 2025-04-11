package com.cen.service;

import com.cen.entity.ProductInventoryP;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author volcano
 * @since 2025-04-09
 */
public interface IProductInventoryPService extends IService<ProductInventoryP> {
    /**
     * 锁定商品库存
     * @param inventoryId 库存ID
     * @param quantity 预订数量
     * @return 是否锁定成功
     */
    boolean lockStock(Long inventoryId, Integer quantity);

    /**
     * 释放商品库存
     * @param inventoryId 库存ID
     * @param quantity 释放数量
     * @return 是否释放成功
     */
    boolean unlockStock(Long inventoryId, Integer quantity);

    /**
     * 扣减商品库存
     * @param inventoryId 库存ID
     * @param quantity 扣减数量
     * @return 是否扣减成功
     */
    boolean deductStock(Long inventoryId, Integer quantity);

    /**
     * 检查库存是否充足
     * @param inventoryId 库存ID
     * @param quantity 检查数量
     * @return 是否充足
     */
    boolean checkStock(Long inventoryId, Integer quantity);

    /**
     * 增加商品库存
     * @param inventoryId 库存ID
     * @param quantity 增加数量
     * @return 是否增加成功
     */
    boolean addStock(Long inventoryId, Integer quantity);
}
