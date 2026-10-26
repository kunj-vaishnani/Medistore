package com.medistore.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.medistore.app.data.entities.Medicine;
import com.medistore.app.data.entities.Supplier;
import com.medistore.app.data.repository.MedicineRepository;
import com.medistore.app.data.repository.SupplierRepository;

import java.util.List;

public class MedicineViewModel extends AndroidViewModel {
    private final MedicineRepository repository;
    private final SupplierRepository supplierRepository;
    private final LiveData<List<Medicine>> allMedicines;
    private final LiveData<List<Supplier>> allSuppliers;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<String> categoryFilter = new MutableLiveData<>("All");
    
    private final LiveData<List<Medicine>> filteredMedicines;

    public MedicineViewModel(@NonNull Application application) {
        super(application);
        repository = new MedicineRepository(application);
        supplierRepository = new SupplierRepository(application);
        allMedicines = repository.getAllMedicines();
        allSuppliers = supplierRepository.getAllSuppliers();

        // SwitchMap for search and category filtering
        LiveData<List<Medicine>> categoryFiltered = Transformations.switchMap(categoryFilter, category -> {
            if (category == null || category.equals("All")) {
                return allMedicines;
            } else {
                return repository.getMedicinesByCategory(category);
            }
        });

        filteredMedicines = Transformations.switchMap(searchQuery, query -> {
            if (query == null || query.isEmpty()) {
                return categoryFiltered;
            } else {
                return repository.searchMedicines("%" + query + "%");
            }
        });
    }

    public LiveData<List<Medicine>> getAllMedicines() {
        return allMedicines;
    }

    public LiveData<List<Medicine>> getFilteredMedicines() {
        return filteredMedicines;
    }
    
    public LiveData<List<Supplier>> getAllSuppliers() {
        return allSuppliers;
    }
    
    public LiveData<Medicine> getMedicineById(int id) {
        return repository.getMedicineById(id);
    }

    public void insert(Medicine medicine) {
        repository.insert(medicine);
    }

    public void update(Medicine medicine) {
        repository.update(medicine);
    }

    public void delete(Medicine medicine) {
        repository.delete(medicine);
    }

    public void updateStock(int id, int qty) {
        repository.updateStock(id, qty);
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void filterByCategory(String category) {
        categoryFilter.setValue(category);
    }
}
