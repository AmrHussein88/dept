package com.intr.debt.controller;

import com.intr.debt.service.WakandaProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ManualTriggerController.class)
public class ManualTriggerControllerTest {
    @Autowired
    MockMvc mvc;

    @MockitoBean
    WakandaProcessor wakandaProcessor;

    @Test
    void triggerWakandaRun_returns202_and_callsProcessor() throws Exception {
        mvc.perform(post("/payouts/process-payout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("started"))
                .andExpect(jsonPath("$.source").value("wakanda"));

        verify(wakandaProcessor, times(1)).processPayout();
    }
}
