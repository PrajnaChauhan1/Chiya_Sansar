package com.illam.chiya.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Orders {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String receiverName;
	private String receiverPhone;
	private String orderStatus;
	private String deliveryLocation;
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	@ManyToOne
    @JoinColumn(name = "product_id", nullable = false) // Link each order to a product
    private Products product;
}
