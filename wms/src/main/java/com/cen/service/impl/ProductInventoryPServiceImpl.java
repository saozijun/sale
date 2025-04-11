package com.cen.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cen.entity.ProductInventory;
import com.cen.entity.ProductInventoryP;
import com.cen.mapper.ProductInventoryPMapper;
import com.cen.service.IProductInventoryPService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author volcano
 * @since 2025-04-09
 */
@Service
public class ProductInventoryPServiceImpl extends ServiceImpl<ProductInventoryPMapper, ProductInventoryP> implements IProductInventoryPService {
    @Override
    @Transactional
    public boolean lockStock(Long inventoryId, Integer quantity) {
        UpdateWrapper<ProductInventoryP> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", inventoryId)
                .ge("total_stock - reserved_stock", quantity)
                .setSql("reserved_stock = reserved_stock + " + quantity);

        return update(updateWrapper);
    }

    @Override
    @Transactional
    public boolean unlockStock(Long inventoryId, Integer quantity) {
        UpdateWrapper<ProductInventoryP> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", inventoryId)
                .ge("reserved_stock", quantity)
                .setSql("reserved_stock = reserved_stock - " + quantity);

        return update(updateWrapper);
    }

    @Override
    @Transactional
    public boolean deductStock(Long inventoryId, Integer quantity) {
        UpdateWrapper<ProductInventoryP> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", inventoryId)
                .ge("total_stock", quantity)
                .ge("reserved_stock", quantity)
                .setSql("total_stock = total_stock - " + quantity)
                .setSql("reserved_stock = reserved_stock - " + quantity);

        return update(updateWrapper);
    }

    @Override
    public boolean checkStock(Long inventoryId, Integer quantity) {
        ProductInventoryP inventory = getById(inventoryId);
        return inventory != null &&
                (inventory.getTotalStock() - inventory.getReservedStock()) >= quantity;
    }

    @Override
    @Transactional
    public boolean addStock(Long inventoryId, Integer quantity) {
        UpdateWrapper<ProductInventoryP> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", inventoryId)
                .setSql("total_stock = total_stock + " + quantity);

        return update(updateWrapper);
    }
}
