package com.sk.markethub.ui.catalog;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.sk.markethub.R;
import com.sk.markethub.api.ApiClient;
import com.sk.markethub.model.CartSummary;
import com.sk.markethub.model.Product;
import com.sk.markethub.repository.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvSku, tvName, tvPrice, tvDescription;
    private Button btnAddToCart;
    private CatalogViewModel viewModel;
    private Long productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productId = getIntent().getLongExtra("productId", -1);
        if (productId == -1) {
            finish();
            return;
        }

        tvSku = findViewById(R.id.tvDetailSku);
        tvName = findViewById(R.id.tvDetailName);
        tvPrice = findViewById(R.id.tvDetailPrice);
        tvDescription = findViewById(R.id.tvDetailDescription);
        btnAddToCart = findViewById(R.id.btnDetailAddToCart);

        viewModel = new ViewModelProvider(this).get(CatalogViewModel.class);

        viewModel.getProductDetails(productId).observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                Product p = resource.data;
                tvSku.setText("SKU: " + p.getSku());
                tvName.setText(p.getName());

                BigDecimal price = p.getSalePrice() != null ? p.getSalePrice() : p.getBasePrice();
                tvPrice.setText("₹" + (price != null ? price.toString() : "0"));
                tvDescription.setText(p.getDescription() != null ? p.getDescription() : "No description available.");
            }
        });

        btnAddToCart.setOnClickListener(v -> {
            Map<String, Object> body = new HashMap<>();
            body.put("productId", productId);
            body.put("quantity", 1);

            ApiClient.getService().addCartItem(body).enqueue(new Callback<CartSummary>() {
                @Override
                public void onResponse(Call<CartSummary> call, Response<CartSummary> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ProductDetailActivity.this, "Added to Cart!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProductDetailActivity.this, "Please sign in first", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CartSummary> call, Throwable t) {
                    Toast.makeText(ProductDetailActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
