package com.searchservice.app.config.aspect;

import java.net.InetAddress;
import org.springframework.http.ResponseEntity;
import com.searchservice.app.domain.dto.Response;

import java.net.UnknownHostException;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jboss.logging.MDC;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import com.searchservice.app.domain.dto.user.UserDTO;

@Aspect
@Configuration
public class AspectConfig {
      private static Logger log=LoggerFactory.getLogger(AspectConfig.class);
      
      private static final String STARTED_EXECUTION ="Started Request of Service Name : {},UserName : {}, CorrelationId : {}, IpAddress : {}, MethodName : {}, TimeStamp : {}, Parameters :{}";
      private static final String SUCCESSFUL_EXECUTION="Successfully Requested of Service Name : {},UserName : {}, CorrelationId : {}, IpAddress : {}, MethodName : {}, TimeStamp : {}, Parameters :{}";
      private static final String CORRELATION_ID_LOG_VAR_NAME="CID";
      private static UserDTO user;
      private static String ip;
     
      //Added Condition for Null User
      @Before(value="execution(* com.searchservice.app.rest.UserResource.*(..))")
      public static Object logStatementForRest(JoinPoint joinPoint) {
    	user =(UserDTO) joinPoint.getArgs()[0];
    	try {
    		ip=InetAddress.getLocalHost().getHostAddress();
    	}
    	catch(UnknownHostException e)
    	{
    		log.error(e.toString());
    	}
    	log.info(STARTED_EXECUTION,joinPoint.getTarget().getClass().getSimpleName(),user!=null?user.getUsername():"",
    			MDC.get(CORRELATION_ID_LOG_VAR_NAME),ip,joinPoint.getSignature().getName(),utcTime(),joinPoint.getArgs());
		return joinPoint;
      }
      
      
      @Before(value="execution(* com.searchservice.app.rest.SearchResource.*(..))")
      public void logStatementForSearchResource(JoinPoint joinPoint){
    	  log.info(STARTED_EXECUTION,joinPoint.getTarget().getClass().getSimpleName(), user!=null?user.getUsername():"",
      			MDC.get(CORRELATION_ID_LOG_VAR_NAME),ip,joinPoint.getSignature().getName(),utcTime(),joinPoint.getArgs());
      }
      
      
      @Before(value="execution(* com.searchservice.app.domain.service.*.*(..))")
      public void logStatementforService(JoinPoint joinPoint){
    	  log.info(STARTED_EXECUTION,joinPoint.getTarget().getClass().getSimpleName(),user!=null?user.getUsername():"",
        			MDC.get(CORRELATION_ID_LOG_VAR_NAME),ip,joinPoint.getSignature().getName(),utcTime(),joinPoint.getArgs());
      }
      
      
      @After(value="execution(* com.searchservice.app.rest.*.*(..))")
      public void logStatementForAfterRest(JoinPoint joinPoint){
    	  log.info(SUCCESSFUL_EXECUTION,joinPoint.getTarget().getClass().getSimpleName(),user!=null?user.getUsername():"",
      			MDC.get(CORRELATION_ID_LOG_VAR_NAME),ip,joinPoint.getSignature().getName(),utcTime(),joinPoint.getArgs());
      }
      
      
      @AfterThrowing(value = "execution(* com.searchservice.app.rest.*.*(..))")
  	  public void logStatementAfterThrowing(JoinPoint joinPoint) {
  		log.error(
  				"Failed Response of Service Name : {}, Username : {}, CorrlationId : {}, IpAddress : {}, MethodName : {}, Parameters : {}",
  				joinPoint.getTarget().getClass().getSimpleName(), user!=null?user.getUsername():"",
  				MDC.get(CORRELATION_ID_LOG_VAR_NAME), ip, joinPoint.getSignature().getName(), joinPoint.getArgs());
  	  }
      
      @AfterReturning(value = "execution(* com.searchservice.app.rest.*.*(..))", returning = "response")
  	  public void logStatementAfterRest(JoinPoint joinPoint, ResponseEntity<Response> response) {
  		log.info("Complete Execution of {}  with response {} ", joinPoint.getSignature().getName(), response);

  	  }
      
      
      @After(value="execution(* com.searchservice.app.domain.service.*.*(..))")
      public void logStatementforAfterService(JoinPoint joinPoint){
    	  log.info(SUCCESSFUL_EXECUTION,joinPoint.getTarget().getClass().getSimpleName(),user!=null?user.getUsername():"",
      			MDC.get(CORRELATION_ID_LOG_VAR_NAME),ip,joinPoint.getSignature().getName(),utcTime(),joinPoint.getArgs());
      }
      
      
      @AfterThrowing(value = "execution(* com.searchservice.app.domain.service.*.*(..))", throwing = "e")
  	  public void logStatementForServiceAfterThrowing(JoinPoint joinPoint, Exception e) {
  		log.error(
  				"Failed Response of Service Name : {}, Username : {}, CorrlationId : {}, IpAddress : {}, MethodName : {}, Parameters : {}",
  				joinPoint.getTarget().getClass().getSimpleName(), user!=null?user.getUsername():"",
  				MDC.get(CORRELATION_ID_LOG_VAR_NAME), ip, joinPoint.getSignature().getName(), joinPoint.getArgs());
  	  }
      
   
      
      public static DateTime utcTime() {
  			DateTime now = new DateTime(); // Gives the default time zone.
  			return now.toDateTime(DateTimeZone.UTC);
  	}
}
