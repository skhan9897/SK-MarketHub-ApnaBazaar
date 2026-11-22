package com.sk.markethub.ui.cart;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sk.markethub.R;
import com.sk.markethub.api.ApiClient;
import com.sk.markethub.model.CartItemDetail;
import com.sk.markethub.model.CartSummary;
import com.sk.markethub.ui.checkout.CheckoutActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment implements CartAdapter.OnCartItemActionListener {

    private RecyclerView rvCart;
    private TextView tvSubtotal;
    private Button btnCheckout;
    private CartAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCart = view.findViewById(R.id.rvCartItems);
        tvSubtotal = view.findViewById(R.id.tvCartSubtotal);
        btnCheckout = view.findViewById(R.id.btnProceedCheckout);

        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartAdapter(this);
        rvCart.setAdapter(adapter);

        btnCheckout.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), CheckoutActivity.class));
        });

        loadCart();
    }

    private void loadCart() {
        ApiClient.getService().getCart().enqueue(new Callback<CartSummary>() {
            @Override
            public void onResponse(Call<CartSummary> call, Response<CartSummary> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CartSummary summary = response.body();
                    adapter.setItems(summary.getItems());
                    tvSubtotal.setText("Subtotal: ₹" + (summary.getSubtotal() != null ? summary.getSubtotal().toString() : "0"));
                    btnCheckout.setEnabled(summary.getItemCount() != null && summary.getItemCount() > 0);
                } else {
                    tvSubtotal.setText("Cart is empty");
                    btnCheckout.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<CartSummary> call, Throwable t) {
                Toast.makeText(getContext(), "Network failure loading cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRemoveItem(CartItemDetail item) {
        ApiClient.getService().removeCartItem(item.getItemId()).enqueue(new Callback<CartSummary>() {
            @Override
            public void onResponse(Call<CartSummary> call, Response<CartSummary> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CartSummary summary = response.body();
                    adapter.setItems(summary.getItems());
                    tvSubtotal.setText("Subtotal: ₹" + (summary.getSubtotal() != null ? summary.getSubtotal().toString() : "0"));
                    Toast.makeText(getContext(), "Item removed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartSummary> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to remove item", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
