package com.illam.chiya.services;

import java.util.List;

import com.illam.chiya.model.Orders;

public interface OrderService {
    
	Orders getById(Long id);
	
	void save(Orders order);
	
	List<Orders> getOrdersByUser(Long userId);
	
	List<Orders> getPendingOrdersByUser(Long userId);
	
	List<Orders> getPendingOrders();
	
}
