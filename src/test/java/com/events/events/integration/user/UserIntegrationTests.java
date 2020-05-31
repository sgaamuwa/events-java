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
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.security.Principal;
import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserIntegrationTests extends BaseIntegrationTest{

    @Autowired
    private MockMvc mockMvc;

    private User user, user2;

    @MockBean
    private Principal mockPrincipal;


    @Before
    public void setup(){
        user = new User("samuel", "gaamuwa","sgaamuwa", "password", "sgaamuwa@gmail.com");
        user2 = new User("joy", "bawaya", "jbawaya", "password", "jbawaya@gmail.com");
        mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("samuelgaamuwa");

    }

    @Test
    @WithMockUser
    public void testReturnsUsersWhenUserIsValid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"userId\":91,\"firstName\":\"Samuel\",\"lastName\":\"Gaamuwa\",\"username\":\"samuelgaamuwa\",\"email\":\"sgaamuwa@gmail.com\",\"createdAt\":\"2020-05-31\",\"updatedAt\":\"2020-05-31\",\"enabled\":true,\"links\":[]},{\"userId\":92,\"firstName\":\"Joy\",\"lastName\":\"Bawaya\",\"username\":\"jbawaya\",\"email\":\"jbawaya@gmail.com\",\"createdAt\":\"2020-05-31\",\"updatedAt\":\"2020-05-31\",\"enabled\":true,\"links\":[]},{\"userId\":93,\"firstName\":\"Sandra\",\"lastName\":\"Nazziwa\",\"username\":\"snazziwa\",\"email\":\"snazziwa@gmail.com\",\"createdAt\":\"2020-05-31\",\"updatedAt\":\"2020-05-31\",\"enabled\":true,\"links\":[]},{\"userId\":94,\"firstName\":\"Merab\",\"lastName\":\"Gaamuwa\",\"username\":\"mgaamuwa\",\"email\":\"mgaamuwa@gmail.com\",\"createdAt\":\"2020-05-31\",\"updatedAt\":\"2020-05-31\",\"enabled\":true,\"links\":[]},{\"userId\":95,\"firstName\":\"Edward\",\"lastName\":\"Gaamuwa\",\"username\":\"egaamuwa\",\"email\":\"egaamuwa@gmail.com\",\"createdAt\":\"2020-05-31\",\"updatedAt\":\"2020-05-31\",\"enabled\":true,\"links\":[]},{\"userId\":96,\"firstName\":\"Peace\",\"lastName\":\"Nakiyemba\",\"username\":\"pnakiyemba\",\"email\":\"pnakiyemba@gmail.com\",\"createdAt\":\"2020-05-31\",\"updatedAt\":\"2020-05-31\",\"enabled\":true,\"links\":[]}]"));
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
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/91")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("samuelgaamuwa")));
    }

    @Test
    @WithMockUser(username = "samuelgaamuwa")
    public void testUserCanRequestToFollowAUser() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users/95/followRequest")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Follower with user id: 95 requested"));
    }

    @Test
    @WithMockUser(username = "samuelgaamuwa")
    public void testUserCantRequestToFollowSomeoneTheyAlreadyFollow() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users/92/followRequest")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string("{\"status\":\"NOT_ACCEPTABLE\",\"message\":\"User is already following or requested to follow user with Id: 92\"}"));
    }

    @Test
    @WithMockUser(username = "samuelgaamuwa")
    public void testCanGetAllFollowersForAUser() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/91/followers")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"userId\":92,\"firstName\":\"Joy\",\"lastName\":\"Bawaya\",\"username\":\"jbawaya\",\"email\":\"jbawaya@gmail.com\",\"createdAt\":\"2020-05-31\",\"updatedAt\":\"2020-05-31\",\"enabled\":true,\"links\":[]},{\"userId\":93,\"firstName\":\"Sandra\",\"lastName\":\"Nazziwa\",\"username\":\"snazziwa\",\"email\":\"snazziwa@gmail.com\",\"createdAt\":\"2020-05-31\",\"updatedAt\":\"2020-05-31\",\"enabled\":true,\"links\":[]}]"));
    }



}
