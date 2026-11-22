package com.skmarkethub.backend.controller;

import com.skmarkethub.backend.entity.*;
import com.skmarkethub.backend.repository.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/seller")
public class SellerOrderController {

    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShipmentRepository shipmentRepository;
    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;

    public SellerOrderController(
            InventoryRepository inventoryRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ShipmentRepository shipmentRepository,
            ProductRepository productRepository,
            SellerRepository sellerRepository,
            UserRepository userRepository) {
        this.inventoryRepository = inventoryRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.shipmentRepository = shipmentRepository;
        this.productRepository = productRepository;
        this.sellerRepository = sellerRepository;
        this.userRepository = userRepository;
    }

    public record UpdateStockRequest(@Min(0) Integer quantity) {}
    public record UpdateShipmentRequest(@NotBlank String courierName, @NotBlank String trackingNumber, String status) {}

    private Seller getSeller(Jwt jwt) {
        User user = userRepository.findByEmailIgnoreCase(jwt.getSubject())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        return sellerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Seller account not found or approved"));
    }

    @GetMapping("/inventory")
    public List<Map<String, Object>> getSellerInventory(@AuthenticationPrincipal Jwt jwt) {
        Seller seller = getSeller(jwt);
        List<Product> products = productRepository.findBySellerId(seller.getId());
        List<Map<String, Object>> result = new ArrayList<>();

        for (Product p : products) {
            List<Inventory> invList = inventoryRepository.findByProductId(p.getId());
            for (Inventory inv : invList) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("inventoryId", inv.getId());
                map.put("productId", p.getId());
                map.put("productName", p.getName());
                map.put("sku", p.getSku());
                map.put("variantId", inv.getVariantId());
                map.put("availableQuantity", inv.getAvailableQuantity());
                map.put("reservedQuantity", inv.getReservedQuantity());
                map.put("reorderLevel", inv.getReorderLevel());
                result.add(map);
            }
        }
        return result;
    }

    @PutMapping("/inventory/{inventoryId}")
    @Transactional
    public ResponseEntity<Inventory> updateStock(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long inventoryId,
            @Valid @RequestBody UpdateStockRequest request) {

        getSeller(jwt);
        Inventory inv = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory item not found"));

        inv.setAvailableQuantity(request.quantity());
        return ResponseEntity.ok(inventoryRepository.save(inv));
    }

    @GetMapping("/orders")
    public List<Order> getSellerOrders(@AuthenticationPrincipal Jwt jwt) {
        Seller seller = getSeller(jwt);
        List<Product> products = productRepository.findBySellerId(seller.getId());
        Set<Long> productIds = new HashSet<>();
        for (Product p : products) productIds.add(p.getId());

        List<OrderItem> allOrderItems = orderItemRepository.findAll();
        Set<Long> sellerOrderIds = new HashSet<>();
        for (OrderItem oi : allOrderItems) {
            if (productIds.contains(oi.getProductId())) {
                sellerOrderIds.add(oi.getOrderId());
            }
        }
        return orderRepository.findAllById(sellerOrderIds);
    }

    @PutMapping("/orders/{orderId}/shipment")
    @Transactional
    public ResponseEntity<Shipment> updateShipment(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateShipmentRequest request) {

        getSeller(jwt);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        Shipment shipment = shipmentRepository.findByOrderId(order.getId())
                .orElseGet(() -> {
                    Shipment s = new Shipment();
                    s.setOrderId(order.getId());
                    return s;
                });

        shipment.setCourierName(request.courierName().trim());
        shipment.setTrackingNumber(request.trackingNumber().trim());
        shipment.setShipmentStatus(request.status() == null || request.status().isBlank() ? "SHIPPED" : request.status().toUpperCase());
        shipment.setShippedAt(LocalDateTime.now());
        if ("DELIVERED".equalsIgnoreCase(shipment.getShipmentStatus())) {
            shipment.setDeliveredAt(LocalDateTime.now());
            order.setOrderStatus("DELIVERED");
        } else {
            order.setOrderStatus("SHIPPED");
        }
        orderRepository.save(order);

        return ResponseEntity.ok(shipmentRepository.save(shipment));
    }

    @GetMapping("/analytics")
    public Map<String, Object> getAnalytics(@AuthenticationPrincipal Jwt jwt) {
        Seller seller = getSeller(jwt);
        List<Product> products = productRepository.findBySellerId(seller.getId());
        List<Order> sellerOrders = getSellerOrders(jwt);

        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (Order o : sellerOrders) {
            if ("PAID".equalsIgnoreCase(o.getPaymentStatus()) || "DELIVERED".equalsIgnoreCase(o.getOrderStatus())) {
                totalRevenue = totalRevenue.add(o.getTotalAmount());
            }
        }

        Map<String, Object> analytics = new LinkedHashMap<>();
        analytics.put("storeName", seller.getStoreName());
        analytics.put("totalProducts", products.size());
        analytics.put("totalOrders", sellerOrders.size());
        analytics.put("totalRevenue", totalRevenue);
        return analytics;
    }
}
