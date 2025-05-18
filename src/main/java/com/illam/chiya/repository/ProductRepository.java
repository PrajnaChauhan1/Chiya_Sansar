package com.illam.chiya.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.illam.chiya.enums.Tags;
import com.illam.chiya.model.Products;

public interface ProductRepository extends JpaRepository<Products, Long>{

	 List<Products> findByNameContainingIgnoreCase(String name);
	 
	 @Query("SELECT DISTINCT p FROM Products p JOIN p.tags t WHERE t IN :tags")
	 List<Products> findByTagsIn(List<Tags> tags);
	 
}
