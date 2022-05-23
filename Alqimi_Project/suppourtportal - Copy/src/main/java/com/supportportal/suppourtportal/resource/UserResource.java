package com.supportportal.suppourtportal.resource;

import static org.springframework.http.HttpStatus.OK;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supportportal.suppourtportal.constant.SecurityConstant;
import com.supportportal.suppourtportal.domain.User;
import com.supportportal.suppourtportal.domain.UserPrincipal;
import com.supportportal.suppourtportal.exception.ExceptionHandling;
import com.supportportal.suppourtportal.exception.domain.EmailExistException;
import com.supportportal.suppourtportal.exception.domain.EmailNotFoundException;
import com.supportportal.suppourtportal.exception.domain.UserNotFoundException;
import com.supportportal.suppourtportal.exception.domain.UsernameExistException;
import com.supportportal.suppourtportal.service.UserService;
import com.supportportal.suppourtportal.utility.JWTTokenProvider;

@RestController
@RequestMapping(path = { "/", "/user"})
public class UserResource extends ExceptionHandling {
	private UserService userService;
	private AuthenticationManager authenticationManager;
	private JWTTokenProvider jwtTokenProvider;
	
	@Autowired
	public UserResource( AuthenticationManager authenticationManager,UserService userService,JWTTokenProvider jwtTokenProvider) {
		this.userService= userService;
		this.authenticationManager =authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
	}
	
	@PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
		authenticate(user.getUsername(),user.getPassword());
		User loginUser = userService.findUserByUsername(user.getUsername());
		UserPrincipal userPrincipal = new UserPrincipal(loginUser);
		HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
		return new ResponseEntity<>(loginUser, jwtHeader, OK);
		
	}
	
	
	private HttpHeaders getJwtHeader(UserPrincipal user) {
		 HttpHeaders headers = new HttpHeaders();
	        headers.add(SecurityConstant.JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(user));
	        return headers;
	}

	private void authenticate(String username, String password) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		  
		
	}

	@PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException{
        User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return new ResponseEntity<>(newUser, OK);
    }}
