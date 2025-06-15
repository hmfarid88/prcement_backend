package com.example.bake_boss_backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bake_boss_backend.entity.OrderInfo;

public interface OrderInfoRepository extends JpaRepository<OrderInfo, Long> {
    @Query("SELECT ps.orderId, ps.retailer, ps.productName, ps.orderNote, " +
    "CASE WHEN ps.orderQty > COALESCE(ps.deliveredQty, 0) THEN (ps.orderQty - COALESCE(ps.deliveredQty, 0)) ELSE 0 END AS remainingQty " +
    "FROM OrderInfo ps")
List<Object[]> findAllList();

 @Query("SELECT o FROM OrderInfo o WHERE o.username = :username AND (o.orderQty - o.deliveredQty) > 0")
    List<OrderInfo> findByUsernameAndPendingQuantity(@Param("username") String username);

    
    List<OrderInfo> findByOrderId(Long orderId);

    
}
