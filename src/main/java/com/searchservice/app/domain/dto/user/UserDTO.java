package com.searchservice.app.domain.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class UserDTO {
	
	private String username;
	private String password;

	public UserDTO(String username, String password) {
		this.username = username;
		this.password = password;
	}
}