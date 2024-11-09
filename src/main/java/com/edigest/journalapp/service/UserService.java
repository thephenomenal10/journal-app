package com.edigest.journalapp.service;

import com.edigest.journalapp.entity.User;
import com.edigest.journalapp.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
//@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public User saveNewUser(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(List.of("USER"));
            return userRepository.save(user);
        } catch (Exception e) {
            logger.error(e.toString());
            throw new RuntimeException(e);
        }
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User updateUser(User user) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User createdUser = userRepository.findByUsername(authentication.getName());

            createdUser.setUsername(user.getUsername());
            createdUser.setPassword(user.getPassword());
            return this.saveNewUser(createdUser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<User> getUserById(ObjectId id) {
        return userRepository.findById(id);
    }

    public void deleteUserById(ObjectId id) throws BadRequestException {
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Exception", e);
            throw new BadRequestException(e.getMessage());
        }
    }

    public void deleteUserByName() throws BadRequestException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            userRepository.deleteByUsername(authentication.getName());
        } catch (Exception e) {
            logger.error("Exception", e);
            throw new BadRequestException(e.getMessage());
        }
    }

    public User findByUserName(String userName) {
        try {
            User user = userRepository.findByUsername(userName);
            if (user != null) {
                return user;
            }
            throw new InstanceNotFoundException("User not found");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
