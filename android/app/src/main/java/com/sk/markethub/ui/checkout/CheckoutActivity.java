package com.sk.markethub.ui.checkout;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.sk.markethub.R;
import com.sk.markethub.api.ApiClient;
import com.sk.markethub.model.Address;
import com.sk.markethub.model.Order;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    private EditText etAddressLine, etCoupon;
    private Button btnPlaceOrder;
    private Long selectedAddressId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        etAddressLine = findViewById(R.id.etAddressLine);
        etCoupon = findViewById(R.id.etCheckoutCoupon);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        fetchOrCreateAddress();

        btnPlaceOrder.setOnClickListener(v -> {
            if (selectedAddressId == null) {
                Toast.makeText(this, "Valid delivery address required", Toast.LENGTH_SHORT).show();
                return;
            }

            String coupon = etCoupon.getText().toString().trim();
            Map<String, Object> body = new HashMap<>();
            body.put("addressId", selectedAddressId);
            if (!coupon.isEmpty()) body.put("couponCode", coupon);

            ApiClient.getService().checkout(body).enqueue(new Callback<Order>() {
                @Override
                public void onResponse(Call<Order> call, Response<Order> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Order order = response.body();
                        payOrderSandbox(order.getOrderId());
                    } else {
                        Toast.makeText(CheckoutActivity.this, "Checkout failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Order> call, Throwable t) {
                    Toast.makeText(CheckoutActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void fetchOrCreateAddress() {
        ApiClient.getService().getAddresses().enqueue(new Callback<List<Address>>() {
            @Override
            public void onResponse(Call<List<Address>> call, Response<List<Address>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Address addr = response.body().get(0);
                    selectedAddressId = addr.getId();
                    etAddressLine.setText(addr.getFullName() + ", " + addr.getAddressLine1() + ", " + addr.getCity());
                } else {
                    createNewAddress();
                }
            }

            @Override
            public void onFailure(Call<List<Address>> call, Throwable t) {
                createNewAddress();
            }
        });
    }

    private void createNewAddress() {
        Address newAddr = new Address();
        newAddr.setFullName("Customer");
        newAddr.setMobile("9876543210");
        newAddr.setAddressLine1("123 Main Street");
        newAddr.setCity("Bangalore");
        newAddr.setState("Karnataka");
        newAddr.setPincode("560001");
        newAddr.setIsDefault(true);

        ApiClient.getService().createAddress(newAddr).enqueue(new Callback<Address>() {
            @Override
            public void onResponse(Call<Address> call, Response<Address> response) {
                if (response.isSuccessful() && response.body() != null) {
                    selectedAddressId = response.body().getId();
                    etAddressLine.setText("Customer, 123 Main Street, Bangalore");
                }
            }

            @Override
            public void onFailure(Call<Address> call, Throwable t) {}
        });
    }

    private void payOrderSandbox(Long orderId) {
        Map<String, Object> body = new HashMap<>();
        body.put("paymentMethod", "UPI");

        ApiClient.getService().payOrder(orderId, body).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CheckoutActivity.this, "Order Placed & Paid Successfully (Sandbox)!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Toast.makeText(CheckoutActivity.this, "Order Placed! Payment Pending.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
