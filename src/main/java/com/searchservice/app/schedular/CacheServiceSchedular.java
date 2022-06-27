package com.searchservice.app.schedular;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.searchservice.app.config.UserPermissionConfigProperties;

@Service
public class CacheServiceSchedular {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired 
	private UserPermissionConfigProperties userPermissionConfigProperties;
	
    @Autowired
    CacheManager cacheManager;

    public void evictAllCaches() {
        cacheManager.getCacheNames().stream()
          .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }
    
    public void evictUserPermissionsCaches() {
        cacheManager.getCacheNames().stream()
          .forEach(cacheName -> cacheManager.getCache(userPermissionConfigProperties.getKey()).clear());
    }

    @Scheduled(fixedRate = 300000)
    public void evictUserPermissionsCachesAtIntervals() {
    	logger.debug("Cache eviction for entry: {} is started", userPermissionConfigProperties.getKey());
    	evictUserPermissionsCaches();
    }

}