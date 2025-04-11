package com.cen.service.impl;

import com.cen.entity.ProductInventory;
import com.cen.mapper.ProductInventoryMapper;
import com.cen.service.IProductInventoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author volcano
 * @since 2025-04-08
 */
@Service
public class ProductInventoryServiceImpl extends ServiceImpl<ProductInventoryMapper, ProductInventory> implements IProductInventoryService {

    @Override
    @Transactional
    public boolean lockStock(Long inventoryId, Integer quantity) {
        UpdateWrapper<ProductInventory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", inventoryId)
                .ge("total_stock - reserved_stock", quantity)
                .setSql("reserved_stock = reserved_stock + " + quantity);
        
        return update(updateWrapper);
    }

    @Override
    @Transactional
    public boolean unlockStock(Long inventoryId, Integer quantity) {
        UpdateWrapper<ProductInventory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", inventoryId)
                .ge("reserved_stock", quantity)
                .setSql("reserved_stock = reserved_stock - " + quantity);
        
        return update(updateWrapper);
    }

    @Override
    @Transactional
    public boolean deductStock(Long inventoryId, Integer quantity) {
        UpdateWrapper<ProductInventory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", inventoryId)
                .ge("total_stock", quantity)
                .ge("reserved_stock", quantity)
                .setSql("total_stock = total_stock - " + quantity)
                .setSql("reserved_stock = reserved_stock - " + quantity);
        
        return update(updateWrapper);
    }

    @Override
    public boolean checkStock(Long inventoryId, Integer quantity) {
        ProductInventory inventory = getById(inventoryId);
        return inventory != null && 
               (inventory.getTotalStock() - inventory.getReservedStock()) >= quantity;
    }

    @Override
    @Transactional
    public boolean addStock(Long inventoryId, Integer quantity) {
        UpdateWrapper<ProductInventory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", inventoryId)
                .setSql("total_stock = total_stock + " + quantity);
        
        return update(updateWrapper);
    }
}
