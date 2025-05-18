package com.illam.chiya.model;

import java.util.List;

import com.illam.chiya.enums.Tags;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private float price;
    private String quantity;
    private Long stock;
    private String imagePath;
    private String description;
    private String ingredients;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Tags> tags;
}
