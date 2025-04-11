package com.cen.service.impl;

import com.cen.entity.Product;
import com.cen.entity.BasePlace;
import com.cen.entity.User;
import com.cen.mapper.ProductMapper;
import com.cen.mapper.BasePlaceMapper;
import com.cen.service.IProductService;
import com.cen.service.IBasePlaceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cen.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cen.controller.dto.ProductDetailDTO;
import org.springframework.beans.BeanUtils;

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
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {
    
    @Resource
    private IBasePlaceService basePlaceService;

    @Resource
    private IUserService userService;

    @Override
    public Page<ProductDetailDTO> findPublishedProductPage(Integer pageNum, Integer pageSize, String name, String origin) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        
        // 创建查询条件
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        // 只查询状态为已发布的商品
        queryWrapper.eq("status", 1);
        
        // 添加可选过滤条件
        if (name != null && !name.isEmpty()) {
            queryWrapper.like("name", name);
        }
        if (origin != null && !origin.isEmpty()) {
            queryWrapper.eq("origin", origin);
        }
        
        // 查询数据库
        Page<Product> productPage = page(page, queryWrapper);
        List<Product> productList = productPage.getRecords();
        
        // 如果没有数据，直接返回空结果
        if (productList.isEmpty()) {
            Page<ProductDetailDTO> emptyPage = new Page<>(pageNum, pageSize, 0);
            emptyPage.setRecords(List.of());
            return emptyPage;
        }
                
        // 获取所有产地ID
        List<Long> originPlaceIds = productList.stream()
                .map(Product::getOriginPlaceId)
                .filter(id -> id != null)
                .collect(Collectors.toList());
        
        // 批量查询产地信息
        Map<Long, BasePlace> placeMap;
        if (!originPlaceIds.isEmpty()) {
            List<BasePlace> places = basePlaceService.listByIds(originPlaceIds);
            placeMap = places.stream()
                    .collect(Collectors.toMap(BasePlace::getId, Function.identity()));
        } else {
            placeMap = new HashMap<>();
        }
        // 转换为DTO对象
        Page<ProductDetailDTO> resultPage = new Page<>(pageNum, pageSize, productPage.getTotal());
        List<ProductDetailDTO> records = productList.stream().map(product -> {
            ProductDetailDTO dto = new ProductDetailDTO();
            // 基本字段复制
            BeanUtils.copyProperties(product, dto);
            dto.setCreateTime(product.getCreatedAt());  // 创建时间映射

            // 获取用户信息
            User user = userService.getById(product.getUserId());
            dto.setManufacturer(user.getNickname());
            dto.setContact(user.getPhone());
            
            // 设置产地信息
            BasePlace place = placeMap.get(product.getOriginPlaceId());
            if (place != null) {
                dto.setOrigin(place.getName());
            }
            
            return dto;
        }).collect(Collectors.toList());
        
        resultPage.setRecords(records);
        return resultPage;
    }
}
