package com.searchservice.app.domain.port.api;

public interface PublicKeyServicePort {
	String retrievePublicKey(String realmName);
	boolean checkIfPublicKeyExistsInCache();
}
