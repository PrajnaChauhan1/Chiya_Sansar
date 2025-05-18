package com.illam.chiya.controller;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.illam.chiya.enums.Tags;
import com.illam.chiya.model.Orders;
import com.illam.chiya.model.Products;
import com.illam.chiya.model.User;
import com.illam.chiya.services.OrderService;
import com.illam.chiya.services.ProductsService;
import com.illam.chiya.utils.MailUtils;
import com.illam.chiya.utils.PdfGenerator;
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

	@Autowired
	PdfGenerator pdfgenerator;
	
	@GetMapping("/aboutus")
	public String aboutUs() {

		return "aboutus";
	}

	@GetMapping("/addproduct")
	public String addProduct(HttpSession session, RedirectAttributes attribute,Model model) {
		if (session.getAttribute("admin") == null) {
			attribute.addFlashAttribute("error", "Please login");
			return "redirect:/login";
		}
		List<Tags> tagsList = new ArrayList<>();
        for (Tags tag : Tags.values()) {
            tagsList.add(tag);
        }
        model.addAttribute("tagsList", tagsList);
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
		List<Products> products = productService.getProducts(); 
		List<List<Products>> partitionedProducts = productService.partitionList(products, 3);
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
	public String postOtp(HttpSession session, RedirectAttributes attribute, @RequestParam String otp,Model model) {
		if (session.getAttribute("user") == null) {
			attribute.addFlashAttribute("error", "Please login before buying");
			return "redirect:/login";
		}
		String otpfromsession = (String) session.getAttribute("otp");
		if (!otp.equals(otpfromsession)) {
			attribute.addFlashAttribute("error", "Invalid OTP! Try again");
			return "redirect:/otp";
		}
		User user = (User) session.getAttribute("user");
		Orders order = (Orders) session.getAttribute("order");
		order.setOrderStatus("Pending");
		order.setUser((User) session.getAttribute("user"));
		Products product = productService.getById((Long) session.getAttribute("purchaseattemptproductid"));
		product.setStock(product.getStock() - 1);
		order.setProduct(product);
		productService.save(product);
		orderService.save(order);
		String htmlContent = generateHTMLContent(model, product.getName(), product.getPrice(), LocalDate.now(), order.getDeliveryLocation());
		String filename = "ticket" + LocalTime.now().getNano();
		pdfgenerator.generateTicket(htmlContent, filename);
		String filepath = "C:\\Users\\utsav\\OneDrive\\Documents\\workspace-spring-tool-suite-4-4.18.0.RELEASE\\Chiya-Sansar\\src\\main\\resources\\static\\bills"+filename+".pdf";
		File bill = new File(filepath);
		mailUtils.sendEmailWithAttachment(user.getEmail(), "Your bill for your purchase",
				"Your purchase", bill);
		session.removeAttribute("purchaseattemptproductid");
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
		List<Orders> orders = orderService.getOrdersByUser(user.getId());
		Map<Products, Orders> orderProductsMap = orders.stream().collect(Collectors.toMap(order -> productService.getProductByOrderId(order.getId()), order -> order));		model.addAttribute("orderProductsMap", orderProductsMap);
		return "history";
	}

	@GetMapping("/pendingorders")
	public String pendingOrders(HttpSession session, RedirectAttributes attribute, Model model) {
		if (session.getAttribute("user") == null) {
			attribute.addFlashAttribute("error", "Please login before accessing pending orders.");
			return "redirect:/login";
		}

		User user = (User) session.getAttribute("user");
		List<Orders> pendingOrders = orderService.getPendingOrdersByUser(user.getId());
		Map<Products, Orders> pendingOrderProductsMap = pendingOrders.stream()
				.collect(Collectors.toMap(order -> order.getProduct(), order -> order));
		model.addAttribute("orderProductsMap", pendingOrderProductsMap);
		model.addAttribute("isPending", true);
		return "history";
	}

	@GetMapping("/orders")
	public String orders(HttpSession session, RedirectAttributes attribute, Model model) {
		if (session.getAttribute("admin") == null) {
			attribute.addFlashAttribute("error", "Please login");
			return "redirect:/login";
		}
		List<Orders> orders = orderService.getPendingOrders();
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
		List<String> statuses = Arrays.asList("Pending", "Shipped", "Delivered", "Cancelled");
		model.addAttribute("orderDetails", orderDetails);
		model.addAttribute("statuses", statuses);
		return "orders";
	}

	@GetMapping("/updateorder")
	public String updateOrder(HttpSession session, RedirectAttributes attribute, Model model,
			@RequestParam String orderStatus, @RequestParam Long orderId) {
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

	@GetMapping("/productsforstocks")
	public String productsforstock(HttpSession session, RedirectAttributes attribute, Model model) {
		if (session.getAttribute("admin") == null) {
			attribute.addFlashAttribute("error", "Please login");
			return "redirect:/login";
		}
		List<Products> products = productService.getProducts(); 
		List<List<Products>> partitionedProducts = productService.partitionList(products, 3); 
		model.addAttribute("partitionedProducts", partitionedProducts);
		return "productsforstocks";
	}

	@GetMapping("/productforstock")
	public String productsforstocks(@RequestParam Long id, Long stocktoadd, Model model, RedirectAttributes attribute) {
		Products product = productService.getById(id);
		product.setStock(product.getStock() + stocktoadd);
		productService.save(product);
		attribute.addFlashAttribute("success", "Stock updated to " + product.getStock());
		return "redirect:/productsforstocks";
	}

	@PostMapping("/search")
	public String search(@RequestParam String name, Model model) {
		List<Products> products = productService.getProductBySimiliarName(name);
		List<List<Products>> partitionedProducts = productService.partitionList(products, 3); 
		model.addAttribute("partitionedProducts", partitionedProducts);
		return "allproducts";
	}
	
	public String generateHTMLContent(Model model,String name, Float price, LocalDate date, String location) {
		Context context = new Context();
		context.setVariable("name", name);
		context.setVariable("price", price);
		context.setVariable("date", date);
		context.setVariable("location", location);
		model.asMap().forEach(context::setVariable);
		TemplateEngine templateEngine = new SpringTemplateEngine();
		String html = "<meta charset=\"UTF-8\">\r\n"
				+ "<html xmlns:th=\"http://www.thymeleaf.org\">\r\n"
				+ "\r\n"
				+ "<body>\r\n"
				+ "\r\n"
				+ "    <head>\r\n"
				+ "        <style>\r\n"
				+ "            body {\r\n"
				+ "                display: flex;\r\n"
				+ "                align-items: center;\r\n"
				+ "                justify-content: center;\r\n"
				+ "            }\r\n"
				+ "\r\n"
				+ "            .container {\r\n"
				+ "                display: flex;\r\n"
				+ "                flex-direction: column;\r\n"
				+ "                text-align: center;\r\n"
				+ "                background-color: #FBF9F1;\r\n"
				+ "                border-radius: 28px;\r\n"
				+ "                width: 500px;\r\n"
				+ "                height: 250px;\r\n"
				+ "            }\r\n"
				+ "\r\n"
				+ "            .heading {\r\n"
				+ "                color: #2D5B4E;\r\n"
				+ "                font-weight: bold;\r\n"
				+ "            }\r\n"
				+ "\r\n"
				+ "            .sub-container {\r\n"
				+ "                display: flex;\r\n"
				+ "                flex-direction: row;\r\n"
				+ "                margin-top: 30px;\r\n"
				+ "            }\r\n"
				+ "\r\n"
				+ "            .mini-container {\r\n"
				+ "                text-align: left;\r\n"
				+ "                margin-left: 50px;\r\n"
				+ "            }\r\n"
				+ "\r\n"
				+ "            .info {\r\n"
				+ "                color: black;\r\n"
				+ "            }\r\n"
				+ "        </style>\r\n"
				+ "    </head>\r\n"
				+ "    <div class=\"container\">\r\n"
				+ "        <h1 class=\"heading\">Chiya Sansar</h1>\r\n"
				+ "        <div class=\"sub-container\">\r\n"
				+ "            <div class=\"mini-container\">\r\n"
				+ "                <p class=\"info\">Name: <span th:text=\"${name}\">Product Name</span></p>\r\n"
				+ "                <p class=\"info\">Price: Rs. <span th:text=\"${price}\">1200</span></p>\r\n"
				+ "            </div>\r\n"
				+ "            <div class=\"mini-container\" style=\"margin-left: 100px;\">\r\n"
				+ "                <p class=\"info\">\r\n"
				+ "                    Date: <span th:text=\"${date}\">Date</span>\r\n"
				+ "                </p>\r\n"
				+ "                <p class=\"info\">\r\n"
				+ "                    Location: <span th:text=\"${location}\">Kathmandu</span>\r\n"
				+ "                </p>\r\n"
				+ "            </div>\r\n"
				+ "        </div>\r\n"
				+ "    </div>\r\n"
				+ "</body>\r\n"
				+ "\r\n"
				+ "</html>\r\n"
				+ "";
		String content = templateEngine.process(html, context);
		return content;
	}

}
