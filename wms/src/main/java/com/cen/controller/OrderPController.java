package com.cen.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cen.common.Constants;
import com.cen.entity.Order;
import com.cen.service.IOrderService;
import com.cen.service.IProductInventoryPService;
import com.cen.service.IProductInventoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cen.common.Result;
import javax.annotation.Resource;
import java.util.List;

import com.cen.service.IOrderPService;
import com.cen.entity.OrderP;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author volcano
 * @since 2025-04-10
 */
@RestController
@RequestMapping("/orderP")
public class OrderPController {

    @Resource
    private IOrderPService orderPService;

    @Resource
    private IProductInventoryPService productInventoryPService;
    //新增或修改
    @PostMapping("/save")
    public Result save(@RequestBody OrderP orderP) {
        productInventoryPService.lockStock(orderP.getInventoryId(),orderP.getQuantity());
        orderPService.saveOrUpdate(orderP);
        return Result.success(orderP.getId());
    }
    //删除
    @PostMapping("/delete")
    public Result delete(@RequestBody OrderP orderP){
        return Result.success(orderPService.removeById(orderP.getId()));
    }
    //批量删除
    @PostMapping("/del/batch")
    public Result Batch(@RequestBody List<Integer> ids){
        return Result.success(orderPService.removeBatchByIds(ids));
    }
    //根据id获取
    @GetMapping("/getById")
    public Result findOne(@PathVariable OrderP orderP) {
        return Result.success(orderPService.getById(orderP.getId()));
    }
    //分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestParam(defaultValue = "") String status,
                           @RequestParam(required = true) Long userId) {
        return Result.success(orderPService.findOrderDetailPage(pageNum, pageSize, userId, status));
    }
}

