package com.acc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Vikash Kaushik
 * Simple POJO for Account
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
	private Long id;
	private String accountName;
	
	public Account(String accountName) {
		this.accountName = accountName;
	}
}
