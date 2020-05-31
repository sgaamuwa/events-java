package com.events.events.unit.controller;

import com.events.events.EventsApplication;
import com.events.events.config.security.SecurityConfiguration;
import com.events.events.controllers.UserController;
import com.events.events.models.User;
import com.events.events.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
// Note to self: you have to provide the context configuration to know which security configuration to use(uses
// It will use basic otherwise
@ContextConfiguration(classes = {SecurityConfiguration.class, EventsApplication.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User user, user2;


    @Before
    public void setup(){
        user = new User("sgaamuwa", "password", "sgaamuwa@gmail.com");
        user2 = new User("jbawaya", "password");
        Mockito.when(userService.saveUser(user)).thenReturn(user);
    }
    
    @Test
    @WithMockUser
    public void testReturnsUsersWhenUserIsValid() throws Exception {
        Mockito.when(userService.getAllUsers()).thenReturn(Arrays.asList(user, user2));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testReturns403ForbiddenIfUserIsNotValidOnGetUsers() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void testReturnsUserGivenIdIfUserIsValid() throws Exception{
        Mockito.when(userService.getUserById(1)).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("sgaamuwa")));
    }



}
