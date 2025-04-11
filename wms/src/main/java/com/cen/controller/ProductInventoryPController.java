package com.cen.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cen.common.Constants;
import com.cen.controller.dto.StockDTO;
import com.cen.entity.*;
import com.cen.service.IBasePriceLevelService;
import com.cen.service.IOrderPService;
import com.cen.service.IOrderService;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cen.common.Result;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.cen.service.IProductInventoryPService;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author volcano
 * @since 2025-04-09
 */
@RestController
@RequestMapping("/productInventoryP")
public class ProductInventoryPController {

    @Resource
    private IProductInventoryPService productInventoryPService;

    @Resource
    private IBasePriceLevelService basePriceLevelService;

    @Resource
    private IOrderPService orderPService;
    //新增或修改
    @PostMapping("/save")
    public Result save(@RequestBody ProductInventoryP productInventory) {
        return Result.success(productInventoryPService.saveOrUpdate(productInventory));
    }
    //删除
    @PostMapping("/delete")
    public Result delete(@RequestBody ProductInventoryP productInventory){ //@RequestBody把前台的json对象转成java的对象
        return Result.success(productInventoryPService.removeById(productInventory.getId()));
    }
    //批量删除
    @PostMapping("/del/batch")
    public Result Batch(@RequestBody List<Integer> ids){
        return Result.success(productInventoryPService.removeBatchByIds(ids));
    }
    //根据id获取
    @GetMapping("/getById")
    public Result findOne(@PathVariable ProductInventoryP productInventory) {
        return Result.success(productInventoryPService.getById(productInventory.getId()));
    }
    //分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestParam(required = true) Integer productId) {
        QueryWrapper<ProductInventoryP> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id"); //设置id倒序
        queryWrapper.eq("product_id", productId);
        return Result.success(productInventoryPService.page(new Page<>(pageNum, pageSize),queryWrapper));
    }

    /**
     * 锁定库存
     */
    @PostMapping("/lock")
    public Result lockStock(@RequestParam Long inventoryId, @RequestParam Integer quantity) {
        if (!productInventoryPService.checkStock(inventoryId, quantity)) {
            return Result.error(Constants.CODE_500,"库存不足");
        }
        boolean success = productInventoryPService.lockStock(inventoryId, quantity);
        return success ? Result.success() : Result.error(Constants.CODE_500,"锁定库存失败");
    }

    /**
     * 释放库存
     */
    @PostMapping("/unlock")
    public Result unlockStock(@RequestParam Long inventoryId, @RequestParam Integer quantity) {
        boolean success = productInventoryPService.unlockStock(inventoryId, quantity);
        return success ? Result.success() : Result.error(Constants.CODE_500,"释放库存失败");
    }

    /**
     * 扣减库存
     */
    @PostMapping("/deduct")
    public Result deductStock(@RequestParam Long inventoryId, @RequestParam Integer quantity) {
        boolean success = productInventoryPService.deductStock(inventoryId, quantity);
        return success ? Result.success() : Result.error(Constants.CODE_500,"扣减库存失败");
    }

    /**
     * 查询可用库存
     */
    @GetMapping("/available")
    public Result getAvailableStock(@RequestParam Long inventoryId) {
        ProductInventoryP inventory = productInventoryPService.getById(inventoryId);
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
        ProductInventoryP inventory = productInventoryPService.getById(stockDTO.getInventoryId());
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

        boolean success = productInventoryPService.updateById(inventory);
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

        ProductInventoryP inventory = productInventoryPService.getById(inventoryId);
        if (inventory == null) {
            return Result.error(Constants.CODE_500, "库存记录不存在");
        }

        // 增加库存
        inventory.setTotalStock(inventory.getTotalStock() + quantity);

        boolean success = productInventoryPService.updateById(inventory);
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

        ProductInventoryP inventory = productInventoryPService.getById(inventoryId);
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
        boolean success = productInventoryPService.updateById(inventory);
        return success ? Result.success() : Result.error(Constants.CODE_500, "减少库存失败");
    }

    /**
     * 获取商品的完整库存数据
     */
    @GetMapping("/product/full")
    public Result getProductFullInventory(@RequestParam Long productId) {
        // 查询该商品的所有库存记录
        QueryWrapper<ProductInventoryP> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId);
        List<ProductInventoryP> inventories = productInventoryPService.list(queryWrapper);

        // 获取所有价格等级ID
        List<Long> priceLevelIds = inventories.stream()
                .map(ProductInventoryP::getPriceLevelId)
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
        for (ProductInventoryP inventory : inventories) {
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
        OrderP orderP = orderPService.getById(orderId);
        if (orderP == null) {
            return Result.error(Constants.CODE_500, "订单不存在");
        }

        // 如果订单状态变为已付款，则扣除库存
        if ("paid".equals(newStatus) && !"paid".equals(orderP.getStatus())) {
            boolean success = productInventoryPService.deductStock(orderP.getInventoryId(), orderP.getQuantity());
            if (!success) {
                return Result.error(Constants.CODE_500, "扣除库存失败");
            }
        }

        // 如果订单状态变为已取消，则释放库存
        if ("cancelled".equals(newStatus) && !"cancelled".equals(orderP.getStatus())) {
            boolean success = productInventoryPService.unlockStock(orderP.getInventoryId(), orderP.getQuantity());
            if (!success) {
                return Result.error(Constants.CODE_500, "释放库存失败");
            }
        }

        // 如果订单状态变为已退款或者已退货，则增加库存
        if (("refunded".equals(newStatus) || "returned".equals(newStatus)) &&
                !"refunded".equals(orderP.getStatus()) && !"returned".equals(orderP.getStatus())) {
            boolean success = productInventoryPService.addStock(orderP.getInventoryId(), orderP.getQuantity());
            if (!success) {
                return Result.error(Constants.CODE_500, "增加库存失败");
            }
        }

        // 更新订单状态
        orderP.setStatus(newStatus);
        orderP.setUpdatedAt(LocalDateTime.now());
        boolean success = orderPService.updateById(orderP);
        return success ? Result.success() : Result.error(Constants.CODE_500, "更新订单状态失败");
    }
}

