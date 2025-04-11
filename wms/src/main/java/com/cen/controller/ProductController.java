package com.cen.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cen.common.Result;
import javax.annotation.Resource;
import java.util.List;

import com.cen.service.IProductService;
import com.cen.entity.Product;
import com.cen.service.IProductInventoryService;
import com.cen.entity.ProductInventory;

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
@RequestMapping("/product")
public class ProductController {

    @Resource
    private IProductService productService;
    
    @Resource
    private IProductInventoryService productInventoryService;
    
    //新增或修改
    @PostMapping("/save")
    public Result save(@RequestBody Product product) {
        return Result.success(productService.saveOrUpdate(product));
    }
    //删除
    @PostMapping("/delete")
    public Result delete(@RequestBody Product product){ //@RequestBody把前台的json对象转成java的对象
        return Result.success(productService.removeById(product.getId()));
    }
    //批量删除
    @PostMapping("/del/batch")
    public Result Batch(@RequestBody List<Integer> ids){
        return Result.success(productService.removeBatchByIds(ids));
    }
    //根据id获取
    @GetMapping("/getById")
    public Result findOne(@PathVariable Product product) {
        return Result.success(productService.getById(product.getId()));
    }
    //分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestParam(defaultValue = "") Integer userId) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id"); //设置id倒序
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        return Result.success(productService.page(new Page<>(pageNum, pageSize),queryWrapper));
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
        
        return Result.success(productService.findPublishedProductPage(pageNum, pageSize, name, origin));
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
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error(400,"商品不存在");
        }
        
        // 2. 如果是上架操作，需要检查库存
        if (status == 1) {
            // 查询商品库存
            QueryWrapper<ProductInventory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("product_id", id);
            List<ProductInventory> inventories = productInventoryService.list(queryWrapper);
            
            // 检查是否有库存记录
            if (inventories.isEmpty()) {
                return Result.error(400,"商品没有库存记录，请先添加库存");
            }
            
            // 检查是否有可用库存
            boolean hasAvailableStock = false;
            for (ProductInventory inventory : inventories) {
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
        product.setStatus(status);
        boolean success = productService.updateById(product);
        
        if (success) {
            return Result.success(status == 1 ? "商品上架成功" : "商品下架成功");
        } else {
            return Result.error(400,status == 1 ? "商品上架失败" : "商品下架失败");
        }
    }
}

