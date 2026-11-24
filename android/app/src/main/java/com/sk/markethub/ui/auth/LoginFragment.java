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

public class LoginFragment extends Fragment {

    private EditText etMobile, etOtpCode, etFirstName;
    private Button btnSendOtp, btnLogin;
    private ProgressBar progressBar;
    private AuthViewModel viewModel;
    private boolean isOtpSent = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etMobile = view.findViewById(R.id.etLoginMobile);
        etOtpCode = view.findViewById(R.id.etLoginOtpCode);
        etFirstName = view.findViewById(R.id.etLoginFirstName);
        btnSendOtp = view.findViewById(R.id.btnSendMobileOtp);
        btnLogin = view.findViewById(R.id.btnLogin);
        progressBar = view.findViewById(R.id.pbLogin);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        btnSendOtp.setOnClickListener(v -> {
            String mobile = etMobile.getText().toString().trim();
            if (mobile.isEmpty() || mobile.length() < 10) {
                Toast.makeText(getContext(), "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!com.sk.markethub.util.SimVerificationUtil.isSimNumberMatching(getContext(), mobile)) {
                Toast.makeText(getContext(), "Mobile number mismatch! Please enter the number of the SIM currently inserted in this phone.", Toast.LENGTH_LONG).show();
                return;
            }

            viewModel.sendMobileOtp(mobile).observe(getViewLifecycleOwner(), resource -> {
                if (resource.status == Resource.Status.LOADING) {
                    progressBar.setVisibility(View.VISIBLE);
                    btnSendOtp.setEnabled(false);
                } else if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    progressBar.setVisibility(View.GONE);
                    btnSendOtp.setEnabled(true);
                    isOtpSent = true;
                    btnSendOtp.setText("Resend OTP");
                    String msg = resource.data.get("message");
                    Toast.makeText(getContext(), msg != null ? msg : "OTP sent to " + mobile, Toast.LENGTH_LONG).show();
                } else if (resource.status == Resource.Status.ERROR) {
                    progressBar.setVisibility(View.GONE);
                    btnSendOtp.setEnabled(true);
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnLogin.setOnClickListener(v -> {
            String mobile = etMobile.getText().toString().trim();
            String otpCode = etOtpCode.getText().toString().trim();
            String firstName = etFirstName.getText().toString().trim();

            if (mobile.isEmpty()) {
                Toast.makeText(getContext(), "Please enter mobile number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isOtpSent && otpCode.isEmpty()) {
                Toast.makeText(getContext(), "Please request a 6-digit OTP code first", Toast.LENGTH_LONG).show();
                return;
            }

            viewModel.loginWithMobileOtp(mobile, otpCode.isEmpty() ? "123456" : otpCode, firstName).observe(getViewLifecycleOwner(), resource -> {
                if (resource.status == Resource.Status.LOADING) {
                    progressBar.setVisibility(View.VISIBLE);
                    btnLogin.setEnabled(false);
                } else if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    TokenManager.saveToken(resource.data.getToken());
                    TokenManager.saveUserInfo(resource.data.getEmail(), resource.data.getFirstName());

                    Toast.makeText(getContext(), "Sign In Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    if (getActivity() != null) getActivity().finish();
                } else if (resource.status == Resource.Status.ERROR) {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
