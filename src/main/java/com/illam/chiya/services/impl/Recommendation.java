package com.illam.chiya.services.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.illam.chiya.enums.Tags;
import com.illam.chiya.model.Orders;
import com.illam.chiya.model.Products;
import com.illam.chiya.model.User;
import com.illam.chiya.services.ProductsService;
import com.illam.chiya.services.UserService;

@Component
public class Recommendation {

	@Autowired
	UserService userService;

	@Autowired
	ProductsService productService;

	public List<Products> getRecommendationsByUser(User user) {
		List<Orders> userOrders = user.getOrders();
		Set<Products> userPurchasedProducts = userOrders.stream().map(Orders::getProduct).collect(Collectors.toSet());

		// Collect all tags from previously purchased products
		Set<Tags> userPurchasedTags = userPurchasedProducts.stream().flatMap(product -> product.getTags().stream())
				.collect(Collectors.toSet());

		// Get all products matching any of those tags
		List<Products> candidateProducts = productService.getProductsByTags(new ArrayList<>(userPurchasedTags));

		// Remove already purchased products by comparing product IDs
		candidateProducts
				.removeIf(product -> userPurchasedProducts.stream().anyMatch(p -> p.getId().equals(product.getId())));

		// Create a map to hold products and their matching tag count
		Map<Products, Integer> productMatchCount = new HashMap<>();

		for (Products product : candidateProducts) {
			int matchCount = 0;
			for (Tags tag : product.getTags()) {
				if (userPurchasedTags.contains(tag)) {
					matchCount++;
				}
			}
			productMatchCount.put(product, matchCount);
		}

		// Sort products by number of matching tags (descending)
		return productMatchCount.entrySet().stream().sorted(Map.Entry.<Products, Integer>comparingByValue().reversed())
				.map(Map.Entry::getKey).collect(Collectors.toList());
	}
}
