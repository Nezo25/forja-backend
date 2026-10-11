package com.pokeprint.api.service;

import com.pokeprint.api.domain.entity.Customer;
import com.pokeprint.api.domain.entity.FilamentInventory;
import com.pokeprint.api.domain.entity.PokemonModel;
import com.pokeprint.api.domain.enums.FinishType;
import com.pokeprint.api.dto.request.CreateOrderRequestDTO;
import com.pokeprint.api.dto.request.OrderItemRequestDTO;
import com.pokeprint.api.dto.request.ShippingAddressDTO;
import com.pokeprint.api.infra.exception.InsufficientStockException;
import com.pokeprint.api.repository.CustomerRepository;
import com.pokeprint.api.repository.FilamentInventoryRepository;
import com.pokeprint.api.repository.PokemonModelRepository;
import com.pokeprint.api.repository.PrintOrderRepository;
import com.pokeprint.api.service.impl.OrderServiceImpl;
import com.pokeprint.api.service.pricing.PricingEngineService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private PrintOrderRepository printOrderRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private PokemonModelRepository pokemonModelRepository;
    @Mock private FilamentInventoryRepository filamentInventoryRepository;
    @Mock private PricingEngineService pricingEngineService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void shouldCreateOrderSuccessfullyWithoutStockLock() {
        CreateOrderRequestDTO request = new CreateOrderRequestDTO(
            1L, 
            List.of(new OrderItemRequestDTO(1L, null, 1L, 2, FinishType.RAW, null)),
            new ShippingAddressDTO("Rua A", "Atibaia", "SP", "12940-000"),
            null
        );

        Customer customer = new Customer();
        customer.setId(1L);

        PokemonModel model = new PokemonModel();
        model.setId(1L);
        model.setName("Charizard");
        model.setIsActive(true);
        model.setBasePrice(new BigDecimal("150.00"));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(pokemonModelRepository.findById(1L)).thenReturn(Optional.of(model));
        when(printOrderRepository.save(any())).thenAnswer(invocation -> {
            com.pokeprint.api.domain.entity.PrintOrder o = invocation.getArgument(0);
            o.setId(10L);
            return o;
        });

        var response = orderService.createOrder(request);
        
        org.junit.jupiter.api.Assertions.assertNotNull(response);
        verify(printOrderRepository).save(any());
    }
}
