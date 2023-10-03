package com.klimmenkov.testtask.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klimmenkov.testtask.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateUser() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2000, Calendar.JANUARY, 1);
        Date birthDate = calendar.getTime();
        User user = new User("petr@gmail.com", "Petr", "Kulinich", birthDate, "123 Main St", "0506667788");

        String userJson = mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn().getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        Long userId = objectMapper.readTree(userJson).get("id").asLong();

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteUser() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2000, Calendar.JANUARY, 1);
        Date birthDate = calendar.getTime();
        User user = new User("petr@gmail.com", "Petr", "Kulinich", birthDate, "123 Main St", "0506667788");

        String userJson = mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        Long userId = objectMapper.readTree(userJson).get("id").asLong();

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());

        mockMvc.perform(put("/users/", userId))
                .andExpect(status().isNotFound());
    }


    @Test
    public void testPartiallyUpdateUser() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1995, Calendar.JANUARY, 1);
        Date birthDate = calendar.getTime();
        User originalUser = new User("john_doe@gmail.com", "John", "Doe", birthDate, "123 Main St", "555-555-5555");

        String userJson = mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(originalUser)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        Long userId = objectMapper.readTree(userJson).get("id").asLong();

        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", "UpdatedFirstName");
        updates.put("email", "updated_email@example.com");

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.firstName", is("UpdatedFirstName")))
                .andExpect(jsonPath("$.email", is("updated_email@example.com")));

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateUser() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1990, Calendar.JANUARY, 1);
        Date birthDate = calendar.getTime();

        mockMvc.perform(put("/users/{userId}", 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new User("test_updated@gmail.com", "UpdatedFirstName", "UpdatedLastName",
                                birthDate, "UpdatedAddress", "UpdatedPhoneNumber"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.firstName", is("UpdatedFirstName")))
                .andExpect(jsonPath("$.email", is("test_updated@gmail.com")));
    }

    @Test
    public void testSearchUsersByBirthDateRange() throws Exception {
        Calendar fromDate = Calendar.getInstance();
        fromDate.set(2000, Calendar.JANUARY, 1);
        Calendar toDate = Calendar.getInstance();
        toDate.set(2005, Calendar.JANUARY, 1);

        mockMvc.perform(get("/users/search")
                        .param("fromDate", "2000-01-01")
                        .param("toDate", "2005-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()));
    }

    @Test
    public void testInvalidSearchUsersByBirthDateRange() throws Exception {
        mockMvc.perform(get("/users/search")
                        .param("fromDate", "2005-01-01")
                        .param("toDate", "2000-01-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUserNotFoundException() throws Exception {
        mockMvc.perform(put("/users/", 0L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAgeNotAllowedException() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2010, Calendar.JANUARY, 1);
        Date birthDate = calendar.getTime();
        User user = new User("young@example.com", "LastName", "FirstName", birthDate, "Address", "PhoneNumber");

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateUserWithEmailValidationError() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2000, Calendar.JANUARY, 1);
        Date birthDate = calendar.getTime();
        User user = new User("null", "test", "TEST", birthDate, "ADDRESS", "0997975514");

        String userJson = asJsonString(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test
    public void testCreateUserWithNullFirstName() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2000, Calendar.JANUARY, 1);
        Date birthDate = calendar.getTime();
        User user = new User("test@example.com", null, "TEST", birthDate, "ADDRESS", "0997975514");

        String userJson = asJsonString(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testCreateUserWithNullLastName() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2000, Calendar.JANUARY, 1);
        Date birthDate = calendar.getTime();
        User user = new User("test@example.com", "Test", null, birthDate, "ADDRESS", "0997975514");

        String userJson = asJsonString(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}