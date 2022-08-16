 package com.supportportal.suppourtportal.constant;

public class SecurityConstant {
	public static final long EXPIRATION_TIME = 432_000_000;
	public static final String TOKEN_PREFIX =  "Bearer ";
	public static final String JWT_TOKEN_HEADER	= "Jwt-Token";
	public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
	public static final String GET_ALQIMI_LLC= "Alqimi,LLC";
	public static final String GET_ALQIMI_ADMINISTRATION="USER MANAGEMENT PORTAL";
	public static final String AUTHORITIES = "Authorities";
	public static final String FORBIDDEN_MESSAGE ="You need to login to access";
	public static final String ACESS_DENIED_MESSAGE= "You do not have permission to access this page";
	public static final String OPTIONS_HTTP_METHOD= "Options";
	public static final String[] PUBLIC_URLS = { "/user/login","/user/login2", "/user/register", "/user/image/**", };
	    


}
