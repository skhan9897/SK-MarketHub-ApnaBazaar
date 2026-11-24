package com.sk.markethub.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.sk.markethub.model.*;
import com.sk.markethub.repository.AuthRepository;
import com.sk.markethub.repository.Resource;
import java.util.Map;

public class AuthViewModel extends ViewModel {

    private final AuthRepository repository;

    public AuthViewModel() {
        this.repository = new AuthRepository();
    }

    public LiveData<Resource<AuthResponse>> login(String email, String password) {
        return repository.login(email, password);
    }

    public LiveData<Resource<AuthResponse>> register(String email, String password, String firstName, String lastName, String mobile) {
        return repository.register(email, password, firstName, lastName, mobile);
    }

    public LiveData<Resource<User>> getMe() {
        return repository.getMe();
    }

    public LiveData<Resource<Map<String, String>>> forgotPassword(String email) {
        return repository.forgotPassword(email);
    }

    public LiveData<Resource<Map<String, String>>> resetPassword(String email, String newPassword) {
        return repository.resetPassword(email, newPassword);
    }

    public LiveData<Resource<Map<String, String>>> sendOtp(String email) {
        return repository.sendOtp(email);
    }

    public LiveData<Resource<Map<String, String>>> verifyOtp(String email, String otpCode) {
        return repository.verifyOtp(email, otpCode);
    }

    public LiveData<Resource<Map<String, String>>> sendMobileOtp(String mobile) {
        return repository.sendMobileOtp(mobile);
    }

    public LiveData<Resource<AuthResponse>> loginWithMobileOtp(String mobile, String otpCode, String firstName) {
        return repository.loginWithMobileOtp(mobile, otpCode, firstName);
    }

    public LiveData<Resource<Map<String, Object>>> submitSellerApplication(String storeName, String businessName, String gstNumber) {
        return repository.submitSellerApplication(storeName, businessName, gstNumber);
    }
}
