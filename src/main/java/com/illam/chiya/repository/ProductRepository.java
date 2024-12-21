package com.illam.chiya.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.illam.chiya.model.Products;

public interface ProductRepository extends JpaRepository<Products, Long>{

	 List<Products> findByNameContainingIgnoreCase(String name);
	 
}
