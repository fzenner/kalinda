package com.fzenner.datademo.entity.ingredient;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Ingredient {
	private Long id;
	private String name;

	private IngredientType type;
	
	public Ingredient() {
		
	}
	
	public Ingredient(long id, String name, IngredientType type) {
		this.id = id;
		this.name = name;
		this.type = type;

		
	}
	
	public static enum IngredientType {
		WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
	}
}
