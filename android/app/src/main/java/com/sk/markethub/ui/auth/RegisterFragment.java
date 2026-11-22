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

    private EditText etFirstName, etLastName, etEmail, etMobile, etPassword;
    private Button btnRegister;
    private ProgressBar progressBar;
    private AuthViewModel viewModel;

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
        etMobile = view.findViewById(R.id.etRegMobile);
        etPassword = view.findViewById(R.id.etRegPassword);
        btnRegister = view.findViewById(R.id.btnRegister);
        progressBar = view.findViewById(R.id.pbRegister);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        btnRegister.setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
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

            viewModel.register(email, password, firstName, lastName, mobile).observe(getViewLifecycleOwner(), resource -> {
                if (resource.status == Resource.Status.LOADING) {
                    progressBar.setVisibility(View.VISIBLE);
                    btnRegister.setEnabled(false);
                } else if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
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
        });
    }
}
