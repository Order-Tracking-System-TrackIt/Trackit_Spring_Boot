package com.trackit.auth.entity;

import lombok.AllArgsConstructor;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import jakarta.persistence.Entity;
import jakarta.persistence.*;



@Entity
@Table(name = "customer")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Authentication {
	
	@Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	private String name;
	
    private String email;
    private String password;
    private String role;

	

}
