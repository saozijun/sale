package com.cen.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cen.entity.BasePlace;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cen.common.Result;
import javax.annotation.Resource;
import java.util.List;

import com.cen.service.IBasePriceLevelService;
import com.cen.entity.BasePriceLevel;

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
@RequestMapping("/basePriceLevel")
public class BasePriceLevelController {

    @Resource
    private IBasePriceLevelService basePriceLevelService;

    @GetMapping("/basePriceLevelList")
    public Result basePriceLevelList(@RequestParam(required = true) Long userId){
        QueryWrapper<BasePriceLevel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return Result.success(basePriceLevelService.list(queryWrapper));
    }
    //新增或修改
    @PostMapping("/save")
    public Result save(@RequestBody BasePriceLevel basePriceLevel) {
        return Result.success(basePriceLevelService.saveOrUpdate(basePriceLevel));
    }
    //删除
    @PostMapping("/delete")
    public Result delete(@RequestBody BasePriceLevel basePriceLevel){ //@RequestBody把前台的json对象转成java的对象
        return Result.success(basePriceLevelService.removeById(basePriceLevel.getId()));
    }
    //批量删除
    @PostMapping("/del/batch")
    public Result Batch(@RequestBody List<Integer> ids){
        return Result.success(basePriceLevelService.removeBatchByIds(ids));
    }
    //根据id获取
    @GetMapping("/getById")
    public Result findOne(@PathVariable BasePriceLevel basePriceLevel) {
        return Result.success(basePriceLevelService.getById(basePriceLevel.getId()));
    }
    //分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestParam(required = true) Integer userId,
                           @RequestParam(defaultValue = "") String name) {
        QueryWrapper<BasePriceLevel> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(Strings.isNotEmpty(name),"name",name);
        queryWrapper.orderByDesc("id"); //设置id倒序
        queryWrapper.eq("user_id", userId);
        return Result.success(basePriceLevelService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }
}

