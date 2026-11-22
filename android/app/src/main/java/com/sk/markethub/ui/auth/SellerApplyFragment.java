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
import com.sk.markethub.util.TokenManager;

public class SellerApplyFragment extends Fragment {

    private EditText etStoreName, etBusinessName, etGstNumber;
    private Button btnApply;
    private ProgressBar progressBar;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seller_apply, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etStoreName = view.findViewById(R.id.etSellerStoreName);
        etBusinessName = view.findViewById(R.id.etSellerBusinessName);
        etGstNumber = view.findViewById(R.id.etSellerGstNumber);
        btnApply = view.findViewById(R.id.btnApplySeller);
        progressBar = view.findViewById(R.id.pbApplySeller);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        btnApply.setOnClickListener(v -> {
            String storeName = etStoreName.getText().toString().trim();
            String businessName = etBusinessName.getText().toString().trim();
            String gstNumber = etGstNumber.getText().toString().trim();

            if (storeName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter store name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TokenManager.getToken() == null) {
                Toast.makeText(getContext(), "Please sign in first to apply as seller", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.submitSellerApplication(storeName, businessName, gstNumber).observe(getViewLifecycleOwner(), resource -> {
                if (resource.status == Resource.Status.LOADING) {
                    progressBar.setVisibility(View.VISIBLE);
                    btnApply.setEnabled(false);
                } else if (resource.status == Resource.Status.SUCCESS) {
                    progressBar.setVisibility(View.GONE);
                    btnApply.setEnabled(true);
                    Toast.makeText(getContext(), "Seller Application Submitted! Pending Admin Approval.", Toast.LENGTH_LONG).show();
                    etStoreName.setText("");
                    etBusinessName.setText("");
                    etGstNumber.setText("");
                } else if (resource.status == Resource.Status.ERROR) {
                    progressBar.setVisibility(View.GONE);
                    btnApply.setEnabled(true);
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
