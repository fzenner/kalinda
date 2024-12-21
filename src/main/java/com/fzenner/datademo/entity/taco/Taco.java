package com.fzenner.datademo.entity.taco;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;

import org.springframework.data.annotation.Transient;

import com.fzenner.datademo.entity.ingredient.Ingredient;
import com.kewebsi.controller.DtoAssistant;

import lombok.Data;

@Data
public class Taco {
	private Long id;
	private String name;

	private LocalDateTime createdAt;

	public Taco() {
	}

	public Taco(Long id, String name) {
		this.id=id;
		this.name=name;
	}


	@Override
	public String toString() {
		String result = String.format("Taco: id:%d, name: %s, createdAt: %s",  id, name, createdAt == null ? "null" : createdAt.toString());
		return result;
	}

	
	
}