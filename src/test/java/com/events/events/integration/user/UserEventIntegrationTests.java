package com.events.events.integration.user;

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

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserEventIntegrationTests extends BaseIntegrationTest{

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
    @WithMockUser(username = "samuelgaamuwa")
    public void testGetEventsWhenUserIsValid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/v1/users/91/events")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Snockling")))
                .andExpect(jsonPath("$[1].title", is("Bungee Jumping")));
    }

    @Test
    @WithMockUser(username = "jbawaya")
    public void testCanCreateEventsWhenUserIsValid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/users/92/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "\t\"title\": \"Man of Steel 2\",\n" +
                        "\t\"location\" : \"Garden City\",\n" +
                        "\t\"description\" : \"Going to watch a sequel for a great movie\",\n" +
                        "\t\"link\" : \"https://www.google.com\",\n" +
                        "\t\"date\" : \""+LocalDate.now().plusDays(10)+"\"\n" +
                        "}")
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Man of Steel 2"))
                .andExpect(jsonPath("$.location").value("Garden City"));
    }

    @Test
    @WithMockUser(username = "samuelgaamuwa")
    public void testCantCreateEventsIfUserIdDoesntExist() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/users/51/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "\t\"title\": \"Man of Steel 2\",\n" +
                        "\t\"location\" : \"Garden City\",\n" +
                        "\t\"description\" : \"Going to watch a sequel for a great movie\",\n" +
                        "\t\"link\" : \"https://www.google.com\",\n" +
                        "\t\"date\" : \""+LocalDate.now().plusDays(10)+"\"\n" +
                        "}")
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
