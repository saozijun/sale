package com.cen.mapper;

import com.cen.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author volcano
 * @since 2025-04-09
 */
public interface UserMapper extends BaseMapper<User> {
    List<User> getStudentsByCourseId(Long courseId);
    
    @Select("SELECT COUNT(*) as total FROM sys_user")
    Integer getTotalUsers();
    
    @Select("SELECT COUNT(*) as active FROM sys_user WHERE status = 1")
    Integer getActiveUsers();
    
    @Select("SELECT role, COUNT(*) as count FROM sys_user GROUP BY role")
    List<Map<String, Object>> getUserRoleDistribution();
    
    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m') as month, COUNT(*) as count FROM sys_user GROUP BY DATE_FORMAT(create_time, '%Y-%m') ORDER BY month DESC LIMIT 12")
    List<Map<String, Object>> getUserGrowthTrend();
    
    // 种植户(growers)相关统计
    @Select("SELECT COUNT(*) as total FROM sys_product WHERE user_id = #{userId}")
    Integer getGrowerProducts(@Param("userId") Long userId);
    
    @Select("SELECT COUNT(*) as total FROM sys_product_inventory WHERE product_id IN (SELECT id FROM sys_product WHERE user_id = #{userId})")
    Integer getGrowerInventory(@Param("userId") Long userId);
    
    @Select("SELECT SUM(total_stock) as total FROM sys_product_inventory WHERE product_id IN (SELECT id FROM sys_product WHERE user_id = #{userId})")
    Integer getGrowerTotalStock(@Param("userId") Long userId);
    
    @Select("SELECT SUM(reserved_stock) as total FROM sys_product_inventory WHERE product_id IN (SELECT id FROM sys_product WHERE user_id = #{userId})")
    Integer getGrowerReservedStock(@Param("userId") Long userId);
    
    @Select("SELECT status, COUNT(*) as count FROM sys_order WHERE seller_id = #{userId} GROUP BY status")
    List<Map<String, Object>> getGrowerOrderStatus(@Param("userId") Long userId);
    
    @Select("SELECT DATE_FORMAT(created_at, '%Y-%m') as month, COUNT(*) as count, SUM(total_amount) as amount FROM sys_order WHERE seller_id = #{userId} GROUP BY DATE_FORMAT(created_at, '%Y-%m') ORDER BY month DESC LIMIT 12")
    List<Map<String, Object>> getGrowerOrderTrend(@Param("userId") Long userId);
    
    @Select("SELECT p.name, COUNT(o.id) as count FROM sys_order o LEFT JOIN sys_product p ON o.product_id = p.id WHERE p.user_id = #{userId} GROUP BY p.id, p.name ORDER BY count DESC LIMIT 10")
    List<Map<String, Object>> getGrowerTopProducts(@Param("userId") Long userId);
    
    @Select("SELECT bp.name as place, COUNT(p.id) as count FROM sys_product p LEFT JOIN sys_base_place bp ON p.origin_place_id = bp.id WHERE p.user_id = #{userId} GROUP BY bp.id, bp.name")
    List<Map<String, Object>> getGrowerProductPlaceDistribution(@Param("userId") Long userId);
    
    // 批发商(wholesaler)相关统计
    @Select("SELECT COUNT(*) as total FROM sys_product_p WHERE user_id = #{userId}")
    Integer getWholesalerProducts(@Param("userId") Long userId);
    
    @Select("SELECT COUNT(*) as total FROM sys_product_inventory_p WHERE product_id IN (SELECT id FROM sys_product_p WHERE user_id = #{userId})")
    Integer getWholesalerInventory(@Param("userId") Long userId);
    
    @Select("SELECT SUM(total_stock) as total FROM sys_product_inventory_p WHERE product_id IN (SELECT id FROM sys_product_p WHERE user_id = #{userId})")
    Integer getWholesalerTotalStock(@Param("userId") Long userId);
    
    @Select("SELECT SUM(reserved_stock) as total FROM sys_product_inventory_p WHERE product_id IN (SELECT id FROM sys_product_p WHERE user_id = #{userId})")
    Integer getWholesalerReservedStock(@Param("userId") Long userId);
    
    @Select("SELECT status, COUNT(*) as count FROM sys_order_p WHERE seller_id = #{userId} GROUP BY status")
    List<Map<String, Object>> getWholesalerOrderStatus(@Param("userId") Long userId);
    
    @Select("SELECT DATE_FORMAT(created_at, '%Y-%m') as month, COUNT(*) as count, SUM(total_amount) as amount FROM sys_order_p WHERE seller_id = #{userId} GROUP BY DATE_FORMAT(created_at, '%Y-%m') ORDER BY month DESC LIMIT 12")
    List<Map<String, Object>> getWholesalerOrderTrend(@Param("userId") Long userId);
    
    @Select("SELECT p.name, COUNT(o.id) as count FROM sys_order_p o LEFT JOIN sys_product_p p ON o.product_id = p.id WHERE o.seller_id = #{userId} GROUP BY p.id, p.name ORDER BY count DESC LIMIT 10")
    List<Map<String, Object>> getWholesalerTopProducts(@Param("userId") Long userId);
    
    @Select("SELECT bp.name as place, COUNT(p.id) as count FROM sys_product_p p LEFT JOIN sys_base_place bp ON p.origin_place_id = bp.id WHERE p.user_id = #{userId} GROUP BY bp.id, bp.name")
    List<Map<String, Object>> getWholesalerProductPlaceDistribution(@Param("userId") Long userId);
    
    @Select("SELECT COUNT(*) as total FROM sys_product WHERE status = 1")
    Integer getTotalProducts();
    
    @Select("SELECT COUNT(*) as total FROM sys_product_inventory")
    Integer getTotalInventory();
    
    @Select("SELECT SUM(total_stock) as total FROM sys_product_inventory")
    Integer getTotalStock();
    
    @Select("SELECT SUM(reserved_stock) as total FROM sys_product_inventory")
    Integer getTotalReservedStock();
    
    @Select("SELECT status, COUNT(*) as count FROM sys_order GROUP BY status")
    List<Map<String, Object>> getOrderStatusDistribution();
    
    @Select("SELECT DATE_FORMAT(created_at, '%Y-%m') as month, COUNT(*) as count, SUM(total_amount) as amount FROM sys_order GROUP BY DATE_FORMAT(created_at, '%Y-%m') ORDER BY month DESC LIMIT 12")
    List<Map<String, Object>> getOrderTrend();
    
    @Select("SELECT p.name, COUNT(o.id) as count FROM sys_order o LEFT JOIN sys_product p ON o.product_id = p.id GROUP BY p.id, p.name ORDER BY count DESC LIMIT 10")
    List<Map<String, Object>> getTopProducts();
    
    @Select("SELECT bp.name as place, COUNT(p.id) as count FROM sys_product p LEFT JOIN sys_base_place bp ON p.origin_place_id = bp.id GROUP BY bp.id, bp.name")
    List<Map<String, Object>> getProductPlaceDistribution();
}
