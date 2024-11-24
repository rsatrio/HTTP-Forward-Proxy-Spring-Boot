package com.rizky.proxy.services.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.rizky.proxy.services.ProxyServices;

import jakarta.servlet.http.HttpServletRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;


@Service
public class ProxyServicesImpl implements ProxyServices {
	
	final Logger log1=LoggerFactory.getLogger(this.getClass());

	@Override
	public ResponseEntity doProxy(HttpServletRequest request) {
		
		try	{
			
			List<String> listMethod=new LinkedList();
			listMethod.add("POST");
			listMethod.add("GET");
			listMethod.add("PATCH");
			listMethod.add("PUT");
			
			if(!listMethod.contains(request.getMethod().toUpperCase()))	{
				//Method not supported
				return ResponseEntity.status(405).build();
			}
			
			
			//Collect all http headers
			Map<String, String> headers=new HashMap<>();

			Enumeration<String> headerNames = request.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String headerName = headerNames.nextElement();  

				String headerValue = request.getHeader(headerName);
				headers.put(headerName, headerValue);
			}

			//Remove header and add new header
			headers.remove("host");
			headers.put("via", "1.1 rizky-server");
			
			
			//Collect all query params
			Enumeration<String> parameterNames = request.getParameterNames();
			Map<String, Object> querys=new HashMap<>();
			
			while (parameterNames.hasMoreElements())  
			{
				String parameterName = parameterNames.nextElement();
				String parameterValue = request.getParameter(parameterName);
				querys.put(parameterName, parameterValue);
			}
			HttpResponse<byte[]> result=null;
			
			if(request.getMethod().toUpperCase().equals("GET"))	{
				result=Unirest.get(request.getRequestURL().toString())
						.queryString(querys)
						.headers(headers)
						.asBytes();
			}
			else if(request.getMethod().toUpperCase().equals("POST"))	{
				result=Unirest.post(request.getRequestURL().toString())
						.queryString(querys)
						.headers(headers)
						.body(getBody(request))
						.asBytes();
			}
			else if(request.getMethod().toUpperCase().equals("PUT"))	{
				result=Unirest.put(request.getRequestURL().toString())
						.queryString(querys)
						.headers(headers)
						.body(getBody(request))
						.asBytes();
			}
			else if(request.getMethod().toUpperCase().equals("PATCH"))	{
				result=Unirest.patch(request.getRequestURL().toString())
						.queryString(querys)
						.headers(headers)
						.body(getBody(request))
						.asBytes();
			}
			
			if(result==null)	{
				return ResponseEntity.status(404).build();
			}

			HttpHeaders headersResult = new HttpHeaders();
			result.getHeaders().all().forEach(a->{
				headersResult.add(a.getName(), a.getValue());
			});


			String contentType=result.getHeaders().getFirst("content-type");
			
			log1.debug("Response content-type for url {}:{}",request.getRequestURI(),contentType);
			
			
			if(contentType.contains("image"))	{
				return ResponseEntity.status(result.getStatus())
						.headers(headersResult)
						.body(result.getBody());
			}
			else 	{
				String resultString=new String( result.getBody()).replaceAll("https://", "http://");
				return ResponseEntity.status(result.getStatus())
						.headers(headersResult)
						.body(resultString);
			}
			
			
		}
		catch(Exception e)	{
			log1.error("Error processing {} request {}",request.getMethod(),
					request.getRequestURL().toString(),e);
			return ResponseEntity.internalServerError().build();
		}

	}
	
	private static String getBody(HttpServletRequest request) throws IOException	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));  

		StringBuilder stringBuilder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);  

		}
		reader.close();

		String  body = stringBuilder.toString();
		return body;
	}


}
