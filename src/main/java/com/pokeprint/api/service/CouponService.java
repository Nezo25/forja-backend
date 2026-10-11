package com.pokeprint.api.service;

import com.pokeprint.api.dto.CouponValidationResponseDTO;
import java.math.BigDecimal;

public interface CouponService {
    CouponValidationResponseDTO validateAndApplyCoupon(String code, BigDecimal subtotal, String customerEmail);
}
