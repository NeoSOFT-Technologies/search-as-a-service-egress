package com.searchservice.app.domain.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.searchservice.app.rest.errors.OperationNotAllowedException;

import ch.qos.logback.core.net.SyslogOutputStream;
import lombok.Data;

@Data
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
			// Validate queryFields & searchTerms: multi-value --> array of searchTerms
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
					List<String> listOfMultvalSearchTerms = Arrays.asList(arrayData.split(","));
					listOfMultvalSearchTerms.forEach(String::trim);
					multivaluedSearchTermsMap.put(idx, listOfMultvalSearchTerms);
				});

		return multivaluedSearchTermsMap;
	}
	
	
	public static boolean isArrayOfStrings(String data) {
		
		boolean isValidSeparator
				= isAlphaNumeric(data)
				|| Arrays.asList(data.split("")).contains(",")
				|| Arrays.asList(data.split("")).contains("\"");	// For string data type
		if(!isValidSeparator)
			throw new OperationNotAllowedException(406, "Only ',' separator is allowed for multivalued search term array. Please provide valid separator");
		return (data.substring(0, 1).equals("[")
				&& data.substring(data.length()-1, data.length()).equals("]")
				&& isValidSeparator);
	}
	
	
	public static boolean isNotArrayOfStrings(String data) {
		return (!data.substring(0, 1).equals("[") && !data.substring(data.length()-1, data.length()).equals("]"));
	}
	
	
	public static void setQueryForFirstQueryField(
			int i, String currentQueryField, List<String> searchTermList, Map<Integer, String> multivalueQueryFieldsMap, Map<Integer, List<String>> searchTermArrayValuesMap, 
			StringBuilder queryString) {
		queryString.append("(");
		if(!multivalueQueryFieldsMap.containsKey(i))
			queryString.append(currentQueryField+":"+searchTermList.get(i)+")");
		else {
			List<String> currentSearchTermArrayValues = searchTermArrayValuesMap.get(i); 
			int counter = 0;
			for (String val : currentSearchTermArrayValues) {
				if (counter == 0)
					queryString.append(currentQueryField + ":" + val);
				else
					queryString.append(" OR " + currentQueryField + ":" + val);
				counter++;
			}
			
			queryString.append(")");
		}
	}
	
	
	public static void setQueryForOtherThanFirstQueryField(
			int i, String currentQueryField, List<String> searchTermList, Map<Integer, String> multivalueQueryFieldsMap, Map<Integer, List<String>> searchTermArrayValuesMap, 
			StringBuilder queryString, String searchOperator) {
		if(!multivalueQueryFieldsMap.containsKey(i))
			queryString.append(" "+searchOperator+" ("+currentQueryField+":"+searchTermList.get(i)+")");
		else {
			// It's multivalue queryField
			queryString.append(" "+searchOperator+" (");

			List<String> currentSearchTermArrayValues = searchTermArrayValuesMap.get(i); 
			int counter = 0;
			for (String val : currentSearchTermArrayValues) {
				if (counter == 0)
					queryString.append(currentQueryField + ":" + val);
				else
					queryString.append(" OR " + currentQueryField + ":" + val);
				counter++;
			}
			
			queryString.append(")");
		}
	}
	
	
	public static List<String> getTrimmedListOfStrings(List<String> list) {
		List<String> trimmedList = new ArrayList<>();
		for(String str: list) {
			str = str.trim();
			trimmedList.add(str);
		}
		
		return trimmedList;
	}
	
	public static String getTrimmedListOfStrings(String queryField) {
		
		return queryField;
	}
	
	public static boolean isAlphaNumeric(String charSequence) {
	    String pattern= "^[a-zA-Z0-9]*$";
	    return charSequence.matches(pattern);
	}
	
}
