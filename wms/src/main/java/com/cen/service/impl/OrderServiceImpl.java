package com.cen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cen.controller.dto.OrderDetailDTO;
import com.cen.entity.Order;
import com.cen.entity.Product;
import com.cen.entity.User;
import com.cen.mapper.OrderMapper;
import com.cen.service.IOrderService;
import com.cen.service.IProductService;
import com.cen.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.cen.common.Constants;
import com.cen.exception.ServiceException;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author volcano
 * @since 2025-04-08
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Resource
    private IProductService productService;

    @Resource
    private IUserService userService;

    @Override
    public Page<OrderDetailDTO> findOrderDetailPage(Integer pageNum, Integer pageSize, Long userId, String status) {
        // 获取当前用户信息
        User currentUser = userService.getById(userId);
        if (currentUser == null) {
            throw new ServiceException(Constants.CODE_401, "用户不存在");
        }

        // 创建分页对象
        Page<Order> page = new Page<>(pageNum, pageSize);
        
        // 创建查询条件
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.and(q -> {
            q.eq("buyer_id", userId);
            q.or().eq("seller_id", userId);
        });

        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        
        // 查询订单数据
        Page<Order> orderPage = page(page, queryWrapper);
        List<Order> orderList = orderPage.getRecords();
        
        // 如果没有数据，直接返回空结果
        if (orderList.isEmpty()) {
            Page<OrderDetailDTO> emptyPage = new Page<>(pageNum, pageSize, 0);
            emptyPage.setRecords(List.of());
            return emptyPage;
        }
        
        // 获取所有相关的ID
        List<Long> productIds = orderList.stream()
                .map(Order::getProductId)
                .filter(id -> id != null)
                .collect(Collectors.toList());
        
        List<Long> buyerIds = orderList.stream()
                .map(Order::getBuyerId)
                .filter(id -> id != null)
                .collect(Collectors.toList());
        
        List<Long> sellerIds = orderList.stream()
                .map(Order::getSellerId)
                .filter(id -> id != null)
                .collect(Collectors.toList());
        
        // 批量查询相关数据
        Map<Long, Product> productMap;
        if (!productIds.isEmpty()) {
            List<Product> products = productService.listByIds(productIds);
            productMap = products.stream()
                    .collect(Collectors.toMap(Product::getId, Function.identity()));
        } else {
            productMap = new HashMap<>();
        }

        Map<Long, User> userMap;
        if (!buyerIds.isEmpty() || !sellerIds.isEmpty()) {
            List<Long> allUserIds = new java.util.ArrayList<>(buyerIds);
            allUserIds.addAll(sellerIds);
            List<User> users = userService.listByIds(allUserIds);
            userMap = users.stream()
                    .collect(Collectors.toMap(User::getId, Function.identity()));
        } else {
            userMap = new HashMap<>();
        }

        // 转换为DTO对象
        Page<OrderDetailDTO> resultPage = new Page<>(pageNum, pageSize, orderPage.getTotal());
        List<OrderDetailDTO> records = orderList.stream().map(order -> {
            OrderDetailDTO dto = new OrderDetailDTO();
            // 基本字段复制
            BeanUtils.copyProperties(order, dto);
            
            // 设置商品信息
            Product product = productMap.get(order.getProductId());
            if (product != null) {
                dto.setProductName(product.getName());
                dto.setProductImage(product.getCoverImage());
            }
            
            // 设置买家信息
            User buyer = userMap.get(order.getBuyerId());
            if (buyer != null) {
                dto.setBuyerName(buyer.getNickname());
                dto.setBuyerPhone(buyer.getPhone());
            }
            
            // 设置卖家信息
            User seller = userMap.get(order.getSellerId());
            if (seller != null) {
                dto.setSellerName(seller.getNickname());
                dto.setSellerPhone(seller.getPhone());
            }
            
            return dto;
        }).collect(Collectors.toList());
        
        resultPage.setRecords(records);
        return resultPage;
    }
}
