package com.illam.chiya.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.illam.chiya.model.Products;

public interface ProductRepository extends JpaRepository<Products, Long>{

	 
}
