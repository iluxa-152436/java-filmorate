package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void testShouldReturnStatus200WhenCallMethodPost() throws Exception {
        Director director = new Director(1, "Dir");
        String json = mapper.writeValueAsString(director);
        MvcResult result = mockMvc.perform(post("/directors").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk()).andReturn();
        Director director1 = mapper.readValue(result.getResponse().getContentAsString(), Director.class);
        assertEquals(director, director1);
    }

    @Test
    void testShouldReturnStatus200AndDirectorWhenCallMethodGet() throws Exception {
        Director director = new Director(1, "Dir");
        Director director2 = new Director(2, "Dir2");
        String json = mapper.writeValueAsString(director);
        String json2 = mapper.writeValueAsString(director2);
        mockMvc.perform(post("/directors").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
        mockMvc.perform(post("/directors").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json2)).andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/directors").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        List<Director> directors = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<Director>>() {
                });
        assertEquals(2, directors.size());
        assertTrue(directors.contains(director));
        assertTrue(directors.contains(director2));
    }

    @Test
    void testShouldReturnStatus200AndDirectorWhenCallMethodGetById() throws Exception {
        Director director = new Director(1, "Dir");
        String json = mapper.writeValueAsString(director);
        mockMvc.perform(post("/directors").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/directors/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        Director directorFromDb = mapper.readValue(result.getResponse().getContentAsString(), Director.class);
        assertEquals(director, directorFromDb);
        assertEquals(1, directorFromDb.getId());
    }

    @Test
    void testShouldReturnStatus200AndUpdatedDirectorWhenCallMethodPut() throws Exception {
        Director director = new Director(1, "Dir");
        String json = mapper.writeValueAsString(director);
        mockMvc.perform(post("/directors").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());

        Director updatedDirector = new Director(1, "New name");
        String json2 = mapper.writeValueAsString(updatedDirector);
        MvcResult result = mockMvc.perform(put("/directors").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json2)).andExpect(status().isOk()).andReturn();
        Director returnedDirector = mapper.readValue(result.getResponse().getContentAsString(), Director.class);

        assertEquals(1, returnedDirector.getId());
        assertEquals("New name", returnedDirector.getName());

        MvcResult result2 = mockMvc.perform(get("/directors/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        Director directorFromDb = mapper.readValue(result2.getResponse().getContentAsString(), Director.class);

        assertEquals(1, directorFromDb.getId());
        assertEquals(returnedDirector, directorFromDb);
    }

    @Test
    void testShouldReturnStatus200WhenCallMethodDelete() throws Exception {
        Director director = new Director(1, "Dir");
        String json = mapper.writeValueAsString(director);
        mockMvc.perform(post("/directors").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());

        mockMvc.perform(delete("/directors/1").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());

        mockMvc.perform(get("/directors/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}