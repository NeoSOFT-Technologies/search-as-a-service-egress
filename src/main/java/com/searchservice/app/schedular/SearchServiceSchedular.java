package com.searchservice.app.schedular;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.searchservice.app.domain.port.api.PublicKeyServicePort;

@Component
public class SearchServiceSchedular {

	 
    @Autowired
	PublicKeyServicePort publicKeyServicePort;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	
	@Scheduled(fixedRateString = "${schedular-durations.public-key-update}")
	public void updatePublicKeyValueInCache() {
		logger.debug("Check for Public Key Updation in Cache Started");
		publicKeyServicePort.checkIfPublicKeyExistsInCache();
	}

}