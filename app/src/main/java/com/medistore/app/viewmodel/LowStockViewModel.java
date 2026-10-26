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

public class LowStockViewModel extends AndroidViewModel {
    private final MedicineRepository repository;
    private final LiveData<List<Medicine>> lowStockMedicines;
    private final LiveData<List<Medicine>> outOfStockMedicines;
    private final LiveData<List<Medicine>> allMedicines;
    
    private final MutableLiveData<String> filter = new MutableLiveData<>("ALL");
    private final LiveData<List<Medicine>> displayedMedicines;

    public LowStockViewModel(@NonNull Application application) {
        super(application);
        repository = new MedicineRepository(application);

        lowStockMedicines = repository.getLowStockMedicines(); // Implementation assumed in repo
        outOfStockMedicines = repository.getOutOfStockMedicines(); // Implementation assumed in repo
        allMedicines = repository.getAllMedicines();

        displayedMedicines = Transformations.switchMap(filter, f -> {
            switch (f) {
                case "LOW": return lowStockMedicines;
                case "OUT": return outOfStockMedicines;
                default: return allMedicines;
            }
        });
    }

    public LiveData<List<Medicine>> getDisplayedMedicines() {
        return displayedMedicines;
    }
    
    public LiveData<List<Medicine>> getLowStockMedicines() {
        return lowStockMedicines;
    }
    
    public LiveData<List<Medicine>> getOutOfStockMedicines() {
        return outOfStockMedicines;
    }

    public void setFilter(String f) {
        filter.setValue(f);
    }
}
