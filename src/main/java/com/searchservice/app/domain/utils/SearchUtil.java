package com.searchservice.app.domain.utils;

import java.util.ArrayList;
import java.util.List;
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
	
	
	public static boolean isAlphaNumeric(String charSequence) {
	    String pattern= "^[a-zA-Z0-9]*$";
	    return charSequence.matches(pattern);
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
	
	
	public static boolean validateInputs(String startRecord, String pageSize, String order) {
		boolean testResult = true;
		String startRecordRegex = "^[0-9]{0,5}+$";
		//String pageSizeRegex = "^[0-9]{5}+$";
		String orderRegex = "^(ASC|DESC)$";
		if(!validateUsingRegex(startRecordRegex, startRecord.trim().toUpperCase()))
			throw new OperationNotAllowedException(406, "Start Record must be of type Integer, Range : 0-99999");
		if(!validateUsingRegex(startRecordRegex, pageSize.trim().toUpperCase()))
			throw new OperationNotAllowedException(406, "Page Size must be of type Integer, Range : 0-99999");
		if(!validateUsingRegex(orderRegex, order.trim().toUpperCase()))
			throw new OperationNotAllowedException(406, "Order value must be : 'asc' OR 'desc");
		return testResult;
	}
	
	
	public static boolean validateUsingRegex(String regex, String value) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(value);
		return matcher.find();
	}
	
	
	// Utitlity functions
	public static List<String> getTrimmedListOfStrings(List<String> list) {
		List<String> trimmedList = new ArrayList<>();
		for(String str: list) {
			str = str.trim();
			trimmedList.add(str);
		}
		
		return trimmedList;
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
