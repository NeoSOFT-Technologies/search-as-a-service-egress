package com.searchservice.app.domain.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.searchservice.app.rest.errors.NullPointerOccurredException;
import com.searchservice.app.rest.errors.OperationNotAllowedException;

import lombok.Data;

@Data
public class SearchUtil {
	
	private SearchUtil() {}
	
	
	// VALIDATIONS
	public static boolean checkIfNameIsAlphaNumeric(String name) {
		if (null == name || name.isBlank() || name.isEmpty())
			throw new NullPointerOccurredException(404, "Field can't be empty. Provide some value");
		Pattern pattern = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]*$");
		Matcher matcher = pattern.matcher(name);
		return matcher.find();
	}
	
	
	public static boolean validateSearchQueryInputs(
			JSONArray currentTableSchema, 
			String queryField, 
			String searchTerm) {

		boolean isSearchQueryValidated = false;	
		boolean isQueryFieldMultivalued = false;
		
		if(currentTableSchema.isEmpty()) {
			/** Microservice is down
			 * Multivalue fields validation can't be performed
			 */
			// If MultivalueFields could not be validated; microservice is down, then DO STANDARDIZED VALIDATION
			// If Standardized validation unsuccessful, throw exception
			if(!isSearchQueryValidated)
				throw new OperationNotAllowedException(
						406, 
						"Microservice is down, multivalue fields couldn't be validated. "
						+ "Please provide single-valued search terms only");
			
		} else {
			// Check if Multivalue queryField is present
			isQueryFieldMultivalued = SearchUtil.isQueryFieldMultivalued(queryField, currentTableSchema);
			// Validate queryFields & searchTerms: multi-value --> comma-separated searchTerms
			List<String> multivaluedSearchTerms = SearchUtil.getTrimmedListOfStrings(Arrays.asList(searchTerm.split(",")));
			if(isQueryFieldMultivalued) {
				// Validate for Multivalue field having comma-separated search-terms
				if (multivaluedSearchTerms.isEmpty())
					throw new OperationNotAllowedException(
							406, 
							"Please provide 'comma-separated values' for this multivalued query-field");						
			} else {
				// Handle single-valued search-term
				// If validation unsuccessful, throw exception
				if(!isSearchQueryValidated)
					throw new OperationNotAllowedException(
							406, 
							"There're no Multivalue queryFields here. "
							+ "Please provide single-valued search terms only");
			}
		}
		
		return isSearchQueryValidated;
	}
	
	
	public static boolean isQueryFieldMultivalued(String queryField, JSONArray currentTableSchema) {
		boolean isMultivalued = false;
		for(Object col: currentTableSchema) {
			JSONObject colObj = (JSONObject)col;
			isMultivalued = colObj.get("name").equals(queryField) && colObj.get("multiValue").equals(true);
			if(isMultivalued)
				break;
		}

		return isMultivalued;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	///////////////////////////////////////////////
	
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
					List<String> listOfMultvalSearchTerms = Arrays.asList(arrayData.split(";"));
					listOfMultvalSearchTerms.forEach(String::trim);
					multivaluedSearchTermsMap.put(idx, listOfMultvalSearchTerms);
				});

		return multivaluedSearchTermsMap;
	}
	
	
	public static boolean isArrayOfStrings(String data) {
		
		boolean isValidSeparator
				= isAlphaNumeric(data)
				|| Arrays.asList(data.split("")).contains(";")
				|| Arrays.asList(data.split("")).contains("\"");	// For string data type
		if(!isValidSeparator)
			throw new OperationNotAllowedException(406, "Only ';' separator is allowed for multivalued search term array. Please provide valid separator");
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
	
	
	public static boolean isAlphaNumeric(String charSequence) {
	    String pattern= "^[a-zA-Z0-9]*$";
	    return charSequence.matches(pattern);
	}


	public static void setQueryForMultivaluedField(
			String queryField, List<String> searchTerms, StringBuilder queryString) {
		int counter = 0;
		for (String val : searchTerms) {
			if (counter == 0)
				queryString.append(queryField + ":" + val);
			else
				queryString.append(" AND " + queryField + ":" + val);
			counter++;
		}
	}
}
