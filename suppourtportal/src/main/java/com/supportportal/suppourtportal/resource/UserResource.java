package com.supportportal.suppourtportal.resource;

import static com.supportportal.suppourtportal.constant.FileConstant.*;
import static com.supportportal.suppourtportal.constant.SecurityConstant.*;
import static com.supportportal.suppourtportal.constant.FileConstant.TEMP_PROFILE_IMAGE_BASE_URL;
import static com.supportportal.suppourtportal.constant.FileConstant.USER_FOLDER;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.supportportal.suppourtportal.domain.HttpResponse;
import com.supportportal.suppourtportal.constant.SecurityConstant;
import com.supportportal.suppourtportal.domain.User;
import com.supportportal.suppourtportal.domain.UserPrincipal;
import com.supportportal.suppourtportal.exception.ExceptionHandling;
import com.supportportal.suppourtportal.exception.domain.EmailExistException;
import com.supportportal.suppourtportal.exception.domain.EmailNotFoundException;
import com.supportportal.suppourtportal.exception.domain.NotAnImageFileException;
import com.supportportal.suppourtportal.exception.domain.UserNotFoundException;
import com.supportportal.suppourtportal.exception.domain.UsernameExistException;
import com.supportportal.suppourtportal.service.UserService;

import com.supportportal.suppourtportal.utility.JWTTokenProvider;

@RestController
@RequestMapping(path = { "/", "/user"})
public class UserResource extends ExceptionHandling {
    public static final String EMAIL_SENT = "An email with a new password was sent to: ";
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";
    private AuthenticationManager authenticationManager;
    private UserService userService;
    private JWTTokenProvider jwtTokenProvider;
    

    @Autowired
    public UserResource(AuthenticationManager authenticationManager, UserService userService, JWTTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    //login request
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        authenticate(user.getUsername(), user.getPassword());
        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);  
        
        if(loginUser.getTokenJwt()==null  ) {
        	loginUser.isLoggedIn=true;
            HttpHeaders jwtHeader = getJwtHeader(userPrincipal, user);
            return new ResponseEntity<>( loginUser,jwtHeader, OK);}
        else if(loginUser.getTokenJwt()!=null && loginUser.isLoggedIn==false) {
        	loginUser.isLoggedIn=true; 
        	HttpHeaders jwtHeader = getJwtHeader(userPrincipal, user);
             return new ResponseEntity<>( loginUser,jwtHeader, OK);}   
        else {
        	   loginUser.isLoggedIn=false;
        	   HttpHeaders jwtHeader = getJwtHeader(userPrincipal, user);
        	   return new ResponseEntity<>( loginUser,null, CONFLICT);
        	   }
    }
    
    @PostMapping("/login2")
    public ResponseEntity<User> login2(@RequestBody User user) {
        authenticate(user.getUsername(), user.getPassword());
        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser); 
       
            HttpHeaders jwtHeader = getJwtHeader(userPrincipal, user);
            return new ResponseEntity<>( loginUser,jwtHeader, OK);
            }
           
    
    
