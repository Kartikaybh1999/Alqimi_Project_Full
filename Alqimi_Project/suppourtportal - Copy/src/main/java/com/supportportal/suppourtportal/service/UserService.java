package com.supportportal.suppourtportal.service;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import com.supportportal.suppourtportal.domain.*;
import org.springframework.web.multipart.MultipartFile;

import com.supportportal.suppourtportal.domain.User;
import com.supportportal.suppourtportal.exception.domain.EmailExistException;
import com.supportportal.suppourtportal.exception.domain.EmailNotFoundException;
import com.supportportal.suppourtportal.exception.domain.NotAnImageFileException;
import com.supportportal.suppourtportal.exception.domain.UserNotFoundException;
import com.supportportal.suppourtportal.exception.domain.UsernameExistException;



public interface UserService {

	User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException;

    List<User> getUsers();

    User findUserByUsername(String username);

    User findUserByEmail(String email);

   }
