package com.pokeprint.api.service.impl;

import com.pokeprint.api.domain.entity.Coupon;
import com.pokeprint.api.domain.enums.DiscountType;
import com.pokeprint.api.dto.CouponValidationResponseDTO;
import com.pokeprint.api.repository.CouponRepository;
import com.pokeprint.api.repository.PrintOrderRepository;
import com.pokeprint.api.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final PrintOrderRepository printOrderRepository;

    @Override
    public CouponValidationResponseDTO validateAndApplyCoupon(String code, BigDecimal subtotal, String customerEmail) {
        if (code == null || code.isBlank()) {
            return new CouponValidationResponseDTO(null, BigDecimal.ZERO, subtotal);
        }

        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Cupom não encontrado."));

        if (!coupon.getIsActive()) {
            throw new IllegalArgumentException("Cupom inativo.");
        }

        if (coupon.getValidUntil() != null && coupon.getValidUntil().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cupom expirado.");
        }

        if (coupon.getMaxUses() != null && coupon.getCurrentUses() >= coupon.getMaxUses()) {
            throw new IllegalArgumentException("Cupom esgotado.");
        }

        if (coupon.getMinOrderValue() != null && subtotal.compareTo(coupon.getMinOrderValue()) < 0) {
            throw new IllegalArgumentException("Valor mínimo não atingido para este cupom.");
        }

        if (Boolean.TRUE.equals(coupon.getIsFirstPurchaseOnly())) {
            boolean hasOrders = printOrderRepository.existsByCustomerEmail(customerEmail);
            if (hasOrders) {
                throw new IllegalArgumentException("Este cupom é válido apenas para a primeira compra.");
            }
        }

        BigDecimal discountAmount;
        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            discountAmount = subtotal.multiply(coupon.getDiscountValue()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        } else {
            discountAmount = coupon.getDiscountValue();
        }

        if (discountAmount.compareTo(subtotal) > 0) {
            discountAmount = subtotal;
        }

        BigDecimal newSubtotal = subtotal.subtract(discountAmount);
        
        return new CouponValidationResponseDTO(coupon.getCode(), discountAmount, newSubtotal);
    }
}
