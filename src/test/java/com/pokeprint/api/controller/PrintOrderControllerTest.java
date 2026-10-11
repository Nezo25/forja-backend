package com.pokeprint.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokeprint.api.dto.request.CreateOrderRequestDTO;
import com.pokeprint.api.dto.request.ShippingAddressDTO;
import com.pokeprint.api.service.OrderService;
import com.pokeprint.api.service.OrderStatusTransitionService;
import com.pokeprint.api.repository.PrintOrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PrintOrderController.class)
class PrintOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private OrderService orderService;
    @MockBean private OrderStatusTransitionService statusTransitionService;
    @MockBean private PrintOrderRepository printOrderRepository;

    @Test
    void shouldReturn400WhenItemsListIsEmpty() throws Exception {
        CreateOrderRequestDTO request = new CreateOrderRequestDTO(
            1L, 
            new ArrayList<>(), // Empty list triggers @NotEmpty validation
            new ShippingAddressDTO("Rua A", "Atibaia", "SP", "12940-000"),
            null
        );

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request Parameters"))
                .andExpect(jsonPath("$.invalidFields").isArray());
    }
}
