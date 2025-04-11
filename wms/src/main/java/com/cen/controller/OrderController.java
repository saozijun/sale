package com.cen.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cen.common.Constants;
import com.cen.service.IProductInventoryService;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cen.common.Result;
import javax.annotation.Resource;
import java.util.List;

import com.cen.service.IOrderService;
import com.cen.entity.Order;
import com.cen.controller.dto.OrderDetailDTO;
import com.cen.utils.TokenUtils;
import com.cen.entity.User;

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
@RequestMapping("/order")
public class OrderController {

    @Resource
    private IOrderService orderService;

    @Resource
    private IProductInventoryService productInventoryService;
    //新增或修改
    @PostMapping("/save")
    public Result save(@RequestBody Order order) {
        productInventoryService.lockStock(order.getInventoryId(),order.getQuantity());
        orderService.saveOrUpdate(order);
        return Result.success(order.getId());
    }
    //删除
    @PostMapping("/delete")
    public Result delete(@RequestBody Order order){ //@RequestBody把前台的json对象转成java的对象
        return Result.success(orderService.removeById(order.getId()));
    }
    //批量删除
    @PostMapping("/del/batch")
    public Result Batch(@RequestBody List<Integer> ids){
        return Result.success(orderService.removeBatchByIds(ids));
    }
    //根据id获取
    @GetMapping("/getById")
    public Result findOne(@PathVariable Order order) {
        return Result.success(orderService.getById(order.getId()));
    }
    //分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestParam(defaultValue = "") String status,
                           @RequestParam(required = true) Long userId) {
        return Result.success(orderService.findOrderDetailPage(pageNum, pageSize, userId, status));
    }
}

