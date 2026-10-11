package com.pokeprint.api.service.impl;

import com.pokeprint.api.domain.entity.*;
import com.pokeprint.api.domain.enums.FinishType;
import com.pokeprint.api.domain.enums.KanbanColumn;
import com.pokeprint.api.domain.enums.OrderStatus;
import com.pokeprint.api.domain.TcgProduct;
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
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        PrintOrder order = new PrintOrder();
        order.setCustomer(customer);
        order.setShortCode("OS-" + java.time.Year.now().getValue() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setKanbanColumn(KanbanColumn.NEW_LEAD);
        order.setShippingAddressLine(request.shippingAddress().addressLine());
        order.setShippingCity(request.shippingAddress().city());
        order.setShippingState(request.shippingAddress().state());
        order.setShippingZipCode(request.shippingAddress().zipCode());

        BigDecimal total = BigDecimal.ZERO;
        List<String> tags = new ArrayList<>();
        if (request.notes() != null && !request.notes().isBlank()) {
            tags.add("OBS:" + request.notes().replace(",", " "));
        }

        for (var itemReq : request.items()) {
            OrderItem item = new OrderItem();
            item.setQuantity(itemReq.quantity());
            item.setFinishType(itemReq.finishType() != null ? itemReq.finishType() : FinishType.RAW);
            
            BigDecimal unitPrice = BigDecimal.ZERO;
            if (itemReq.pokemonModelId() != null) {
                tags.add("3D");
                PokemonModel model = pokemonModelRepository.findById(itemReq.pokemonModelId()).orElse(null);
                item.setPokemonModel(model);
                if (model != null && model.getBasePrice() != null) {
                    unitPrice = model.getBasePrice();
                }
            } else if (itemReq.tcgProductId() != null) {
                tags.add("TCG");
                TcgProduct tcg = tcgProductRepository.findById(itemReq.tcgProductId()).orElse(null);
                item.setTcgProduct(tcg);
                if (tcg != null && tcg.getPrice() != null) {
                    unitPrice = tcg.getPrice();
                }
            }

            item.setUnitPrice(unitPrice);
            item.setSubtotal(unitPrice.multiply(BigDecimal.valueOf(itemReq.quantity())));
            total = total.add(item.getSubtotal());
            order.addItem(item);
        }

        order.setTags(String.join(",", tags));
        order.setTotalAmount(total);
        PrintOrder saved = printOrderRepository.save(order);

        List<com.pokeprint.api.dto.response.OrderItemResponseDTO> itemDTOs = saved.getItems().stream()
                .map(item -> new com.pokeprint.api.dto.response.OrderItemResponseDTO(
                        item.getId(),
                        item.getPokemonModel() != null ? item.getPokemonModel().getId() : null,
                        item.getPokemonModel() != null ? item.getPokemonModel().getName() : (item.getTcgProduct() != null ? item.getTcgProduct().getName() : "Item"),
                        item.getFilamentInventory() != null ? item.getFilamentInventory().getId() : null,
                        item.getFinishType(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                )).toList();

        return new OrderResponseDTO(
                saved.getId(),
                customer.getId(),
                saved.getStatus(),
                saved.getTotalAmount(),
                saved.getTrackingCode(),
                saved.getShippingAddressLine(),
                saved.getShippingCity(),
                saved.getShippingState(),
                saved.getShippingZipCode(),
                itemDTOs,
                saved.getCreatedAt()
        );
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
        order.setShortCode("OS-" + java.time.Year.now().getValue() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        order.setStatus(OrderStatus.LEAD_WHATSAPP);
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
            
            if (itemDto.finishType() == FinishType.HAND_PAINTED) {
                hasPainting = true;
            }

            if (itemDto.pokemonModelId() != null) {
                has3D = true;
                PokemonModel model = pokemonModelRepository.findById(itemDto.pokemonModelId()).orElse(null);
                orderItem.setPokemonModel(model);
                orderItem.setUnitPrice(model != null && model.getBasePrice() != null ? model.getBasePrice() : BigDecimal.ZERO);
            } else if (itemDto.tcgProductId() != null) {
                hasTcg = true;
                TcgProduct tcg = tcgProductRepository.findById(itemDto.tcgProductId()).orElse(null);
                orderItem.setTcgProduct(tcg);
                orderItem.setUnitPrice(tcg != null && tcg.getPrice() != null ? tcg.getPrice() : BigDecimal.ZERO);
            } else {
                orderItem.setUnitPrice(BigDecimal.ZERO);
            }
            
            orderItem.setSubtotal(orderItem.getUnitPrice().multiply(new BigDecimal(orderItem.getQuantity())));
            subtotal = subtotal.add(orderItem.getSubtotal());
            order.addItem(orderItem);
        }

        if (has3D) tags.add("3D");
        if (hasTcg) tags.add("TCG");
        if (hasPainting) tags.add("PINTURA");
        if (request.observations() != null && !request.observations().isBlank()) {
            tags.add("OBS:" + request.observations().replace(",", " "));
        }

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
            try {
                boolean hasPreviousOrders = customer.getId() != null 
                    ? printOrderRepository.existsByCustomerId(customer.getId())
                    : printOrderRepository.existsByCustomerEmail(customer.getEmail());
                if (!hasPreviousOrders) {
                    tags.add("NOVO_CLIENTE");
                }
            } catch (Exception ex) {
                tags.add("NOVO_CLIENTE");
            }
        }

        order.setTags(String.join(",", tags));
        order.setTotalAmount(subtotal.subtract(discountAmount));
        
        printOrderRepository.save(order);

        String whatsappMsg = "🔥 Ordem de Serviço #" + order.getShortCode() + " | Cliente: " + customer.getEmail() + "\n" + "Olá, Forja do Chico! Vim pelo site. O valor deu R$ " + order.getTotalAmount();
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
