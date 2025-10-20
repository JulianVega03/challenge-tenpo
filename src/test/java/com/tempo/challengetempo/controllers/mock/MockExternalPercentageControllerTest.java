package com.tempo.challengetempo.controllers.mock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MockExternalPercentageController.class)
public class MockExternalPercentageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String BASE_URL = "/mock/external/percentage";

    @Test
    void getExternalPercentage_shouldReturnOkAndValidJsonStructure() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.percentage").isNumber())
                .andExpect(jsonPath("$.source").value("mock-service"));
    }

}
