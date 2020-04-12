package com.demo.blog.service;

import com.demo.blog.dao.UserMapper;
import com.demo.blog.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    BCryptPasswordEncoder mockEncoder;
    @Mock
    UserMapper userkMapper;
    @InjectMocks
    UserService userService;

    @Test
    public void testSave() {
       when(mockEncoder.encode("password")).thenReturn("myPassword");
        userService.save("aaa", "password");
        verify(userkMapper).save("aaa", "myPassword");
    }


    @Test
    public void testGetUserByUsername() {
        userService.getUserByUsername("aaa");
        verify(userkMapper).findUserByUsername("aaa");
    }

    @Test
    public void throwExceptionWhenUserNotFound() {
        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("aaa"));
    }

    @Test
    public void returnUserDetailsWhenUserFound() {
        when(userkMapper.findUserByUsername("aaa"))
                .thenReturn(new User(11, "aaa", "myPassword"));
        UserDetails userDetails = userService.loadUserByUsername("aaa");
        Assertions.assertEquals("aaa", userDetails.getUsername());
        Assertions.assertEquals("myPassword", userDetails.getPassword());
    }
}