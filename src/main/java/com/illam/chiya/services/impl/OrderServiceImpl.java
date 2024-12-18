package com.illam.chiya.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.illam.chiya.model.Orders;
import com.illam.chiya.repository.OrderRepository;
import com.illam.chiya.services.OrderService;

@Service
public class OrderServiceImpl implements OrderService{

	@Autowired
	OrderRepository orderRepo;
	
	@Override
	public Orders getById(Long id) {
		
		return orderRepo.getReferenceById(id);
	}

	@Override
	public void save(Orders order) {
		orderRepo.save(order);
		
	}

	@Override
	public List<Orders> getOrdersByUser(Long userId) {
		
		return orderRepo.findDeliveredOrdersByUserId(userId);
	}

	@Override
	public List<Orders> getPendingOrdersByUser(Long userId) {
		
		return orderRepo.findOrdersByUserIdExcludingDelivered(userId);
	}

	@Override
	public List<Orders> getPendingOrders() {
		
		return orderRepo.findOrdersByExcludingDelivered();
	}

}
