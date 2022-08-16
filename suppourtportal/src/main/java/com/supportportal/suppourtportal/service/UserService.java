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

    User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNotLocked, boolean isActive,boolean isLoggedIn, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException;

    User updateUser(String tokenJwt,String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNotLocked, boolean isActive,boolean isLoggedIn, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException;

    void deleteUser(String username) throws IOException;

    void resetPassword(String email) throws MessagingException, EmailNotFoundException;

    User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException;
    
    User updateStatus(String username,boolean isActive);
    
    User saveToken(String tokenJwt,String username);
    User deleteToken(String username );
    User saveOldToken(String tokenJwt,String username);
    String getToken(String username);
}
