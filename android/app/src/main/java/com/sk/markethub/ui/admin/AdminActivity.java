package com.sk.markethub.ui.admin;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.sk.markethub.R;
import com.sk.markethub.api.ApiClient;
import com.sk.markethub.model.PendingSeller;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends AppCompatActivity {

    private TextView tvMetrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        tvMetrics = findViewById(R.id.tvAdminMetrics);

        loadMetrics();
        loadPendingSellers();
    }

    private void loadMetrics() {
        ApiClient.getService().getAdminMetrics().enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> m = response.body();
                    tvMetrics.setText("Users: " + m.get("totalCustomers") + " | Sellers: " + m.get("totalSellers") + " | Orders: " + m.get("totalOrders") + " | Revenue: ₹" + m.get("totalRevenue"));
                } else {
                    tvMetrics.setText("ADMIN Role Authorization Required");
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                tvMetrics.setText("Network error loading metrics");
            }
        });
    }

    private void loadPendingSellers() {
        ApiClient.getService().getPendingSellers().enqueue(new Callback<List<PendingSeller>>() {
            @Override
            public void onResponse(Call<List<PendingSeller>> call, Response<List<PendingSeller>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PendingSeller> sellers = response.body();
                    if (!sellers.isEmpty()) {
                        PendingSeller p = sellers.get(0);
                        Toast.makeText(AdminActivity.this, "Pending Seller Application: " + p.getStoreName() + " (" + p.getEmail() + ")", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PendingSeller>> call, Throwable t) {}
        });
    }
}
