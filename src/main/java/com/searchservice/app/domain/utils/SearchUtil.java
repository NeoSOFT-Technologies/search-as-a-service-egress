package com.searchservice.app.domain.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class SearchUtil {
	
	private SearchUtil() {}
	
	public static Map<Integer, String> getMultivaluedQueryFields(List<String> queryFields, JSONArray currentTableSchema) {
		
		log.info("currtableSchema >>>>>> {}", currentTableSchema);
		
		for(Object s: currentTableSchema) {
			log.info("present schema >>>>>>> {}", s);
		}
		
		log.info("getMultiValQueryFields method executed.");
		
		return new HashMap<>();
	}
	
	
	public static boolean isArrayOfStrings(String data) {
		if(data.substring(0, 1).equals("[") && data.substring(data.length()-1, data.length()).equals("]"))
			return true;
		return false;
	}
	
	
	public static boolean isSubstringsCommaSeparated(String str) {
		
		return false;
	}
	
	
}
