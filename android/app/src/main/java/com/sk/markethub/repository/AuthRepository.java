package com.sk.markethub.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.sk.markethub.api.ApiClient;
import com.sk.markethub.model.*;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    public LiveData<Resource<AuthResponse>> login(String email, String password) {
        MutableLiveData<Resource<AuthResponse>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        ApiClient.getService().login(new LoginRequest(email, password)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    data.setValue(Resource.error("Invalid email or password"));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                data.setValue(Resource.error(t.getMessage() != null ? t.getMessage() : "Network failure"));
            }
        });

        return data;
    }

    public LiveData<Resource<AuthResponse>> register(String email, String password, String firstName, String lastName, String mobile) {
        MutableLiveData<Resource<AuthResponse>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        ApiClient.getService().register(new RegisterRequest(email, password, firstName, lastName, mobile))
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            data.setValue(Resource.success(response.body()));
                        } else {
                            String errorMsg = "Registration failed or email already exists";
                            try {
                                if (response.errorBody() != null) {
                                    String err = response.errorBody().string();
                                    if (err.contains("Email already registered")) errorMsg = "Email already registered";
                                    else if (err.contains("Mobile already registered")) errorMsg = "Mobile number already registered";
                                    else if (err.contains("password")) errorMsg = "Password must be at least 8 characters long";
                                }
                            } catch (Exception ignored) {}
                            data.setValue(Resource.error(errorMsg));
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        data.setValue(Resource.error(t.getMessage() != null ? t.getMessage() : "Network failure"));
                    }
                });

        return data;
    }

    public LiveData<Resource<User>> getMe() {
        MutableLiveData<Resource<User>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        ApiClient.getService().getMe().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    data.setValue(Resource.error("Unauthorized or session expired"));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                data.setValue(Resource.error("Network error"));
            }
        });

        return data;
    }

    public LiveData<Resource<Map<String, String>>> forgotPassword(String email) {
        MutableLiveData<Resource<Map<String, String>>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        Map<String, Object> body = Map.of("email", email);
        ApiClient.getService().forgotPassword(body).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    data.setValue(Resource.error("User email not found"));
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                data.setValue(Resource.error("Network error"));
            }
        });

        return data;
    }

    public LiveData<Resource<Map<String, String>>> resetPassword(String email, String newPassword) {
        MutableLiveData<Resource<Map<String, String>>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        Map<String, Object> body = Map.of("email", email, "newPassword", newPassword);
        ApiClient.getService().resetPassword(body).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    data.setValue(Resource.error("Password reset failed"));
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                data.setValue(Resource.error("Network error"));
            }
        });

        return data;
    }

    public LiveData<Resource<Map<String, String>>> sendOtp(String email) {
        MutableLiveData<Resource<Map<String, String>>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        Map<String, Object> body = Map.of("email", email);
        ApiClient.getService().sendOtp(body).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    data.setValue(Resource.error("Failed to send OTP"));
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                data.setValue(Resource.error("Network error"));
            }
        });

        return data;
    }

    public LiveData<Resource<Map<String, String>>> verifyOtp(String email, String otpCode) {
        MutableLiveData<Resource<Map<String, String>>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        Map<String, Object> body = Map.of("email", email, "otpCode", otpCode);
        ApiClient.getService().verifyOtp(body).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    data.setValue(Resource.error("Invalid or expired OTP"));
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                data.setValue(Resource.error("Network error"));
            }
        });

        return data;
    }

    public LiveData<Resource<Map<String, Object>>> submitSellerApplication(String storeName, String businessName, String gstNumber) {
        MutableLiveData<Resource<Map<String, Object>>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        ApiClient.getService().submitSellerApplication(new SellerApplicationRequest(storeName, businessName, gstNumber))
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            data.setValue(Resource.success(response.body()));
                        } else {
                            data.setValue(Resource.error("Seller application failed or already exists"));
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        data.setValue(Resource.error("Network error"));
                    }
                });

        return data;
    }
}
