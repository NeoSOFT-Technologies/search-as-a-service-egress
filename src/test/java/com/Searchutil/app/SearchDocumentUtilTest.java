package com.Searchutil.app;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.searchservice.app.domain.utils.SearchDocumentUtil;

public class SearchDocumentUtilTest {

	SearchDocumentUtil s1=new SearchDocumentUtil();
	Map<String, Object> _fields;
	Map<String, Object> _fields1=new HashMap<>();
	SearchDocumentUtil s2=new SearchDocumentUtil(_fields1);
	@Test 
	void testSearchDocumentUtil()
	{
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
}
