package com.illam.chiya.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.illam.chiya.model.User;
import com.illam.chiya.repository.UserRepository;
import com.illam.chiya.services.UserService;

@Service
public class UserServiceImpl implements UserService{
	@Autowired
	UserRepository userRepo;
	
	@Override
	public void save(User user) {
		userRepo.save(user);
	}

	@Override
	public User getByEmail(String email) {
		
		return userRepo.getByEmail(email);
	}

	@Override
	public User getByPhone(String phone) {
		
		return userRepo.getByPhone(phone);
	}
	@Override
	public boolean verifyPassword(String password, User user, BCryptPasswordEncoder encoder) {
		if (user.getPassword() != null && encoder.matches(password, user.getPassword())) {
			return true;
		}
		return false;
	}
}
