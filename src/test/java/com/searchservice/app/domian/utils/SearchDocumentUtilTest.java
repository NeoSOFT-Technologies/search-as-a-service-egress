package com.searchservice.app.domian.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.common.SolrDocument;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.searchservice.app.domain.utils.SearchDocumentUtil;

 class SearchDocumentUtilTest {

	Object obj = new Object();
	SearchDocumentUtil s1 = new SearchDocumentUtil();
	Map<String, Object> _fields;
	Map<String, Object> _fields1 = new HashMap<>();
	SearchDocumentUtil s2 = new SearchDocumentUtil(intializeFields());

	@SuppressWarnings("deprecation")
	@BeforeEach
	void setUp() throws Exception {
		s1.toString();
		s1.setField("pq", _fields);
		s1.addField("sd", _fields);
		s1.getFieldValue(null);
		s1.getFieldNames();
		s1.getFieldValueMap();
		s1._size();
		s2.toString();
		s2.addField("js", _fields1);
		s2.setField("", _fields1);
	}

	@Test
	void testSearchDocumentUtilmremoveFields() {
		s1.setField("name","neo");
		s1.getFieldValues("Neo");
		s1.getFieldValues("neo");
		assertEquals("neo",s1.getFirstValue("name"));
		s1.hasChildDocuments();
		s1.clear();
		s1.put("pq", _fields);
	}

	@Test
	void testSearchDocumentUtilmremoveFieldsdoc() {
		SolrDocument doc = null;
		s1.size();
		s1.values();
		s1.getFieldValuesMap();
		assertTrue(s1.containsValue(doc));
		s1.entrySet();
		s1.getChildDocuments();
	}

	@Test
	void testSearchDocumentUtilclass() {
		SolrDocument doc = null;
		s1.addChildDocument(doc);
		s1.getChildDocumentCount();
		s1.removeFields("pq");
		s1.containsKey(doc);
		s1.get(doc);
		s1.iterator();
		s1.remove(doc);
		assertFalse(s1.isEmpty());
	}
	public Map<String,Object> intializeFields(){
		Map<String,Object> test1 = new HashMap<>();
		test1.put("name", "_nest_path");
		return test1;
	}
}
