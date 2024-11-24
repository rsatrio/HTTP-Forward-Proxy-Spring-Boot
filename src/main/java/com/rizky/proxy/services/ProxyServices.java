package com.rizky.proxy.services;

import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

public interface ProxyServices {
	
	public ResponseEntity doProxy(HttpServletRequest request);

}
