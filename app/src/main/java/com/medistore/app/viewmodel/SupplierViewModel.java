package com.medistore.app.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.medistore.app.data.entities.Supplier;
import com.medistore.app.data.repository.SupplierRepository;
import java.util.List;

public class SupplierViewModel extends AndroidViewModel {
    private final SupplierRepository repository;
    private final LiveData<List<Supplier>> allSuppliers;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final LiveData<List<Supplier>> searchResults;

    public SupplierViewModel(@NonNull Application application) {
        super(application);
        repository = new SupplierRepository(application);
        allSuppliers = repository.getAllSuppliers();
        searchResults = Transformations.switchMap(searchQuery, query -> {
            if (query == null || query.trim().isEmpty()) {
                return allSuppliers;
            } else {
                return repository.searchSuppliers(query.trim());
            }
        });
    }

    public LiveData<List<Supplier>> getAllSuppliers() {
        return allSuppliers;
    }

    public LiveData<List<Supplier>> getSearchResults() {
        return searchResults;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void insert(Supplier supplier) {
        repository.insert(supplier);
    }

    public void update(Supplier supplier) {
        repository.update(supplier);
    }

    public void delete(Supplier supplier) {
        repository.delete(supplier);
    }
}
