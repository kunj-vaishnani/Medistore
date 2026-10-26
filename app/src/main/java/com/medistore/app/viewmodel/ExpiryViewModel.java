package com.medistore.app.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.medistore.app.data.entities.Medicine;
import com.medistore.app.data.repository.MedicineRepository;
import com.medistore.app.utils.DateUtils;
import java.util.List;

public class ExpiryViewModel extends AndroidViewModel {
    private final MedicineRepository repository;
    private final LiveData<List<Medicine>> expiredMedicines;
    private final LiveData<List<Medicine>> expiringSoon7;
    private final LiveData<List<Medicine>> expiringSoon30;
    private final LiveData<List<Medicine>> allMedicines;
    
    private final MutableLiveData<String> selectedFilter = new MutableLiveData<>("ALL");
    private final LiveData<List<Medicine>> displayedMedicines;

    public ExpiryViewModel(@NonNull Application application) {
        super(application);
        repository = new MedicineRepository(application);
        String today = DateUtils.getTodayDate();
        String threshold7 = DateUtils.getThresholdDate(7);
        String threshold30 = DateUtils.getThresholdDate(30);

        expiredMedicines = repository.getExpiredMedicines(today);
        expiringSoon7 = repository.getExpiringSoonMedicines(today, threshold7);
        expiringSoon30 = repository.getExpiringSoonMedicines(today, threshold30);
        allMedicines = repository.getAllMedicines();

        displayedMedicines = Transformations.switchMap(selectedFilter, filter -> {
            switch (filter) {
                case "EXPIRED":
                    return expiredMedicines;
                case "SOON_7":
                    return expiringSoon7;
                case "SOON_30":
                    return expiringSoon30;
                default:
                    return allMedicines; // Or all expiring soon
            }
        });
    }

    public LiveData<List<Medicine>> getDisplayedMedicines() {
        return displayedMedicines;
    }

    public LiveData<List<Medicine>> getExpiredMedicines() {
        return expiredMedicines;
    }

    public void setFilter(String filter) {
        selectedFilter.setValue(filter);
    }
}
