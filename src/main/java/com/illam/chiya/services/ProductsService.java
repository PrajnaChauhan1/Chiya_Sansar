package com.illam.chiya.services;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.illam.chiya.model.Products;

public interface ProductsService {

	void save(Products product);
	
	List<Products> getProducts();
	
	Products getById(Long id);
	
    String saveImage(MultipartFile image, String name) throws IOException;
    
    byte[] getImage(String imagepath) throws IOException;
    
    List<List<Products>> partitionList(List<Products> list, int size);

	Products getProductByOrderId(Long orderId);
	
}
