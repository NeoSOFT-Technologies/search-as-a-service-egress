package com.searchservice.app.domian.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.searchservice.app.domain.utils.SearchUtil;
import com.searchservice.app.rest.errors.CustomException;
import com.searchservice.app.rest.errors.HttpStatusCode;


class SearchUtilTest {

	private static final String ALPHA_NUMERIC_STRING = "IAmIronMan";
	private static final String NOT_JUST_ALPHA_NUMERIC_STRING = "I_Am_Iron_Man";
	

	@Test
	void testCheckIfNameIsAlphaNumeric() {
		boolean isAlphaNumeric = SearchUtil.checkIfNameIsAlphaNumeric(ALPHA_NUMERIC_STRING);
		assertTrue(isAlphaNumeric);
		isAlphaNumeric = SearchUtil.checkIfNameIsAlphaNumeric(NOT_JUST_ALPHA_NUMERIC_STRING);
		assertFalse(isAlphaNumeric);
	}

	
	@Test
	void testIsAlphaNumeric() {
		boolean isAlphaNumeric = SearchUtil.isAlphaNumeric(ALPHA_NUMERIC_STRING);
		assertTrue(isAlphaNumeric);
		isAlphaNumeric = SearchUtil.isAlphaNumeric(NOT_JUST_ALPHA_NUMERIC_STRING);
		assertFalse(isAlphaNumeric);
	}
	
	@Test
	void testIsAlphaNumericEmptyName() {
		try {
		 SearchUtil.checkIfNameIsAlphaNumeric("");
		}catch(CustomException e) {
			assertEquals(HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(), e.getExceptionCode());
		}
	}
	
	@Test
	void testValidateInvalidStartRecord() {
		try {
			 SearchUtil.validateInputs("A","5", "desc");
			}catch(CustomException e) {
				assertEquals(HttpStatusCode.OPERATION_NOT_ALLOWED.getCode(), e.getExceptionCode());
		}
	}
	
	@Test
	void testValidateInvalidPageSize() {
		try {
			 SearchUtil.validateInputs("0","Five", "desc");
			}catch(CustomException e) {
				assertEquals(HttpStatusCode.OPERATION_NOT_ALLOWED.getCode(), e.getExceptionCode());
		}
	}
	

	@Test
	void testValidateInvalidOrder() {
		try {
			 SearchUtil.validateInputs("0","5", "1");
			}catch(CustomException e) {
				assertEquals(HttpStatusCode.OPERATION_NOT_ALLOWED.getCode(), e.getExceptionCode());
		}
	}

	
	@Test
	void testGetTrimmedListOfStrings() {
		List<String> untrimmedListOfStrings = new ArrayList<>(Arrays.asList("  IronMan ", "Batman    ", "      Spidy"));
		List<String> trimmedListOfStrings = new ArrayList<>(Arrays.asList("IronMan", "Batman", "Spidy"));
		List<String> processedListOfStrings = SearchUtil.getTrimmedListOfStrings(untrimmedListOfStrings);
		for(int i=0; i<trimmedListOfStrings.size(); i++) {
			assertEquals(trimmedListOfStrings.get(i), processedListOfStrings.get(i));
		}
	}

}