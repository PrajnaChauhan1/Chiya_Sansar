package com.illam.chiya.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.illam.chiya.model.Products;
import com.illam.chiya.repository.OrderRepository;
import com.illam.chiya.repository.ProductRepository;
import com.illam.chiya.services.ProductsService;

@Service
public class ProductServiceImpl implements ProductsService{
	
	@Autowired
	ProductRepository productRepo;
	
	@Autowired
    private OrderRepository ordersRepo;
	
	 private static final String STORAGE_PATH = "C:\\Users\\utsav\\OneDrive\\Documents\\workspace-spring-tool-suite-4-4.18.0.RELEASE\\Chiya-Sansar\\src\\main\\resources\\static\\images";

	@Override
	public void save(Products product) {
		productRepo.save(product);
		
	}

	@Override
	public List<Products> getProducts() {
		
		return productRepo.findAll();
	}

	@Override
	public String saveImage(MultipartFile image, String name) throws IOException {
	    String safeProductName = name.replaceAll("[^a-zA-Z0-9]", "_");
	    String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf('.'));
	    String fileName = safeProductName + "_" + UUID.randomUUID().toString() + extension;
	    Path imagePath = Paths.get(STORAGE_PATH, fileName);
	    Files.createDirectories(imagePath.getParent());
	    Files.write(imagePath, image.getBytes());
	    return "images/" + fileName;
	}



	@Override
	public byte[] getImage(String imagepath) throws IOException {
		Path filePath = Paths.get(STORAGE_PATH, imagepath);
        return Files.readAllBytes(filePath);
	}
    @Override
	public List<List<Products>> partitionList(List<Products> list, int size) {
	    List<List<Products>> partitions = new ArrayList<>();
	    for (int i = 0; i < list.size(); i += size) {
	        partitions.add(list.subList(i, Math.min(i + size, list.size())));
	    }
	    return partitions;
}

	@Override
	public Products getById(Long id) {
		return productRepo.getReferenceById(id);
	}
	
	
	@Override
	 public Products getProductByOrderId(Long orderId) {
	        return ordersRepo.findProductByOrderId(orderId);
	    }
	
}
