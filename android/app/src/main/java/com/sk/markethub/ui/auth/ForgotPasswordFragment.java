package com.sk.markethub.ui.auth;

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

public class ForgotPasswordFragment extends Fragment {

    private EditText etEmail, etNewPassword;
    private Button btnAction;
    private ProgressBar progressBar;
    private AuthViewModel viewModel;
    private boolean isResetMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etEmail = view.findViewById(R.id.etForgotEmail);
        etNewPassword = view.findViewById(R.id.etResetPassword);
        btnAction = view.findViewById(R.id.btnForgotPassword);
        progressBar = view.findViewById(R.id.pbForgotPassword);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        btnAction.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(getContext(), "Please enter registered email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isResetMode) {
                viewModel.forgotPassword(email).observe(getViewLifecycleOwner(), resource -> {
                    if (resource.status == Resource.Status.LOADING) {
                        progressBar.setVisibility(View.VISIBLE);
                        btnAction.setEnabled(false);
                    } else if (resource.status == Resource.Status.SUCCESS) {
                        progressBar.setVisibility(View.GONE);
                        btnAction.setEnabled(true);
                        isResetMode = true;
                        btnAction.setText("Reset Password Now");
                        Toast.makeText(getContext(), "Enter your new password below", Toast.LENGTH_LONG).show();
                    } else if (resource.status == Resource.Status.ERROR) {
                        progressBar.setVisibility(View.GONE);
                        btnAction.setEnabled(true);
                        Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                String newPassword = etNewPassword.getText().toString().trim();
                if (newPassword.length() < 8) {
                    Toast.makeText(getContext(), "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
                    return;
                }

                viewModel.resetPassword(email, newPassword).observe(getViewLifecycleOwner(), resource -> {
                    if (resource.status == Resource.Status.LOADING) {
                        progressBar.setVisibility(View.VISIBLE);
                        btnAction.setEnabled(false);
                    } else if (resource.status == Resource.Status.SUCCESS) {
                        progressBar.setVisibility(View.GONE);
                        btnAction.setEnabled(true);
                        Toast.makeText(getContext(), "Password reset successfully! Please sign in.", Toast.LENGTH_LONG).show();
                        if (getActivity() != null) getActivity().onBackPressed();
                    } else if (resource.status == Resource.Status.ERROR) {
                        progressBar.setVisibility(View.GONE);
                        btnAction.setEnabled(true);
                        Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
