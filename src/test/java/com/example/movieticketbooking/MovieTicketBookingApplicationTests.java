package com.example.movieticketbooking;

import com.example.movieticketbooking.entity.Show;
import com.example.movieticketbooking.repository.ShowRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MovieTicketBookingApplicationTests {
    @Autowired MockMvc mvc;
    @Autowired ShowRepository shows;
    @Autowired ObjectMapper mapper;

    @Test void registrationIsPublicAndValidatesDuplicateEmail() throws Exception {
        String body = "{\"name\":\"Test User\",\"email\":\"test@example.com\",\"password\":\"Password@123\"}";
        mvc.perform(post("/api/v1/auth/register").contentType("application/json").content(body)).andExpect(status().isCreated());
        mvc.perform(post("/api/v1/auth/register").contentType("application/json").content(body)).andExpect(status().isConflict());
    }

    @Test void adminEndpointRequiresAdminRole() throws Exception {
        mvc.perform(get("/api/v1/admin/movies").with(httpBasic("customer@example.com","Customer@12345"))).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/admin/movies").with(httpBasic("admin@example.com","Admin@12345"))).andExpect(status().isOk());
    }

    @Test void customerCanHoldAndPayForASeat() throws Exception {
        Show show = shows.findAll().get(0);
        MvcResult seatsResult = mvc.perform(get("/api/v1/shows/{id}/seats", show.getId()).with(httpBasic("customer@example.com","Customer@12345"))).andExpect(status().isOk()).andReturn();
        var seats = mapper.readTree(seatsResult.getResponse().getContentAsString());
        String seatId = seats.get(0).get("showSeatId").asText();
        String holdBody = mapper.writeValueAsString(Map.of("showSeatIds", java.util.List.of(seatId)));
        MvcResult hold = mvc.perform(post("/api/v1/shows/{id}/holds",show.getId()).with(httpBasic("customer@example.com","Customer@12345")).contentType("application/json").content(holdBody)).andExpect(status().isCreated()).andReturn();
        String bookingId = mapper.readTree(hold.getResponse().getContentAsString()).get("id").asText();
        mvc.perform(post("/api/v1/bookings/{id}/payment/success",bookingId).with(httpBasic("customer@example.com","Customer@12345"))).andExpect(status().isOk());
        mvc.perform(post("/api/v1/shows/{id}/holds",show.getId()).with(httpBasic("customer@example.com","Customer@12345")).contentType("application/json").content(holdBody)).andExpect(status().isConflict());
    }
}
