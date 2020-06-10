package com.events.events.integration.user;

import com.events.events.models.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.security.Principal;
import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserIntegrationTests extends BaseIntegrationTest{

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Principal mockPrincipal;


    @Before
    public void setup(){
        mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("samuelgaamuwa");

    }

    @Test
    @WithMockUser
    public void testReturnsUsersWhenUserIsValid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/v1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId", is(91)))
                .andExpect(jsonPath("$[0].username", is("samuelgaamuwa")))
                .andExpect(jsonPath("$", hasSize(6)));
    }

    @Test
    public void testReturns403ForbiddenIfUserIsNotValidOnGetUsers() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                .get("/v1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void testReturnsUserGivenIdIfUserIsValid() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                .get("/v1/users/91")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("samuelgaamuwa")));
    }

    @Test
    @WithMockUser(username = "samuelgaamuwa")
    public void testUserCanRequestToFollowAUser() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/users/91/friends/95")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Friend with user id: 95 requested"));
    }

    @Test
    @WithMockUser(username = "samuelgaamuwa")
    public void testUserCantRequestToFollowSomeoneTheyAlreadyFollow() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/users/91/friends/92")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.status", is("NOT_ACCEPTABLE")))
                .andExpect(jsonPath("$.message", is("User is already following or requested to follow user with Id: 92")));
    }

    @Test
    @WithMockUser(username = "samuelgaamuwa")
    public void testCanGetAllFollowersForAUser() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                .get("/v1/users/91/followers")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId", is(92)))
                .andExpect(jsonPath("$[1].userId", is(93)))
                .andExpect(jsonPath("$", hasSize(2)));
    }



}
