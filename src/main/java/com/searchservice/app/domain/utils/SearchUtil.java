package com.searchservice.app.domain.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.searchservice.app.rest.errors.OperationNotAllowedException;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class SearchUtil {
	
	private SearchUtil() {}
	
	
	public static boolean validateSearchQueryInputs(
			JSONArray currentTableSchema, 
			List<String> queryFieldList, 
			List<String> searchTermList) {
		// Validate queryFields & searchTerms: multifield
		// Expect same no. of <comma separated values> in queryField & searchTerm
		boolean isSearchQueryCountValidated = (queryFieldList.size() == searchTermList.size());
		if(!isSearchQueryCountValidated)
			throw new OperationNotAllowedException(
					406, 
					"Mismatch found in queryField and searchTerm counts. Please provide same number of values");
		
		boolean isSearchQueryValidated = false;
		if(currentTableSchema.isEmpty()) {
			/** Microservice is down
			 * Multivalue fields validation can't be performed
			 */
			// If MultivalueFields could not be validated; microservice is down, then DO STANDARDIZED VALIDATION
			// No Array values in search term
			isSearchQueryValidated = searchTermList.stream().allMatch(SearchUtil::isNotArrayOfStrings);
			// If Standardized validation unsuccessful, throw exception
			if(!isSearchQueryValidated)
				throw new OperationNotAllowedException(
						406, 
						"Microservice is down, multivalue fields couldn't be validated. "
						+ "Please provide single-valued search terms only");
			
		} else {
			// Check if Multivalue queryField is present
			Map<Integer, String> multiValuedQueryFieldsMap = SearchUtil.getMultivaluedQueryFields(queryFieldList, currentTableSchema);
			// Validate queryFields & searchTerms: multivalue --> array of searchTerms
			boolean isMultivalueSearchTermValidated = true;
			boolean isNonMultivalueSearchTermValidated = true;
			if(!multiValuedQueryFieldsMap.isEmpty()) {
				// Validate for Multivalue fields having array of values
				for(Integer idx: multiValuedQueryFieldsMap.keySet()) {
					isMultivalueSearchTermValidated = SearchUtil.isArrayOfStrings(searchTermList.get(idx));
					if(!isMultivalueSearchTermValidated)
						break;
				}
				if(!isMultivalueSearchTermValidated)
					throw new OperationNotAllowedException(
							406, 
							"Please provide \"array of values\" for all multivalue query-fields");	
				// Validate for Non-multivalue fields having non-array of values in search terms
				Set<Integer> multivalueFieldsIdxs = multiValuedQueryFieldsMap.keySet();
				for(int i=0; i<searchTermList.size(); i++) {
					if(!multivalueFieldsIdxs.contains(i)) {
						isNonMultivalueSearchTermValidated = SearchUtil.isNotArrayOfStrings(searchTermList.get(i));
						if(!isNonMultivalueSearchTermValidated)
							break;
					}
				}
				if(!isNonMultivalueSearchTermValidated)
					throw new OperationNotAllowedException(
							406, 
							"Please provide \"single-valued search terms\" for all non-multivalue query-fields");
				else
					isSearchQueryValidated = true;
					
			} else {
				// No Array values in search term
				isSearchQueryValidated = searchTermList.stream().allMatch(SearchUtil::isNotArrayOfStrings);
				// If Standardized validation unsuccessful, throw exception
				if(!isSearchQueryValidated)
					throw new OperationNotAllowedException(
							406, 
							"There're no Multivalue queryFields here. "
							+ "Please provide single-valued search terms only");
			}
		}
		
		return isSearchQueryValidated;
	}
	
	
	public static Map<Integer, String> getMultivaluedQueryFields(List<String> queryFields, JSONArray currentTableSchema) {
		Map<Integer, String> multiValuedQueryFieldsMap = new HashMap<>();
		currentTableSchema.forEach(
				col -> {
					for(int i=0; i<queryFields.size(); i++) {
						String qF = queryFields.get(i);
						JSONObject colObj = (JSONObject)col;
						if(colObj.get("name").equals(qF) && colObj.get("multiValue").equals(true))
							multiValuedQueryFieldsMap.put(i, qF);
					}
				});

		return multiValuedQueryFieldsMap;
	}
	
	
	public static Map<Integer, List<String>> getMultivaluedSearchTerms(
			List<String> queryFields, JSONArray currentTableSchema, List<String> searchTerms) {
		
		Map<Integer, String> multivaluedQueryFieldsMap = getMultivaluedQueryFields(queryFields, currentTableSchema);
		Map<Integer, List<String>> multivaluedSearchTermsMap = new HashMap<>();
		Set<Integer> multivaluedQueryFieldsIndxs = multivaluedQueryFieldsMap.keySet();
		multivaluedQueryFieldsIndxs.forEach(
				idx -> {
					String arrayData = searchTerms.get(idx).substring(1, searchTerms.get(idx).length()-1);
					String [] multivaluedSearchTermValues = arrayData.split(";");
					List<String> listOfMultvalSearchTerms = Arrays.asList(multivaluedSearchTermValues);
					listOfMultvalSearchTerms.stream().map(String::trim);
					multivaluedSearchTermsMap.put(idx, listOfMultvalSearchTerms);
				});

		return multivaluedSearchTermsMap;
	}
	
	
	public static boolean isArrayOfStrings(String data) {
		return (data.substring(0, 1).equals("[") && data.substring(data.length()-1, data.length()).equals("]"));
	}
	
	
	public static boolean isNotArrayOfStrings(String data) {
		return (!data.substring(0, 1).equals("[") && !data.substring(data.length()-1, data.length()).equals("]"));
	}
	
}
