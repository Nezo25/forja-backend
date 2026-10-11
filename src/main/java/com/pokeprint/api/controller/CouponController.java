package com.pokeprint.api.controller;

import com.pokeprint.api.domain.entity.Coupon;
import com.pokeprint.api.repository.CouponRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponRepository couponRepository;

    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        return ResponseEntity.ok(couponRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Coupon> createCoupon(@Valid @RequestBody Coupon coupon) {
        Coupon saved = couponRepository.save(coupon);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }
    
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Coupon> toggleCoupon(@PathVariable Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cupom não encontrado"));
        coupon.setIsActive(!coupon.getIsActive());
        return ResponseEntity.ok(couponRepository.save(coupon));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        if (!couponRepository.existsById(id)) return ResponseEntity.notFound().build();
        couponRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
