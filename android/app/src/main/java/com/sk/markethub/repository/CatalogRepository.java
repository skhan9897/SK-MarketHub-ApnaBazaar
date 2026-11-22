package com.sk.markethub.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.sk.markethub.api.ApiClient;
import com.sk.markethub.model.*;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatalogRepository {

    public LiveData<Resource<List<Category>>> getCategories() {
        MutableLiveData<Resource<List<Category>>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());
        ApiClient.getService().getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    data.setValue(Resource.error("Failed to load categories"));
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                data.setValue(Resource.error("Network error"));
            }
        });
        return data;
    }

    public LiveData<Resource<List<Brand>>> getBrands() {
        MutableLiveData<Resource<List<Brand>>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());
        ApiClient.getService().getBrands().enqueue(new Callback<List<Brand>>() {
            @Override
            public void onResponse(Call<List<Brand>> call, Response<List<Brand>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    data.setValue(Resource.error("Failed to load brands"));
                }
            }

            @Override
            public void onFailure(Call<List<Brand>> call, Throwable t) {
                data.setValue(Resource.error("Network error"));
            }
        });
        return data;
    }

    public LiveData<Resource<List<Product>>> getProducts(String search, Long categoryId, Long brandId) {
        MutableLiveData<Resource<List<Product>>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());
        ApiClient.getService().getProducts(search, categoryId, brandId).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    data.setValue(Resource.error("Failed to load products"));
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                data.setValue(Resource.error("Network error"));
            }
        });
        return data;
    }

    public LiveData<Resource<Product>> getProductDetails(Long productId) {
        MutableLiveData<Resource<Product>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());
        ApiClient.getService().getProductDetails(productId).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    data.setValue(Resource.error("Product not found"));
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                data.setValue(Resource.error("Network error"));
            }
        });
        return data;
    }
}
