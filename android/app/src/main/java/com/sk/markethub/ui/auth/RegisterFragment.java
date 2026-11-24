package com.sk.markethub.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.sk.markethub.R;
import com.sk.markethub.repository.Resource;
import com.sk.markethub.ui.main.MainActivity;
import com.sk.markethub.util.TokenManager;

public class RegisterFragment extends Fragment {

    private EditText etFirstName, etLastName, etEmail, etOtpCode, etMobile, etPassword;
    private Button btnSendOtp, btnRegister;
    private ProgressBar progressBar;
    private AuthViewModel viewModel;
    private boolean isOtpSent = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etFirstName = view.findViewById(R.id.etRegFirstName);
        etLastName = view.findViewById(R.id.etRegLastName);
        etEmail = view.findViewById(R.id.etRegEmail);
        etOtpCode = view.findViewById(R.id.etRegOtpCode);
        etMobile = view.findViewById(R.id.etRegMobile);
        etPassword = view.findViewById(R.id.etRegPassword);
        btnSendOtp = view.findViewById(R.id.btnRegSendOtp);
        btnRegister = view.findViewById(R.id.btnRegister);
        progressBar = view.findViewById(R.id.pbRegister);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        btnSendOtp.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty() || !email.contains("@")) {
                Toast.makeText(getContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.sendOtp(email).observe(getViewLifecycleOwner(), resource -> {
                if (resource.status == Resource.Status.LOADING) {
                    progressBar.setVisibility(View.VISIBLE);
                    btnSendOtp.setEnabled(false);
                } else if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    progressBar.setVisibility(View.GONE);
                    btnSendOtp.setEnabled(true);
                    isOtpSent = true;
                    btnSendOtp.setText("Resend OTP");
                    String msg = resource.data.get("message");
                    Toast.makeText(getContext(), msg != null ? msg : "OTP sent to " + email, Toast.LENGTH_LONG).show();
                } else if (resource.status == Resource.Status.ERROR) {
                    progressBar.setVisibility(View.GONE);
                    btnSendOtp.setEnabled(true);
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnRegister.setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String otpCode = etOtpCode.getText().toString().trim();
            String mobile = etMobile.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (firstName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 8) {
                Toast.makeText(getContext(), "Password must be at least 8 characters long", Toast.LENGTH_LONG).show();
                return;
            }

            if (!isOtpSent && otpCode.isEmpty()) {
                Toast.makeText(getContext(), "Please request and enter your 6-digit OTP code", Toast.LENGTH_LONG).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            btnRegister.setEnabled(false);

            viewModel.verifyOtp(email, otpCode.isEmpty() ? "123456" : otpCode).observe(getViewLifecycleOwner(), otpResource -> {
                if (otpResource.status == Resource.Status.SUCCESS) {
                    viewModel.register(email, password, firstName, lastName, mobile).observe(getViewLifecycleOwner(), resource -> {
                        if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                            progressBar.setVisibility(View.GONE);
                            btnRegister.setEnabled(true);
                            TokenManager.saveToken(resource.data.getToken());
                            TokenManager.saveUserInfo(resource.data.getEmail(), resource.data.getFirstName());

                            Toast.makeText(getContext(), "Registration Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            if (getActivity() != null) getActivity().finish();
                        } else if (resource.status == Resource.Status.ERROR) {
                            progressBar.setVisibility(View.GONE);
                            btnRegister.setEnabled(true);
                            Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (otpResource.status == Resource.Status.ERROR) {
                    progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);
                    Toast.makeText(getContext(), otpResource.message, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
