package com.searchservice.app.config.aspect;


import java.io.IOException;
import java.util.UUID;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.MDC;
import org.springframework.stereotype.Component;

@Component
public class CidFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		if(req instanceof HttpServletRequest)
		{
			HttpServletRequest request=(HttpServletRequest) req;
			String requestCid=request.getHeader("CID");
			if(requestCid==null) {
				requestCid=generationUniqueCorrelationId();
			}
			MDC.put("CID", requestCid);
		}
		
		try {
			chain.doFilter(req, res);
		}finally {
			MDC.remove("CID");
		}
	}

	
	
	
	public static String generationUniqueCorrelationId() {
		return UUID.randomUUID().toString();
	}



}
