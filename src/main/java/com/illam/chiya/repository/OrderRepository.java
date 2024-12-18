package com.illam.chiya.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.illam.chiya.model.Orders;
import com.illam.chiya.model.Products;

public interface OrderRepository extends JpaRepository<Orders, Long>{
	
    
	@Query("SELECT o FROM Orders o WHERE o.user.id = :userId AND o.orderStatus = 'delivered'")
    List<Orders> findDeliveredOrdersByUserId(@Param("userId") Long userId);
	
	@Query("SELECT o FROM Orders o WHERE o.user.id = :userId AND o.orderStatus <> 'delivered'")
	List<Orders> findOrdersByUserIdExcludingDelivered(@Param("userId") Long userId);
	
	@Query("SELECT o FROM Orders o WHERE o.orderStatus <> 'delivered'")
	List<Orders> findOrdersByExcludingDelivered();
	

	@Query("SELECT o.product FROM Orders o WHERE o.id = :orderId")
    Products findProductByOrderId(@Param("orderId") Long orderId);
}
