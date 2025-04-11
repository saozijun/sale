package com.cen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cen.controller.dto.ProductDetailDTO;
import com.cen.entity.BasePlace;
import com.cen.entity.Product;
import com.cen.entity.ProductP;
import com.cen.entity.User;
import com.cen.mapper.ProductPMapper;
import com.cen.service.IBasePlaceService;
import com.cen.service.IProductPService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cen.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author volcano
 * @since 2025-04-09
 */
@Service
public class ProductPServiceImpl extends ServiceImpl<ProductPMapper, ProductP> implements IProductPService {
    @Resource
    private IBasePlaceService basePlaceService;

    @Resource
    private IUserService userService;

    @Override
    public Page<ProductDetailDTO> findPublishedProductPage(Integer pageNum, Integer pageSize, String name, String origin) {
        Page<ProductP> page = new Page<>(pageNum, pageSize);

        // 创建查询条件
        QueryWrapper<ProductP> queryWrapper = new QueryWrapper<>();
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
        Page<ProductP> productPage = page(page, queryWrapper);
        List<ProductP> productList = productPage.getRecords();

        // 如果没有数据，直接返回空结果
        if (productList.isEmpty()) {
            Page<ProductDetailDTO> emptyPage = new Page<>(pageNum, pageSize, 0);
            emptyPage.setRecords(List.of());
            return emptyPage;
        }

        // 获取所有产地ID
        List<Long> originPlaceIds = productList.stream()
                .map(ProductP::getOriginPlaceId)
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
