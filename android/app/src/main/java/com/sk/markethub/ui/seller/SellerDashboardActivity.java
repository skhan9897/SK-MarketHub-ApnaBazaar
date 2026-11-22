package com.sk.markethub.ui.seller;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sk.markethub.R;
import com.sk.markethub.api.ApiClient;
import com.sk.markethub.model.Product;
import com.sk.markethub.ui.catalog.ProductAdapter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerDashboardActivity extends AppCompatActivity {

    private TextView tvRevenue;
    private Button btnAddProduct;
    private RecyclerView rvProducts;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_dashboard);

        tvRevenue = findViewById(R.id.tvSellerRevenue);
        btnAddProduct = findViewById(R.id.btnSellerAddProduct);
        rvProducts = findViewById(R.id.rvSellerProducts);

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(null);
        rvProducts.setAdapter(adapter);

        btnAddProduct.setOnClickListener(v -> createQuickSellerProduct());

        loadAnalytics();
        loadSellerProducts();
    }

    private void loadAnalytics() {
        ApiClient.getService().getSellerAnalytics().enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Object rev = response.body().get("totalRevenue");
                    tvRevenue.setText("Total Store Revenue: ₹" + (rev != null ? rev.toString() : "0"));
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {}
        });
    }

    private void loadSellerProducts() {
        ApiClient.getService().getSellerProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setProducts(response.body());
                } else {
                    Toast.makeText(SellerDashboardActivity.this, "Seller account pending approval or not found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(SellerDashboardActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createQuickSellerProduct() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Seller Product " + System.currentTimeMillis() % 1000);
        body.put("sku", "SP-" + System.currentTimeMillis() % 10000);
        body.put("categoryId", 1);
        body.put("basePrice", 1499);
        body.put("salePrice", 1299);
        body.put("description", "Premium seller store product.");
        body.put("initialStock", 25);

        ApiClient.getService().createSellerProduct(body).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SellerDashboardActivity.this, "New Seller Product Created!", Toast.LENGTH_SHORT).show();
                    loadSellerProducts();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {}
        });
    }
}
