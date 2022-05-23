package com.supportportal.suppourtportal.service.impl;



import static com.supportportal.suppourtportal.constant.FileConstant.DEFAULT_USER_IMAGE_PATH;
import static com.supportportal.suppourtportal.constant.FileConstant.DIRECTORY_CREATED;
import static com.supportportal.suppourtportal.constant.FileConstant.DOT;
import static com.supportportal.suppourtportal.constant.FileConstant.FILE_SAVED_IN_FILE_SYSTEM;
import static com.supportportal.suppourtportal.constant.FileConstant.FORWARD_SLASH;
import static com.supportportal.suppourtportal.constant.FileConstant.JPG_EXTENSION;
import static com.supportportal.suppourtportal.constant.FileConstant.NOT_AN_IMAGE_FILE;
import static com.supportportal.suppourtportal.constant.FileConstant.USER_FOLDER;
import static com.supportportal.suppourtportal.constant.FileConstant.USER_IMAGE_PATH;
import static com.supportportal.suppourtportal.constant.UserImplConstant.EMAIL_ALREADY_EXISTS;
import static com.supportportal.suppourtportal.constant.UserImplConstant.NO_USER_FOUND_BY_EMAIL;
import static com.supportportal.suppourtportal.constant.UserImplConstant.NO_USER_FOUND_BY_USERNAME;
import static com.supportportal.suppourtportal.constant.UserImplConstant.USERNAME_ALREADY_EXISTS;
import static com.supportportal.suppourtportal.enumeration.Role.ROLE_USER;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.MediaType.IMAGE_GIF_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.supportportal.suppourtportal.enumeration.Role;
import com.supportportal.suppourtportal.service.EmailService;
import com.supportportal.suppourtportal.service.LoginAttemptService;
import com.supportportal.suppourtportal.domain.User;
import com.supportportal.suppourtportal.domain.UserPrincipal;
import com.supportportal.suppourtportal.exception.domain.EmailExistException;
import com.supportportal.suppourtportal.exception.domain.EmailNotFoundException;
import com.supportportal.suppourtportal.exception.domain.NotAnImageFileException;
import com.supportportal.suppourtportal.exception.domain.UserNotFoundException;
import com.supportportal.suppourtportal.exception.domain.UsernameExistException;
import com.supportportal.suppourtportal.repository.UserRepository;

import com.supportportal.suppourtportal.service.UserService;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private EmailService emailService;

   

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, LoginAttemptService loginAttemptService, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        }
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findUserByUsername(username);
		 if (user == null) {
	            LOGGER.error("User not found by username" + username);
	            throw new UsernameNotFoundException("User not found by username"+ username);
		 }
		 else {
			 user.setLastLoginDate(user.getLastLoginDate());
			 user.setLastLoginDate(new Date());
			 userRepository.save(user);
	            UserPrincipal userPrincipal = new UserPrincipal(user);
	            LOGGER.info("FOUND_USER_BY_USERNAME" + username);
	            return userPrincipal;			 
		 }
		
		
	}

	@Override
	public User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
        validateNewUsernameAndEmail(EMPTY, username, email);
        User user = new User();
        user.setUserId(generateUserId());
        String password = generatePassword();
        String encodedPassword = encodedPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        userRepository.save(user);
        LOGGER.info("New user password: " + password);
        emailService.sendNewPasswordEmail(firstName, password, email);
		return user;
	}

	private String getTemporaryProfileImageUrl(String username) {
		  return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
		    
	}

	private String encodedPassword(String password) {
		return passwordEncoder.encode(password);
	}

	private String generatePassword() {
		 return RandomStringUtils.randomAlphanumeric(10);
	}

	private String generateUserId() {
		return RandomStringUtils.randomNumeric(10);
	}

	private User validateNewUsernameAndEmail(String currentUsername, String newusername, String newemail) throws UserNotFoundException, UsernameExistException, EmailExistException {
		 User userByNewUsername = findUserByUsername(newusername);
	        User userByNewEmail = findUserByEmail(newemail);
	        if(StringUtils.isNotBlank(currentUsername)) {
	            User currentUser = findUserByUsername(currentUsername);
	            if(currentUser == null) {
	                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
	            }
	            if(userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
	                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
	            }
	            if(userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
	                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
	            }
	            return currentUser;
	        } else {
	            if(userByNewUsername != null) {
	                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
	            }
	            if(userByNewEmail != null) {
	                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
	            }
	            return null;
	        }
        }
    
		

	@Override
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	@Override
	public User findUserByUsername(String username) {
		 return userRepository.findUserByUsername(username);
	}

	@Override
	public User findUserByEmail(String email) {
		 return userRepository.findUserByEmail(email);
	}

	 		}
