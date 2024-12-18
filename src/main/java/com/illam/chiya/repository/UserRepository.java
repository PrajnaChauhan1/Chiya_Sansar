package com.illam.chiya.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.illam.chiya.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
User getByEmail(String Email);
	
	User getByPhone(String Phone);
}
