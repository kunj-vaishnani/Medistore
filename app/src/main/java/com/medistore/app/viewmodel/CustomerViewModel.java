package com.medistore.app.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.medistore.app.data.entities.Customer;
import com.medistore.app.data.repository.CustomerRepository;
import java.util.List;

public class CustomerViewModel extends AndroidViewModel {
    private final CustomerRepository repository;
    private final LiveData<List<Customer>> allCustomers;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final LiveData<List<Customer>> searchResults;

    public CustomerViewModel(@NonNull Application application) {
        super(application);
        repository = new CustomerRepository(application);
        allCustomers = repository.getAllCustomers();
        searchResults = Transformations.switchMap(searchQuery, query -> {
            if (query == null || query.trim().isEmpty()) {
                return allCustomers;
            } else {
                return repository.searchCustomers(query.trim());
            }
        });
    }

    public LiveData<List<Customer>> getAllCustomers() {
        return allCustomers;
    }

    public LiveData<List<Customer>> getSearchResults() {
        return searchResults;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void insert(Customer customer) {
        repository.insert(customer);
    }

    public void update(Customer customer) {
        repository.update(customer);
    }

    public void delete(Customer customer) {
        repository.delete(customer);
    }
}
