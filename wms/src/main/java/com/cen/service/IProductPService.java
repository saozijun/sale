package com.cen.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cen.controller.dto.ProductDetailDTO;
import com.cen.entity.ProductP;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author volcano
 * @since 2025-04-09
 */
public interface IProductPService extends IService<ProductP> {
    /**
     * 分页查询已发布商品详情
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param name 商品名称(可选)
     * @param origin 产地(可选)
     * @return 分页结果
     */
    Page<ProductDetailDTO> findPublishedProductPage(Integer pageNum, Integer pageSize, String name, String origin);
}
