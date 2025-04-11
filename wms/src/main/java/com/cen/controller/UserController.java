package com.cen.controller;


import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cen.common.Constants;
import com.cen.common.lang.Const;
import com.cen.controller.dto.UserDTO;
import com.cen.entity.FileD;
import com.cen.exception.ServiceException;
import com.cen.mapper.UserMapper;
import com.cen.utils.RedisUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cen.common.Result;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.cen.service.IUserService;
import com.cen.entity.User;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author volcano
 * @since 2025-04-09
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Value("${files.upload.path}")
    private  String fileUploadPath;
    @Resource
    private IUserService userService;
    @Resource
    UserMapper userMapper;
    //新增或修改
    @PostMapping("/save")
    public Result save(@RequestBody User user) {
//        return Result.success(userService.saveOrUpdate(user));
        return Result.success(userService.saveUser(user));
    }
    //修改密码
    @PostMapping("/edit/pow")
    public Result editPow(@RequestBody User user) {
        return Result.success(userService.editPow(user));
    }
    //上传头像
    @PostMapping("/upload/avatar")
    public Result uploadAvatar(@RequestBody User user) throws IOException {

        return Result.success(userService.saveOrUpdate(user));
    }
    //删除
    @PostMapping("/delete")
    public Result userDelete(@RequestBody User user){ //@RequestBody把前台的json对象转成java的对象
        return Result.success(userService.removeById(user.getId()));
    }
    //批量删除
    @PostMapping("/del/batch")
    public Result batch(@RequestBody List<Integer> ids){
        return Result.success(userService.removeBatchByIds(ids));
    }
    //根据id获取
    @GetMapping("/getById")
    public Result findOne(@RequestParam Long id) {
        return Result.success(userService.getById(id));
    }
    //分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestParam(defaultValue = "") String nickname,
                           @RequestParam(defaultValue = "") String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(Strings.isNotEmpty(nickname),"nickname",nickname);
        queryWrapper.like(Strings.isNotEmpty(email),"email",email);
        queryWrapper.orderByDesc("id"); //设置id倒序
        return Result.success(userService.page(new Page<>(pageNum, pageSize),queryWrapper));
    }

    /**
     * 获取系统统计数据
     * @return 包含多个统计指标的JSON数据
     */
    @GetMapping("/statistics")
    public Result getSystemStatistics(@RequestParam Long userId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 获取用户信息
        User user = userService.getById(userId);
        if (user == null) {
            throw new ServiceException(Constants.CODE_400, "用户不存在");
        }
        
        // 根据用户角色返回不同的统计数据
        if ("growers".equals(user.getRole())) {
            // 种植户统计
            Map<String, Object> productStats = new HashMap<>();
            productStats.put("totalProducts", userMapper.getGrowerProducts(userId));
            productStats.put("totalInventory", userMapper.getGrowerInventory(userId));
            productStats.put("totalStock", userMapper.getGrowerTotalStock(userId));
            productStats.put("totalReservedStock", userMapper.getGrowerReservedStock(userId));
            // 添加热门商品排行
            productStats.put("topProducts", userMapper.getGrowerTopProducts(userId));
            // 添加商品产地分布
            productStats.put("placeDistribution", userMapper.getGrowerProductPlaceDistribution(userId));
            statistics.put("productStats", productStats);
            
            Map<String, Object> orderStats = new HashMap<>();
            orderStats.put("statusDistribution", userMapper.getGrowerOrderStatus(userId));
            orderStats.put("orderTrend", userMapper.getGrowerOrderTrend(userId));
            statistics.put("orderStats", orderStats);
            
            // 计算库存利用率
            Integer totalStock = userMapper.getGrowerTotalStock(userId);
            Integer totalReservedStock = userMapper.getGrowerReservedStock(userId);
            if (totalStock != null && totalStock > 0) {
                double stockUtilizationRate = (double) totalReservedStock / totalStock * 100;
                statistics.put("stockUtilizationRate", String.format("%.2f", stockUtilizationRate));
            }
            
        } else if ("wholesaler".equals(user.getRole())) {
            // 批发商统计
            Map<String, Object> productStats = new HashMap<>();
            productStats.put("totalProducts", userMapper.getWholesalerProducts(userId));
            productStats.put("totalInventory", userMapper.getWholesalerInventory(userId));
            productStats.put("totalStock", userMapper.getWholesalerTotalStock(userId));
            productStats.put("totalReservedStock", userMapper.getWholesalerReservedStock(userId));
            // 添加热门商品排行
            productStats.put("topProducts", userMapper.getWholesalerTopProducts(userId));
            // 添加商品产地分布
            productStats.put("placeDistribution", userMapper.getWholesalerProductPlaceDistribution(userId));
            statistics.put("productStats", productStats);
            
            Map<String, Object> orderStats = new HashMap<>();
            orderStats.put("statusDistribution", userMapper.getWholesalerOrderStatus(userId));
            orderStats.put("orderTrend", userMapper.getWholesalerOrderTrend(userId));
            statistics.put("orderStats", orderStats);
            
            // 计算库存利用率
            Integer totalStock = userMapper.getWholesalerTotalStock(userId);
            Integer totalReservedStock = userMapper.getWholesalerReservedStock(userId);
            if (totalStock != null && totalStock > 0) {
                double stockUtilizationRate = (double) totalReservedStock / totalStock * 100;
                statistics.put("stockUtilizationRate", String.format("%.2f", stockUtilizationRate));
            }
        }
        
        return Result.success(statistics);
    }
}

