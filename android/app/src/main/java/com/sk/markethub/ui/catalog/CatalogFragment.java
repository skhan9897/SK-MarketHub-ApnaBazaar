package com.sk.markethub.ui.catalog;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sk.markethub.R;
import com.sk.markethub.api.ApiClient;
import com.sk.markethub.model.CartSummary;
import com.sk.markethub.model.Product;
import com.sk.markethub.repository.Resource;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatalogFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private EditText etSearch;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvProducts;
    private ProductAdapter adapter;
    private CatalogViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearch = view.findViewById(R.id.etCatalogSearch);
        swipeRefresh = view.findViewById(R.id.swipeCatalog);
        rvProducts = view.findViewById(R.id.rvProducts);

        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ProductAdapter(this);
        rvProducts.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(CatalogViewModel.class);

        loadProducts(null);

        swipeRefresh.setOnRefreshListener(() -> loadProducts(etSearch.getText().toString().trim()));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadProducts(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadProducts(String search) {
        viewModel.getProducts(search, null, null).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.LOADING) {
                swipeRefresh.setRefreshing(true);
            } else if (resource.status == Resource.Status.SUCCESS) {
                swipeRefresh.setRefreshing(false);
                adapter.setProducts(resource.data);
            } else if (resource.status == Resource.Status.ERROR) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
        intent.putExtra("productId", product.getId());
        startActivity(intent);
    }

    @Override
    public void onAddToCartClick(Product product) {
        Map<String, Object> body = new HashMap<>();
        body.put("productId", product.getId());
        body.put("quantity", 1);

        ApiClient.getService().addCartItem(body).enqueue(new Callback<CartSummary>() {
            @Override
            public void onResponse(Call<CartSummary> call, Response<CartSummary> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Added to Cart!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Please sign in to add to cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartSummary> call, Throwable t) {
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
