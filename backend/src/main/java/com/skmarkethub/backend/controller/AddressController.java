package com.skmarkethub.backend.controller;

import com.skmarkethub.backend.entity.Address;
import com.skmarkethub.backend.entity.User;
import com.skmarkethub.backend.repository.AddressRepository;
import com.skmarkethub.backend.repository.UserRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressController(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public record AddressRequest(
            @NotBlank @Size(max = 150) String fullName,
            @NotBlank @Size(max = 20) String mobile,
            @NotBlank @Size(max = 255) String addressLine1,
            @Size(max = 255) String addressLine2,
            @NotBlank @Size(max = 100) String city,
            @NotBlank @Size(max = 100) String state,
            @Size(max = 100) String country,
            @NotBlank @Size(max = 15) String pincode,
            @Size(max = 30) String addressType,
            Boolean isDefault
    ) {}

    private User getUser(Jwt jwt) {
        return userRepository.findByEmailIgnoreCase(jwt.getSubject())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    @GetMapping
    public List<Address> getUserAddresses(@AuthenticationPrincipal Jwt jwt) {
        User user = getUser(jwt);
        return addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(user.getId());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Address> createAddress(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody AddressRequest request) {

        User user = getUser(jwt);

        if (Boolean.TRUE.equals(request.isDefault())) {
            unsetDefaults(user.getId());
        }

        Address address = new Address();
        address.setUserId(user.getId());
        applyRequest(address, request);

        Address saved = addressRepository.save(address);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @Transactional
    public Address updateAddress(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {

        User user = getUser(jwt);
        Address address = addressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        if (Boolean.TRUE.equals(request.isDefault())) {
            unsetDefaults(user.getId());
        }

        applyRequest(address, request);
        return addressRepository.save(address);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {

        User user = getUser(jwt);
        Address address = addressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        addressRepository.delete(address);
        return ResponseEntity.noContent().build();
    }

    private void unsetDefaults(Long userId) {
        List<Address> addresses = addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);
        for (Address a : addresses) {
            if (Boolean.TRUE.equals(a.getIsDefault())) {
                a.setIsDefault(false);
                addressRepository.save(a);
            }
        }
    }

    private void applyRequest(Address address, AddressRequest request) {
        address.setFullName(request.fullName().trim());
        address.setMobile(request.mobile().trim());
        address.setAddressLine1(request.addressLine1().trim());
        address.setAddressLine2(request.addressLine2() == null ? null : request.addressLine2().trim());
        address.setCity(request.city().trim());
        address.setState(request.state().trim());
        address.setCountry(request.country() == null || request.country().isBlank() ? "India" : request.country().trim());
        address.setPincode(request.pincode().trim());
        address.setAddressType(request.addressType() == null || request.addressType().isBlank() ? "HOME" : request.addressType().trim());
        address.setIsDefault(Boolean.TRUE.equals(request.isDefault()));
    }
}
