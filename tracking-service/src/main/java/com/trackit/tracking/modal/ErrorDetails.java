package com.trackit.tracking.modal;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorDetails {

	 private Date timestamp;
	   private String description;
	   private String message;
	  

}
