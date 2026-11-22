package com.sk.markethub.ui.portals;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sk.markethub.R;
import com.sk.markethub.ui.admin.AdminActivity;
import com.sk.markethub.ui.auth.AuthActivity;
import com.sk.markethub.ui.seller.SellerDashboardActivity;
import com.sk.markethub.ui.support.SupportActivity;
import com.sk.markethub.util.TokenManager;

public class PortalsFragment extends Fragment {

    private TextView tvUserInfo;
    private Button btnSeller, btnAdmin, btnSupport, btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_portals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvUserInfo = view.findViewById(R.id.tvUserInfo);
        btnSeller = view.findViewById(R.id.btnPortalSeller);
        btnAdmin = view.findViewById(R.id.btnPortalAdmin);
        btnSupport = view.findViewById(R.id.btnPortalSupport);
        btnLogout = view.findViewById(R.id.btnPortalLogout);

        String name = TokenManager.getUserName();
        String email = TokenManager.getUserEmail();
        if (!email.isEmpty()) {
            tvUserInfo.setText("Signed in as: " + name + " (" + email + ")");
        } else {
            tvUserInfo.setText("Not signed in");
        }

        btnSeller.setOnClickListener(v -> startActivity(new Intent(getActivity(), SellerDashboardActivity.class)));
        btnAdmin.setOnClickListener(v -> startActivity(new Intent(getActivity(), AdminActivity.class)));
        btnSupport.setOnClickListener(v -> startActivity(new Intent(getActivity(), SupportActivity.class)));

        btnLogout.setOnClickListener(v -> {
            TokenManager.clear();
            Toast.makeText(getContext(), "Signed out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), AuthActivity.class));
            if (getActivity() != null) getActivity().finish();
        });
    }
}