//    
//    //saveoldtoken
//    @PostMapping("/saveoldtoken")
//    public ResponseEntity<User> saveoldtoken(@RequestBody User user) {
//       
//        User loginUser = userService.findUserByUsername(user.getUsername());
//        UserPrincipal userPrincipal = new UserPrincipal(loginUser); 
//       if(loginUser.getTokenJwt()==null) {
//        HttpHeaders jwtHeader = getJwtHeader2(userPrincipal, user);
//        return new ResponseEntity<>( loginUser,jwtHeader, OK);}
//       else {
//    	   HttpHeaders jwtHeader = null;
//    	   return new ResponseEntity<>( loginUser,jwtHeader, ALREADY_REPORTED);
//    	   }
//       }
    
    
    
    
    //regester request
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
        User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return new ResponseEntity<>(newUser, OK);
    }
   
    
    //add user request
    @PostMapping("/add")
    public ResponseEntity<User> addNewUser(@RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isLoggedIn") String isLoggedIn,
                                           @RequestParam("isNotLocked") String isNotLocked,
                                           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
        User newUser = userService.addNewUser(firstName, lastName, username,email, role, Boolean.parseBoolean(isNotLocked), Boolean.parseBoolean(isActive), Boolean.parseBoolean(isLoggedIn), profileImage);
        return new ResponseEntity<>(newUser, OK);
    }
    //update User request
    @PostMapping("/update")
    public ResponseEntity<User> update(@RequestParam("currentUsername") String currentUsername,
                                       @RequestParam("firstName") String firstName,
                                       @RequestParam("lastName") String lastName,
                                       @RequestParam("username") String username,
                                       @RequestParam("email") String email,
                                       @RequestParam("role") String role,
                                       @RequestParam("isActive") String isActive,
                                       @RequestParam("isLoggedIn") String isLoggedIn,
                                       @RequestParam("isNotLocked") String isNotLocked,
                                       @RequestParam("tokenJwt") String tokenJwt,
                                       @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
        User updatedUser = userService.updateUser(tokenJwt,currentUsername, firstName, lastName, username,email, role, Boolean.parseBoolean(isNotLocked), Boolean.parseBoolean(isActive),Boolean.parseBoolean(isLoggedIn), profileImage);
        return new ResponseEntity<>(updatedUser, OK);
    }
    
   
    
    // find username
    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username) {
        User user = userService.findUserByUsername(username);
        return new ResponseEntity<>(user, OK);
    }
    
    
    
    
    //access list of users
    @GetMapping("/list/{username}")
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader(value = "Authorization") String header1,@PathVariable("username") String username)  {
    	
    	String token = userService.getToken(username);
    	if(header1.equalsIgnoreCase(TOKEN_PREFIX+token)) {
    	List<User> users = userService.getUsers();
    	
        return new ResponseEntity<>(users, OK);
    	}
    	else { 
    		return new ResponseEntity<>(null, UNAUTHORIZED);    	}
    }
    
    
    
    
    //reseting password
    @GetMapping("/resetpassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws MessagingException, EmailNotFoundException {
        userService.resetPassword(email);
        return response(OK, EMAIL_SENT + email);
    }
//delete username
    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) throws IOException {
        userService.deleteUser(username);
        return response(OK, USER_DELETED_SUCCESSFULLY);
    }
    //deleteToken
    @DeleteMapping("/deleteToken/{username}")
    public ResponseEntity<HttpResponse> deleteUserToken(@PathVariable("username") String username) throws IOException {
    	User loginUser = userService.findUserByUsername(username);
    	loginUser.isLoggedIn=true;
    	userService.deleteToken(username);
       
        return response(OK, USER_DELETED_SUCCESSFULLY);
    }
    
//updateProfileImage
    @PostMapping("/updateProfileImage")
    public ResponseEntity<User> updateProfileImage(@RequestParam("username") String username, @RequestParam(value = "profileImage") MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
        User user = userService.updateProfileImage(username, profileImage);
        return new ResponseEntity<>(user, OK);
    }
    
    

    @GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
    }

    @GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()) {
            int bytesRead;
            byte[] chunk = new byte[1024];
            while((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }
//get jwt token
    private HttpHeaders getJwtHeader(UserPrincipal user,User users) {
        HttpHeaders headers = new HttpHeaders();
       
        String token = jwtTokenProvider.generateJwtToken(user);
        userService.saveToken(token,users.getUsername());
        headers.add(JWT_TOKEN_HEADER, token);
        return headers;
        
    }
   
    private HttpHeaders getJwtHeader2(UserPrincipal user,User users) {
        HttpHeaders headers = new HttpHeaders();
        String token = users.getTokenJwt();
        headers.add(JWT_TOKEN_HEADER, token);;
       
        return headers;
    }
    
//authenticate user
    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
