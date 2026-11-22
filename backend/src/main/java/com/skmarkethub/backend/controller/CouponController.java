package com.skmarkethub.backend.controller;

import com.skmarkethub.backend.entity.Coupon;
import com.skmarkethub.backend.repository.CouponRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponRepository couponRepository;

    public CouponController(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public record ValidateCouponRequest(
            @NotBlank String code,
            @NotNull @DecimalMin("0.00") BigDecimal orderTotal
    ) {}

    public record CouponRequest(
            @NotBlank String code,
            @NotBlank String discountType,
            @NotNull @DecimalMin("0.00") BigDecimal discountValue,
            @DecimalMin("0.00") BigDecimal minimumOrder,
            BigDecimal maximumDiscount,
            Integer usageLimit,
            LocalDateTime startsAt,
            LocalDateTime expiresAt,
            Boolean active
    ) {}

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateCoupon(@Valid @RequestBody ValidateCouponRequest request) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(request.code().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid coupon code"));

        if (!Boolean.TRUE.equals(coupon.getActive())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon is inactive");
        }

        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartsAt() != null && now.isBefore(coupon.getStartsAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon is not active yet");
        }
        if (coupon.getExpiresAt() != null && now.isAfter(coupon.getExpiresAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon has expired");
        }

        if (coupon.getUsageLimit() != null && coupon.getUsedCount() != null &&
                coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon usage limit reached");
        }

        if (coupon.getMinimumOrder() != null && request.orderTotal().compareTo(coupon.getMinimumOrder()) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Minimum order value for this coupon is " + coupon.getMinimumOrder());
        }

        BigDecimal discountAmount;
        if ("PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType())) {
            discountAmount = request.orderTotal()
                    .multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if (coupon.getMaximumDiscount() != null && discountAmount.compareTo(coupon.getMaximumDiscount()) > 0) {
                discountAmount = coupon.getMaximumDiscount();
            }
        } else {
            discountAmount = coupon.getDiscountValue();
        }

        if (discountAmount.compareTo(request.orderTotal()) > 0) {
            discountAmount = request.orderTotal();
        }

        return ResponseEntity.ok(Map.of(
                "couponId", coupon.getId(),
                "code", coupon.getCode(),
                "discountType", coupon.getDiscountType(),
                "discountValue", coupon.getDiscountValue(),
                "discountAmount", discountAmount,
                "finalTotal", request.orderTotal().subtract(discountAmount)
        ));
    }

    @GetMapping("/admin")
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    @PostMapping("/admin")
    @Transactional
    public ResponseEntity<Coupon> createCoupon(@Valid @RequestBody CouponRequest request) {
        String code = request.code().trim().toUpperCase();
        if (couponRepository.findByCodeIgnoreCase(code).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Coupon code already exists");
        }

        Coupon c = new Coupon();
        c.setCode(code);
        c.setDiscountType(request.discountType().trim().toUpperCase());
        c.setDiscountValue(request.discountValue());
        c.setMinimumOrder(request.minimumOrder() == null ? BigDecimal.ZERO : request.minimumOrder());
        c.setMaximumDiscount(request.maximumDiscount());
        c.setUsageLimit(request.usageLimit());
        c.setUsedCount(0);
        c.setStartsAt(request.startsAt());
        c.setExpiresAt(request.expiresAt());
        c.setActive(Boolean.TRUE.equals(request.active()));

        return ResponseEntity.status(HttpStatus.CREATED).body(couponRepository.save(c));
    }
}
