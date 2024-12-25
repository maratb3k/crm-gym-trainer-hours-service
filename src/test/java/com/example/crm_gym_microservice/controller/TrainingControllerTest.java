package com.example.crm_gym_microservice.controller;

import com.example.crm_gym_microservice.config.TestSecurityConfig;
import com.example.crm_gym_microservice.dtos.TrainingSessionRequestDTO;
import com.example.crm_gym_microservice.mapper.ModelMapper;
import com.example.crm_gym_microservice.models.TrainingSession;
import com.example.crm_gym_microservice.services.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainingController.class)
@Import(TestSecurityConfig.class)
class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingService trainingService;

    @MockBean
    private ModelMapper modelMapper;

    @BeforeEach
    void setup() {
        Mockito.reset(trainingService, modelMapper);
    }

    @Test
    @WithMockUser
    void handleTrainingSession_successful() throws Exception {
        TrainingSessionRequestDTO requestDTO = new TrainingSessionRequestDTO();
        TrainingSession trainingSession = new TrainingSession();

        when(modelMapper.mapToTrainingSession(any())).thenReturn(trainingSession);

        mockMvc.perform(post("/trainings")
                        .header("TransactionID", "test-transaction-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trainerUsername\": \"trainer123\", \"trainingDate\": \"2024-12-31\", \"trainingDuration\": 2.5, \"actionType\": \"ADD\"}"))
                .andExpect(status().isOk());

        verify(modelMapper).mapToTrainingSession(any());
        verify(trainingService).handleTrainingSession(eq(trainingSession), eq("test-transaction-id"));
    }

    @Test
    @WithMockUser
    void getTrainerMonthlyDuration_returnCorrectDuration() throws Exception {
        when(trainingService.getMonthlyTrainingDuration("trainer123", 2024, 12)).thenReturn(15.0);

        mockMvc.perform(get("/trainings/trainer123/2024/12")
                        .header("TransactionID", "test-transaction-id")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("15.0"));

        verify(trainingService).getMonthlyTrainingDuration("trainer123", 2024, 12);
    }
}
