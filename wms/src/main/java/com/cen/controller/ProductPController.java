package com.cen.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cen.entity.Product;
import com.cen.entity.ProductInventory;
import com.cen.entity.ProductInventoryP;
import com.cen.service.IProductInventoryPService;
import com.cen.service.IProductInventoryService;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cen.common.Result;
import javax.annotation.Resource;
import java.util.List;

import com.cen.service.IProductPService;
import com.cen.entity.ProductP;

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
@RequestMapping("/productP")
public class ProductPController {

    @Resource
    private IProductPService productPService;

    @Resource
    private IProductInventoryPService productInventoryPService;
    //新增或修改
    @PostMapping("/save")
    public Result save(@RequestBody ProductP productP) {
        return Result.success(productPService.saveOrUpdate(productP));
    }
    //删除
    @PostMapping("/delete")
    public Result delete(@RequestBody ProductP productP){ //@RequestBody把前台的json对象转成java的对象
        return Result.success(productPService.removeById(productP.getId()));
    }
    //批量删除
    @PostMapping("/del/batch")
    public Result Batch(@RequestBody List<Integer> ids){
        return Result.success(productPService.removeBatchByIds(ids));
    }
    //根据id获取
    @GetMapping("/getById")
    public Result findOne(@PathVariable ProductP productP) {
        return Result.success(productPService.getById(productP.getId()));
    }
    //分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestParam(required = true) Integer userId) {
        QueryWrapper<ProductP> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id"); //设置id倒序
        queryWrapper.eq("user_id", userId);
        return Result.success(productPService.page(new Page<>(pageNum, pageSize),queryWrapper));
    }

    /**
     * 分页查询已发布商品详情
     */
    @GetMapping("/published/page")
    public Result findPublishedProductPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String origin) {

        return Result.success(productPService.findPublishedProductPage(pageNum, pageSize, name, origin));
    }

    /**
     * 商品上架/下架
     * @param id 商品ID
     * @param status 状态：1上架，0下架
     * @return 操作结果
     */
    @PostMapping("/publish/{id}/{status}")
    public Result publishProduct(@PathVariable Long id, @PathVariable Integer status) {
        // 1. 查询商品是否存在
        ProductP productP = productPService.getById(id);
        if (productP == null) {
            return Result.error(400,"商品不存在");
        }

        // 2. 如果是上架操作，需要检查库存
        if (status == 1) {
            // 查询商品库存
            QueryWrapper<ProductInventoryP> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("product_id", id);
            List<ProductInventoryP> inventories = productInventoryPService.list(queryWrapper);

            // 检查是否有库存记录
            if (inventories.isEmpty()) {
                return Result.error(400,"商品没有库存记录，请先添加库存");
            }

            // 检查是否有可用库存
            boolean hasAvailableStock = false;
            for (ProductInventoryP inventory : inventories) {
                if (inventory.getTotalStock() > inventory.getReservedStock()) {
                    hasAvailableStock = true;
                    break;
                }
            }

            if (!hasAvailableStock) {
                return Result.error(400,"商品库存不足，无法上架");
            }
        }

        // 3. 更新商品状态
        productP.setStatus(status);
        boolean success = productPService.updateById(productP);

        if (success) {
            return Result.success(status == 1 ? "商品上架成功" : "商品下架成功");
        } else {
            return Result.error(400,status == 1 ? "商品上架失败" : "商品下架失败");
        }
    }
}

