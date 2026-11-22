package com.skmarkethub.backend.service;

import com.skmarkethub.backend.entity.*;
import com.skmarkethub.backend.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CouponRepository couponRepository;
    private final PaymentRepository paymentRepository;
    private final ShipmentRepository shipmentRepository;
    private final RefundRepository refundRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            AddressRepository addressRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            ProductVariantRepository productVariantRepository,
            CouponRepository couponRepository,
            PaymentRepository paymentRepository,
            ShipmentRepository shipmentRepository,
            RefundRepository refundRepository,
            ReviewRepository reviewRepository,
            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.addressRepository = addressRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.couponRepository = couponRepository;
        this.paymentRepository = paymentRepository;
        this.shipmentRepository = shipmentRepository;
        this.refundRepository = refundRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Map<String, Object> checkout(String email, Long addressId, String couponCode) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Address address = addressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid delivery address"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty"));

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> preparedOrderItems = new ArrayList<>();

        for (CartItem ci : cartItems) {
            Product product = productRepository.findById(ci.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

            ProductVariant variant = ci.getVariantId() != null
                    ? productVariantRepository.findById(ci.getVariantId()).orElse(null) : null;

            BigDecimal unitPrice = (variant != null && variant.getSalePrice() != null) ? variant.getSalePrice()
                    : (variant != null && variant.getPrice() != null) ? variant.getPrice()
                    : (product.getSalePrice() != null) ? product.getSalePrice()
                    : product.getBasePrice();

            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(ci.getQuantity()));
            subtotal = subtotal.add(itemTotal);

            OrderItem oi = new OrderItem();
            oi.setProductId(product.getId());
            oi.setVariantId(ci.getVariantId());
            oi.setProductName(product.getName());
            oi.setSku(variant != null ? variant.getSku() : product.getSku());
            oi.setQuantity(ci.getQuantity());
            oi.setUnitPrice(unitPrice);
            oi.setTotalPrice(itemTotal);
            preparedOrderItems.add(oi);
        }

        BigDecimal discount = BigDecimal.ZERO;
        Long couponId = null;

        if (couponCode != null && !couponCode.isBlank()) {
            Optional<Coupon> optCoupon = couponRepository.findByCodeIgnoreCase(couponCode.trim());
            if (optCoupon.isPresent() && Boolean.TRUE.equals(optCoupon.get().getActive())) {
                Coupon coupon = optCoupon.get();
                couponId = coupon.getId();
                if ("PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType())) {
                    discount = subtotal.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    if (coupon.getMaximumDiscount() != null && discount.compareTo(coupon.getMaximumDiscount()) > 0) {
                        discount = coupon.getMaximumDiscount();
                    }
                } else {
                    discount = coupon.getDiscountValue();
                }
                if (discount.compareTo(subtotal) > 0) discount = subtotal;
                coupon.setUsedCount(coupon.getUsedCount() + 1);
                couponRepository.save(coupon);
            }
        }

        BigDecimal shipping = subtotal.compareTo(BigDecimal.valueOf(500)) > 0 ? BigDecimal.ZERO : BigDecimal.valueOf(50);
        BigDecimal total = subtotal.subtract(discount).add(shipping);

        String orderNumber = "ORD-" + System.currentTimeMillis();

        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setUserId(user.getId());
        order.setAddressId(address.getId());
        order.setCouponId(couponId);
        order.setSubtotal(subtotal);
        order.setDiscountAmount(discount);
        order.setTaxAmount(BigDecimal.ZERO);
        order.setShippingAmount(shipping);
        order.setTotalAmount(total);
        order.setOrderStatus("PLACED");
        order.setPaymentStatus("PENDING");

        Order savedOrder = orderRepository.save(order);

        for (OrderItem oi : preparedOrderItems) {
            oi.setOrderId(savedOrder.getId());
            orderItemRepository.save(oi);
        }

        String paymentRef = "PAY-" + System.currentTimeMillis();
        Payment payment = new Payment();
        payment.setOrderId(savedOrder.getId());
        payment.setPaymentReference(paymentRef);
        payment.setProvider("SANDBOX");
        payment.setAmount(total);
        payment.setPaymentStatus("PENDING");
        paymentRepository.save(payment);

        Shipment shipment = new Shipment();
        shipment.setOrderId(savedOrder.getId());
        shipment.setShipmentStatus("PENDING");
        shipmentRepository.save(shipment);

        cartItemRepository.deleteByCartId(cart.getId());

        return getOrderView(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUserOrders(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::getOrderView).toList();
    }

    @Transactional
    public Map<String, Object> processPayment(String email, Long orderId, String paymentMethod) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment record not found"));

        payment.setPaymentMethod(paymentMethod == null ? "CARD" : paymentMethod.toUpperCase());
        payment.setPaymentStatus("COMPLETED");
        payment.setGatewayTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        paymentRepository.save(payment);

        order.setPaymentStatus("PAID");
        order.setOrderStatus("PROCESSING");
        orderRepository.save(order);

        return getOrderView(order);
    }

    @Transactional
    public Map<String, Object> requestRefund(String email, Long orderId, String reason) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment record not found"));

        Refund refund = new Refund();
        refund.setOrderId(order.getId());
        refund.setPaymentId(payment.getId());
        refund.setRefundAmount(order.getTotalAmount());
        refund.setReason(reason == null ? "Customer requested refund" : reason.trim());
        refund.setRefundStatus("REQUESTED");
        refundRepository.save(refund);

        order.setOrderStatus("REFUND_REQUESTED");
        orderRepository.save(order);

        return getOrderView(order);
    }

    private Map<String, Object> getOrderView(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        Optional<Payment> payment = paymentRepository.findByOrderId(order.getId());
        Optional<Shipment> shipment = shipmentRepository.findByOrderId(order.getId());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("orderId", order.getId());
        map.put("orderNumber", order.getOrderNumber());
        map.put("subtotal", order.getSubtotal());
        map.put("discountAmount", order.getDiscountAmount());
        map.put("shippingAmount", order.getShippingAmount());
        map.put("totalAmount", order.getTotalAmount());
        map.put("orderStatus", order.getOrderStatus());
        map.put("paymentStatus", order.getPaymentStatus());
        map.put("createdAt", order.getCreatedAt());
        map.put("items", items);
        map.put("payment", payment.orElse(null));
        map.put("shipment", shipment.orElse(null));
        return map;
    }
}
