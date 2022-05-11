package com.searchservice.app.domain.port.api;

import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.user.UserDTO;

public interface UserServicePort {
	Response getToken(UserDTO user);
}