package com.sk.markethub.ui.catalog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.sk.markethub.model.*;
import com.sk.markethub.repository.CatalogRepository;
import com.sk.markethub.repository.Resource;
import java.util.List;

public class CatalogViewModel extends ViewModel {

    private final CatalogRepository repository;

    public CatalogViewModel() {
        this.repository = new CatalogRepository();
    }

    public LiveData<Resource<List<Category>>> getCategories() {
        return repository.getCategories();
    }

    public LiveData<Resource<List<Brand>>> getBrands() {
        return repository.getBrands();
    }

    public LiveData<Resource<List<Product>>> getProducts(String search, Long categoryId, Long brandId) {
        return repository.getProducts(search, categoryId, brandId);
    }

    public LiveData<Resource<Product>> getProductDetails(Long productId) {
        return repository.getProductDetails(productId);
    }
}
