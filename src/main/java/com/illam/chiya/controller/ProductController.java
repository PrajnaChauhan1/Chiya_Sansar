package com.illam.chiya.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.illam.chiya.model.Orders;
import com.illam.chiya.model.Products;
import com.illam.chiya.model.User;
import com.illam.chiya.services.OrderService;
import com.illam.chiya.services.ProductsService;
import com.illam.chiya.utils.MailUtils;
import com.itextpdf.io.exceptions.IOException;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProductController {

	@Autowired
	ProductsService productService;

	@Autowired
	OrderService orderService;

	@Autowired
	MailUtils mailUtils;

	@GetMapping("/aboutus")
	public String aboutUs() {

		return "aboutus";
	}

	@GetMapping("/addproduct")
	public String addProduct(HttpSession session, RedirectAttributes attribute) {
		if (session.getAttribute("admin") == null) {
			attribute.addFlashAttribute("error", "Please login");
			return "redirect:/login";
		}
		return "addProduct";

	}

	@PostMapping("/addproduct")
	public String addProductPost(HttpSession session, RedirectAttributes attribute, @ModelAttribute Products product,
			@RequestParam("image") MultipartFile image) {
		if (session.getAttribute("admin") == null) {
			attribute.addFlashAttribute("error", "Please login");
			return "redirect:/login";
		}
		try {
			String imagePath = null;
			try {
				imagePath = productService.saveImage(image, product.getName());
			} catch (java.io.IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			product.setImagePath(imagePath);
			productService.save(product);
			attribute.addFlashAttribute("success", "Product added successfully.");
			return "redirect:/adminDashboard";
		} catch (IOException e) {
			attribute.addFlashAttribute("error", "Could not upload the product");
			return "redirect:/addproduct";
		}

	}

	@GetMapping("/allproducts")
	public String allProducts(Model model) {
		List<Products> products = productService.getProducts(); // Original list
		List<List<Products>> partitionedProducts = productService.partitionList(products, 3); // Partitioned
		model.addAttribute("partitionedProducts", partitionedProducts);
		return "allproducts";
	}

	@GetMapping("/product")
	public String product(Model model, @RequestParam("id") Long id) {
		Products product = productService.getById(id);
		model.addAttribute("product", product);
		return "productShowcase";
	}

	@GetMapping("/buy")
	public String buy(@RequestParam Long id, HttpSession session, RedirectAttributes attribute) {
		session.setAttribute("purchaseattemptproductid", id);
		if (session.getAttribute("user") == null) {
			attribute.addFlashAttribute("error", "Please login before buying");
			return "redirect:/login";
		}

		return "buyerinfo";
	}

	@PostMapping("/buy")
	public String postBuy(HttpSession session, RedirectAttributes attribute, @ModelAttribute Orders order,
			@RequestParam String paymentmethod) {
		if (session.getAttribute("user") == null) {
			attribute.addFlashAttribute("error", "Please login before buying");
			return "redirect:/login";
		}
		session.setAttribute("order", order);
		if (paymentmethod.equals("esewa")) {
			return "redirect:/esewa";
		} else {
			return "redirect:/imepay";
		}
	}

	@GetMapping("/esewa")
	public String esewa(HttpSession session, RedirectAttributes attribute) {
		if (session.getAttribute("user") == null) {
			attribute.addFlashAttribute("error", "Please login before buying");
			return "redirect:/login";
		}
		return "esewa";
	}

	@GetMapping("/imepay")
	public String imepay(HttpSession session, RedirectAttributes attribute) {
		if (session.getAttribute("user") == null) {
			attribute.addFlashAttribute("error", "Please login before buying");
			return "redirect:/login";
		}
		return "imepay";
	}

	@PostMapping("/payment")
	public String postPayment(HttpSession session, RedirectAttributes attribute, @RequestParam String id) {
		if (session.getAttribute("user") == null) {
			attribute.addFlashAttribute("error", "Please login before buying");
			return "redirect:/login";
		}
		Products product = productService.getById((Long) session.getAttribute("purchaseattemptproductid"));
		Random random = new Random();
		int randomNumber = random.nextInt(900000) + 100000;
		String otp = Integer.toString(randomNumber);
		session.setAttribute("otp", otp);
		mailUtils.sendEmail(id, "OTP for your payment of Rs." + product.getPrice() + " is " + otp + ".", "OTP");
		return "redirect:/otp";
	}

	@GetMapping("/otp")
	public String otp(HttpSession session, RedirectAttributes attribute) {
		if (session.getAttribute("user") == null) {
			attribute.addFlashAttribute("error", "Please login before buying");
			return "redirect:/login";
		}
		return "otp";
	}

	@PostMapping("/otp")
	public String postOtp(HttpSession session, RedirectAttributes attribute, @RequestParam String otp) {
		if (session.getAttribute("user") == null) {
			attribute.addFlashAttribute("error", "Please login before buying");
			return "redirect:/login";
		}
		String otpfromsession = (String) session.getAttribute("otp");
		if (!otp.equals(otpfromsession)) {
			attribute.addFlashAttribute("error", "Invalid OTP! Try again");
			return "redirect:/otp";
		}
		Orders order = (Orders) session.getAttribute("order");
		order.setOrderStatus("Pending");
		order.setUser((User) session.getAttribute("user"));
		Products product = productService.getById((Long) session.getAttribute("purchaseattemptproductid"));
		product.setStock(product.getStock() - 1);
		order.setProduct(product);
		productService.save(product);
		orderService.save(order);
		return "redirect:/purchasesuccess";
	}

	@GetMapping("/purchasesuccess")
	public String purchasesuccess(RedirectAttributes attribute) {
		attribute.addFlashAttribute("success", "Your purchase was successfull");
		return "redirect:/userDashboard";
	}

	@GetMapping("/history")
	public String history(HttpSession session, RedirectAttributes attribute, Model model) {
		if (session.getAttribute("user") == null) {
			attribute.addFlashAttribute("error", "Please login before accessing your order history.");
			return "redirect:/login";
		}

		User user = (User) session.getAttribute("user");

		// Retrieve orders for the current user
		List<Orders> orders = orderService.getOrdersByUser(user.getId());

		// Fetch the associated products for each order
		Map<Products, Orders> orderProductsMap = orders.stream()
				.collect(Collectors.toMap(order -> productService.getProductByOrderId(order.getId()), // Assume this
																										// fetches the
																										// product
																										// linked to the
																										// order
						order -> order));

		// Add data to the model
		model.addAttribute("orderProductsMap", orderProductsMap);
		return "history";
	}

	@GetMapping("/pendingorders")
	public String pendingOrders(HttpSession session, RedirectAttributes attribute, Model model) {
		if (session.getAttribute("user") == null) {
			attribute.addFlashAttribute("error", "Please login before accessing pending orders.");
			return "redirect:/login";
		}

		User user = (User) session.getAttribute("user");

		// Fetch pending orders for the user
		List<Orders> pendingOrders = orderService.getPendingOrdersByUser(user.getId());

		// Map products to their respective pending orders
		Map<Products, Orders> pendingOrderProductsMap = pendingOrders.stream()
				.collect(Collectors.toMap(order -> order.getProduct(),order -> order));
		model.addAttribute("orderProductsMap", pendingOrderProductsMap);
		model.addAttribute("isPending", true); // This will differentiate pending orders from order history
		return "history";
	}
	@GetMapping("/orders")
	public String orders(HttpSession session, RedirectAttributes attribute, Model model) {
	    if (session.getAttribute("admin") == null) {
	        attribute.addFlashAttribute("error", "Please login");
	        return "redirect:/login";
	    }

	    List<Orders> orders = orderService.getPendingOrders();
	    // Map order details with associated product information
	    List<Map<String, Object>> orderDetails = orders.stream().map(order -> {
	    	System.out.println(order);
	        Map<String, Object> details = new HashMap<>();
	        details.put("productName", order.getProduct().getName());
	        details.put("price", order.getProduct().getPrice());
	        details.put("ingredients", order.getProduct().getIngredients());
	        details.put("deliveryLocation", order.getDeliveryLocation());
	        details.put("orderStatus", order.getOrderStatus());
	        details.put("receiverName", order.getReceiverName());
	        details.put("receiverPhone", order.getReceiverPhone());
	        details.put("orderId", order.getId());
	        return details;
	    }).collect(Collectors.toList());

	    // List of statuses for the dropdown
	    List<String> statuses = Arrays.asList("Pending", "Shipped", "Delivered", "Cancelled");

	    model.addAttribute("orderDetails", orderDetails);
	    model.addAttribute("statuses", statuses);
	    return "orders";
	}


	
	@GetMapping("/updateorder")
	public String updateOrder(HttpSession session, RedirectAttributes attribute, Model model,@RequestParam String orderStatus, @RequestParam Long orderId){
		if (session.getAttribute("admin") == null) {
	        attribute.addFlashAttribute("error", "Please login");
	        return "redirect:/login";
	    }
       Orders order = orderService.getById(orderId);		
	   order.setOrderStatus(orderStatus);
	   orderService.save(order);
	   attribute.addFlashAttribute("success", "Order status updated");
	   return "redirect:/adminDashboard";
	}


}
