package com.cen.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cen.controller.dto.OrderDetailDTO;
import com.cen.entity.OrderP;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author volcano
 * @since 2025-04-10
 */
public interface IOrderPService extends IService<OrderP> {
    Page<OrderDetailDTO> findOrderDetailPage(Integer pageNum, Integer pageSize, Long userId, String status);
}
