package com.searchservice.app.domian.utils;

import static org.junit.jupiter.api.Assertions.*;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.searchservice.app.domain.utils.SearchUtil;


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
	void testGetTrimmedListOfStrings() {
		List<String> untrimmedListOfStrings = new ArrayList<>(Arrays.asList("  IronMan ", "Batman    ", "      Spidy"));
		List<String> trimmedListOfStrings = new ArrayList<>(Arrays.asList("IronMan", "Batman", "Spidy"));
		List<String> processedListOfStrings = SearchUtil.getTrimmedListOfStrings(untrimmedListOfStrings);
		for(int i=0; i<trimmedListOfStrings.size(); i++) {
			assertEquals(trimmedListOfStrings.get(i), processedListOfStrings.get(i));
		}
	}

}