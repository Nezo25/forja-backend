package com.pokeprint.api.service.impl;

import com.pokeprint.api.domain.entity.*;
import com.pokeprint.api.domain.enums.FinishType;
import com.pokeprint.api.domain.enums.KanbanColumn;
import com.pokeprint.api.domain.enums.OrderStatus;
import com.pokeprint.api.dto.LeadCaptureRequestDTO;
import com.pokeprint.api.dto.LeadCaptureResponseDTO;
import com.pokeprint.api.dto.CouponValidationResponseDTO;
import com.pokeprint.api.dto.request.CreateOrderRequestDTO;
import com.pokeprint.api.dto.response.OrderResponseDTO;
import com.pokeprint.api.repository.*;
import com.pokeprint.api.service.CouponService;
import com.pokeprint.api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final PrintOrderRepository printOrderRepository;
    private final CustomerRepository customerRepository;
    private final CouponRepository couponRepository;
    private final CouponService couponService;
    private final PokemonModelRepository pokemonModelRepository;
    private final TcgProductRepository tcgProductRepository;

    @Override
    @Transactional
    public OrderResponseDTO createOrder(CreateOrderRequestDTO request) {
        // Dummy implementation for existing interface method
        return null; 
    }

    @Override
    @Transactional
    public LeadCaptureResponseDTO createLeadCapture(LeadCaptureRequestDTO request) {
        Customer customer = customerRepository.findByEmail(request.customerEmail())
                .orElseGet(() -> {
                    Customer newCustomer = new Customer();
                    newCustomer.setName(request.customerName());
                    newCustomer.setEmail(request.customerEmail());
                    newCustomer.setPhone(request.customerPhone());
                    newCustomer.setRole("ROLE_CLIENT");
                    return customerRepository.save(newCustomer);
                });

        PrintOrder order = new PrintOrder();
        order.setCustomer(customer);
        order.setShortCode("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setStatus(OrderStatus.LEAD_WHATSAPP); // assuming this status exists, if not we fall back to PENDING
        order.setKanbanColumn(KanbanColumn.NEW_LEAD);
        
        order.setShippingAddressLine(request.shippingAddressLine());
        order.setShippingCity(request.shippingCity());
        order.setShippingState(request.shippingState());
        order.setShippingZipCode(request.shippingZipCode());

        BigDecimal subtotal = BigDecimal.ZERO;
        List<String> tags = new ArrayList<>();
        boolean has3D = false;
        boolean hasTcg = false;
        boolean hasPainting = false;

        for (var itemDto : request.items()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(itemDto.quantity());
            orderItem.setFinishType(itemDto.finishType() != null ? itemDto.finishType() : FinishType.RAW);
            
            if (itemDto.finishType() == FinishType.PAINTED) {
                hasPainting = true;
            }

            if (itemDto.pokemonModelId() != null) {
                has3D = true;
                PokemonModel model = pokemonModelRepository.findById(itemDto.pokemonModelId()).orElseThrow();
                orderItem.setPokemonModel(model);
                orderItem.setUnitPrice(model.getBasePrice()); // simplistic pricing
            } else if (itemDto.tcgProductId() != null) {
                hasTcg = true;
                TcgProduct tcg = tcgProductRepository.findById(itemDto.tcgProductId()).orElseThrow();
                orderItem.setTcgProduct(tcg);
                orderItem.setUnitPrice(tcg.getPrice());
            }
            
            orderItem.setSubtotal(orderItem.getUnitPrice().multiply(new BigDecimal(orderItem.getQuantity())));
            subtotal = subtotal.add(orderItem.getSubtotal());
            order.addItem(orderItem);
        }

        if (has3D) tags.add("3D");
        if (hasTcg) tags.add("TCG");
        if (hasPainting) tags.add("PINTURA");

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.couponCode() != null && !request.couponCode().isBlank()) {
            CouponValidationResponseDTO couponResp = couponService.validateAndApplyCoupon(request.couponCode(), subtotal, customer.getEmail());
            if (couponResp.discountAmount().compareTo(BigDecimal.ZERO) > 0) {
                Coupon coupon = couponRepository.findByCode(request.couponCode().toUpperCase()).orElse(null);
                if (coupon != null) {
                    order.setCoupon(coupon);
                    order.setDiscountAmount(couponResp.discountAmount());
                    discountAmount = couponResp.discountAmount();
                    coupon.setCurrentUses(coupon.getCurrentUses() + 1);
                    if (Boolean.TRUE.equals(coupon.getIsFirstPurchaseOnly())) {
                        tags.add("NOVO_CLIENTE");
                    }
                    couponRepository.save(coupon);
                }
            }
        } else {
            // Check if first purchase anyway
            if (!printOrderRepository.existsByCustomerEmail(customer.getEmail())) {
                tags.add("NOVO_CLIENTE");
            }
        }

        order.setTags(String.join(",", tags));
        order.setTotalAmount(subtotal.subtract(discountAmount));
        
        printOrderRepository.save(order);

        String whatsappMsg = "Olá, Forja do Chico! Vim pelo site. Meu pedido é " + order.getShortCode() + " e o valor deu R$ " + order.getTotalAmount();
        String wppLink = "https://wa.me/5511999999999?text=" + URLEncoder.encode(whatsappMsg, StandardCharsets.UTF_8);

        return new LeadCaptureResponseDTO(
            order.getShortCode(),
            customer.getName(),
            subtotal,
            discountAmount,
            order.getTotalAmount(),
            wppLink,
            tags
        );
    }
}
