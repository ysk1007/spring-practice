package com.example.jpaboard.util;

import java.security.MessageDigest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SHA256Util {
	
	// 문자열을 입력하면 SHA256 암호화된 16진수 문자열을 반환
	public static String encoding(String str) {
		String dest = "";
		try {
			
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(str.getBytes());	// 문자열의 바이트를 암호화
			
			// 암호화된 바이트를 16진수로 문자로 변경
			StringBuffer sb = new StringBuffer();
			for(byte b : md.digest()) {
				sb.append(String.format("%02x", b));	// b를 2자리 16진수로 만들어라. 만약 1자리라면 앞에 0 붙임
			}
			dest = sb.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("PW 암호화 실패");
		}
		
		return dest;
	}
}
