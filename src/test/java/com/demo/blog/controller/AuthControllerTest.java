package com.demo.blog.controller;

import com.demo.blog.entity.User;
import com.demo.blog.service.AuthService;
import com.demo.blog.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class AuthControllerTest {

    private MockMvc mvc;

    @Mock
    private UserService userService;
    @Mock
    private AuthenticationManager authenticationManager;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        AuthService authService = new AuthService(userService);
        mvc = MockMvcBuilders.standaloneSetup(new AuthController(userService, authenticationManager, authService)).build();
    }


    @Test
    void returnNotLoginByDefault() throws Exception {
        mvc.perform(get("/auth")).andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString().contains("用户没有登录")));
    }

    @Test
    void testLogin() throws Exception {
        mvc.perform(get("/auth")).andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString().contains("用户没有登录")));

        Map<String, String> map = new HashMap<>();
        map.put("username", "aaa");
        map.put("password", "password");
        when(userService.getUserByUsername("aaa"))
                .thenReturn(new User(1, "aaa", bCryptPasswordEncoder.encode("password")));

        MvcResult response = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.122 Safari/537.36")
                .content(new ObjectMapper().writeValueAsString(map)))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString().contains("登录成功")))
                .andReturn();

        HttpSession session = response.getRequest().getSession();

        // 通过/auth接口检查账号，是否处于登录状态
        mvc.perform(get("/auth").session((MockHttpSession) session)).andExpect(status().isOk())
                .andExpect(result -> {
                    System.out.println(result.getResponse().getContentAsString());
                    Assertions.assertTrue(result.getResponse().getContentAsString().contains("aaa"));
                });
    }

}