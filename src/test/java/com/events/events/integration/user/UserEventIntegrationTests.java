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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
                .andExpect(jsonPath("$", hasSize(3)))
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
                        "\t\"startTime\" : \""+ LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(10)+"\",\n" +
                        "\t\"endTime\" : \""+ LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(10).plusHours(2)+"\"\n" +
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

    @Test
    @WithMockUser(username = "samuelgaamuwa")
    public void testCanGetEventsUserIsInvitedTo() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                .get("/v1/users/91/events/invites")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventId", is(93)))
                .andExpect(jsonPath("$[1].eventId", is(94)));
    }

    @Test
    @WithMockUser(username = "samuelgaamuwa")
    public void testCanGetEventsUserIsAttending() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                .get("/v1/users/91/events/attending")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventId", is(91)))
                .andExpect(jsonPath("$[1].eventId", is(92)));
    }

    @Test
    @WithMockUser(username = "snazziwa")
    public void testCanAddInviteesToEvent() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/users/93/events/98/invites")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "\t\"invitees\": [94, 95, 91, 92, 96]\n" +
                        "}")
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invitees", hasSize(2)))
                .andExpect(jsonPath("$.invitees[0].userId", is(94)))
                .andExpect(jsonPath("$.invitees[1].userId", is(95)));
    }

    @Test
    @WithMockUser(username = "samuelgaamuwa")
    public void testCanGetParticipantsForAnEvent() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                .get("/v1/users/91/events/attending")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventId", is(91)))
                .andExpect(jsonPath("$[1].eventId", is(92)));
    }

    @Test
    @WithMockUser(username = "samuelgaamuwa")
    public void testCanDeleteInviteeFromEvent() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/v1/users/91/events/99/invites/92")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invitees", hasSize(1)))
                .andExpect(jsonPath("$.invitees[0].userId", is(93)));
    }

    @Test
    @WithMockUser(username = "jbawaya")
    public void testCantDeleteInviteeIfLoggedInUserNotCreatorOfEvent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/v1/users/91/events/99/invites/92")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("You do not have the required permission to complete this operation")));
    }

    @Test
    @WithMockUser(username = "samuelgaamuwa")
    public void testReturnsBadRequestIfInviteeIdIsNotInvited() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/v1/users/91/events/99/invites/95")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("User with id :95 is not invited to the event id: 99")));
    }
}
