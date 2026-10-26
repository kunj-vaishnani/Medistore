package com.medistore.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.medistore.app.data.entities.Medicine;
import com.medistore.app.data.repository.MedicineRepository;

import java.util.List;

public class InventoryViewModel extends AndroidViewModel {
    private final MedicineRepository repository;
    private final LiveData<List<Medicine>> allMedicines;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final LiveData<List<Medicine>> searchResults;

    public InventoryViewModel(@NonNull Application application) {
        super(application);
        repository = new MedicineRepository(application);
        allMedicines = repository.getAllMedicines();

        searchResults = Transformations.switchMap(searchQuery, query -> {
            if (query == null || query.isEmpty()) {
                return allMedicines;
            } else {
                return repository.searchMedicines("%" + query + "%");
            }
        });
    }

    public LiveData<List<Medicine>> getAllMedicines() {
        return searchResults;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }
}
