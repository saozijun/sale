package com.cen.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cen.common.Constants;
import com.cen.controller.dto.StockDTO;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cen.common.Result;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import com.cen.service.IProductInventoryService;
import com.cen.service.IBasePriceLevelService;
import com.cen.entity.ProductInventory;
import com.cen.entity.BasePriceLevel;
import com.cen.service.IOrderService;
import com.cen.entity.Order;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author volcano
 * @since 2025-04-08
 */
@RestController
@RequestMapping("/productInventory")
public class ProductInventoryController {

    @Resource
    private IProductInventoryService productInventoryService;

    @Resource
    private IBasePriceLevelService basePriceLevelService;

    @Resource
    private IOrderService orderService;

    //新增或修改
    @PostMapping("/save")
    public Result save(@RequestBody ProductInventory productInventory) {
        return Result.success(productInventoryService.saveOrUpdate(productInventory));
    }
    //删除
    @PostMapping("/delete")
    public Result delete(@RequestBody ProductInventory productInventory){ //@RequestBody把前台的json对象转成java的对象
        return Result.success(productInventoryService.removeById(productInventory.getId()));
    }
    //批量删除
    @PostMapping("/del/batch")
    public Result Batch(@RequestBody List<Integer> ids){
        return Result.success(productInventoryService.removeBatchByIds(ids));
    }
    //根据id获取
    @GetMapping("/getById")
    public Result findOne(@PathVariable ProductInventory productInventory) {
        return Result.success(productInventoryService.getById(productInventory.getId()));
    }
    //分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestParam(required = true) Integer productId) {
        QueryWrapper<ProductInventory> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id"); //设置id倒序
        queryWrapper.eq("product_id", productId);
        return Result.success(productInventoryService.page(new Page<>(pageNum, pageSize),queryWrapper));
    }

    /**
     * 锁定库存
     */
    @PostMapping("/lock")
    public Result lockStock(@RequestParam Long inventoryId, @RequestParam Integer quantity) {
        if (!productInventoryService.checkStock(inventoryId, quantity)) {
            return Result.error(Constants.CODE_500,"库存不足");
        }
        boolean success = productInventoryService.lockStock(inventoryId, quantity);
        return success ? Result.success() : Result.error(Constants.CODE_500,"锁定库存失败");
    }

    /**
     * 释放库存
     */
    @PostMapping("/unlock")
    public Result unlockStock(@RequestParam Long inventoryId, @RequestParam Integer quantity) {
        boolean success = productInventoryService.unlockStock(inventoryId, quantity);
        return success ? Result.success() : Result.error(Constants.CODE_500,"释放库存失败");
    }

    /**
     * 扣减库存
     */
    @PostMapping("/deduct")
    public Result deductStock(@RequestParam Long inventoryId, @RequestParam Integer quantity) {
        boolean success = productInventoryService.deductStock(inventoryId, quantity);
        return success ? Result.success() : Result.error(Constants.CODE_500,"扣减库存失败");
    }

    /**
     * 查询可用库存
     */
    @GetMapping("/available")
    public Result getAvailableStock(@RequestParam Long inventoryId) {
        ProductInventory inventory = productInventoryService.getById(inventoryId);
        if (inventory == null) {
            return Result.error(Constants.CODE_500,"库存记录不存在");
        }
        int availableStock = inventory.getTotalStock() - inventory.getReservedStock();
        return Result.success(availableStock);
    }

    /**
     * 修改库存数量
     */
    @PostMapping("/updateStock")
    public Result updateStock(@RequestBody StockDTO stockDTO) {
        ProductInventory inventory = productInventoryService.getById(stockDTO.getInventoryId());
        if (inventory == null) {
            return Result.error(Constants.CODE_500, "库存记录不存在");
        }
        
        // 检查库存数量是否合法
        if (stockDTO.getTotalStock() < 0) {
            return Result.error(Constants.CODE_500, "库存数量不能为负数");
        }
        
        // 检查是否小于已预订数量
        if (stockDTO.getTotalStock() < inventory.getReservedStock()) {
            return Result.error(Constants.CODE_500, "库存数量不能小于已预订数量");
        }
        
        // 更新库存
        inventory.setTotalStock(stockDTO.getTotalStock());
        
        boolean success = productInventoryService.updateById(inventory);
        return success ? Result.success() : Result.error(Constants.CODE_500, "更新库存失败");
    }

    /**
     * 增加库存
     */
    @PostMapping("/addStock")
    public Result addStock(@RequestParam Long inventoryId, 
                          @RequestParam Integer quantity) {
        if (quantity <= 0) {
            return Result.error(Constants.CODE_500, "增加的库存数量必须大于0");
        }
        
        ProductInventory inventory = productInventoryService.getById(inventoryId);
        if (inventory == null) {
            return Result.error(Constants.CODE_500, "库存记录不存在");
        }
        
        // 增加库存
        inventory.setTotalStock(inventory.getTotalStock() + quantity);
        
        boolean success = productInventoryService.updateById(inventory);
        return success ? Result.success() : Result.error(Constants.CODE_500, "增加库存失败");
    }

    /**
     * 减少库存
     */
    @PostMapping("/reduceStock")
    public Result reduceStock(@RequestParam Long inventoryId, 
                            @RequestParam Integer quantity) {
        if (quantity <= 0) {
            return Result.error(Constants.CODE_500, "减少的库存数量必须大于0");
        }
        
        ProductInventory inventory = productInventoryService.getById(inventoryId);
        if (inventory == null) {
            return Result.error(Constants.CODE_500, "库存记录不存在");
        }
        
        // 检查可用库存是否足够
        int availableStock = inventory.getTotalStock() - inventory.getReservedStock();
        if (quantity > availableStock) {
            return Result.error(Constants.CODE_500, "可用库存不足");
        }
        
        // 减少库存
        inventory.setTotalStock(inventory.getTotalStock() - quantity);
        boolean success = productInventoryService.updateById(inventory);
        return success ? Result.success() : Result.error(Constants.CODE_500, "减少库存失败");
    }

    /**
     * 获取商品的完整库存数据
     */
    @GetMapping("/product/full")
    public Result getProductFullInventory(@RequestParam Long productId) {
        // 查询该商品的所有库存记录
        QueryWrapper<ProductInventory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId);
        List<ProductInventory> inventories = productInventoryService.list(queryWrapper);
        
        // 获取所有价格等级ID
        List<Long> priceLevelIds = inventories.stream()
                .map(ProductInventory::getPriceLevelId)
                .collect(Collectors.toList());
        
        // 批量查询价格等级信息
        Map<Long, BasePriceLevel> priceLevelMap;
        if (!priceLevelIds.isEmpty()) {
            List<BasePriceLevel> priceLevels = basePriceLevelService.listByIds(priceLevelIds);
            priceLevelMap = priceLevels.stream()
                    .collect(Collectors.toMap(BasePriceLevel::getId, level -> level));
        } else {
            priceLevelMap = new HashMap<>();
        }

        // 计算总库存和可用库存
        int totalStock = 0;
        int availableStock = 0;
        for (ProductInventory inventory : inventories) {
            totalStock += inventory.getTotalStock();
            availableStock += (inventory.getTotalStock() - inventory.getReservedStock());
        }
        
        // 构建返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("inventories", inventories.stream().map(inventory -> {
            Map<String, Object> inventoryMap = new HashMap<>();
            inventoryMap.put("id", inventory.getId());
            inventoryMap.put("productId", inventory.getProductId());
            inventoryMap.put("priceLevelId", inventory.getPriceLevelId());
            inventoryMap.put("price", inventory.getPrice());
            inventoryMap.put("totalStock", inventory.getTotalStock());
            inventoryMap.put("reservedStock", inventory.getReservedStock());
            inventoryMap.put("ladderDiscount", inventory.getLadderDiscount());
            
            // 添加价格等级信息
            BasePriceLevel priceLevel = priceLevelMap.get(inventory.getPriceLevelId());
            if (priceLevel != null) {
                inventoryMap.put("priceLevelName", priceLevel.getLevelName());
                inventoryMap.put("priceLevelDescription", priceLevel.getDescription());
            }
            
            return inventoryMap;
        }).collect(Collectors.toList()));
        result.put("totalStock", totalStock);
        result.put("availableStock", availableStock);
        
        return Result.success(result);
    }

    /**
     * 处理订单状态变更
     * @param orderId 订单ID
     * @param newStatus 新状态
     * @return 处理结果
     */
    @PostMapping("/order/status/change")
    public Result handleOrderStatusChange(@RequestParam Long orderId, @RequestParam String newStatus) {
        // 获取订单信息
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error(Constants.CODE_500, "订单不存在");
        }

        // 如果订单状态变为已付款，则扣除库存
        if ("paid".equals(newStatus) && !"paid".equals(order.getStatus())) {
            boolean success = productInventoryService.deductStock(order.getInventoryId(), order.getQuantity());
            if (!success) {
                return Result.error(Constants.CODE_500, "扣除库存失败");
            }
        }

        // 如果订单状态变为已取消，则释放库存
        if ("cancelled".equals(newStatus) && !"cancelled".equals(order.getStatus())) {
            boolean success = productInventoryService.unlockStock(order.getInventoryId(), order.getQuantity());
            if (!success) {
                return Result.error(Constants.CODE_500, "释放库存失败");
            }
        }

        // 如果订单状态变为已退款或者已退货，则增加库存
        if (("refunded".equals(newStatus) || "returned".equals(newStatus)) && 
            !"refunded".equals(order.getStatus()) && !"returned".equals(order.getStatus())) {
            boolean success = productInventoryService.addStock(order.getInventoryId(), order.getQuantity());
            if (!success) {
                return Result.error(Constants.CODE_500, "增加库存失败");
            }
        }

        // 更新订单状态
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        boolean success = orderService.updateById(order);
        return success ? Result.success() : Result.error(Constants.CODE_500, "更新订单状态失败");
    }
}

