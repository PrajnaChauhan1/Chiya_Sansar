package com.illam.chiya.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.illam.chiya.model.User;
import com.illam.chiya.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

	@Autowired
	UserService userService;

	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	
	@GetMapping("/adminDashboard")
	public String adminDashboard(HttpSession session, Model model, RedirectAttributes attribute) {
		if(session.getAttribute("admin")==null) {
			attribute.addFlashAttribute("error", "Please login");
			return "redirect:/login";
		}
		User user =(User) session.getAttribute("admin");
		model.addAttribute("adminName", user.getFullName());
		return "adminDashboard";
	}
	
	
	
}
