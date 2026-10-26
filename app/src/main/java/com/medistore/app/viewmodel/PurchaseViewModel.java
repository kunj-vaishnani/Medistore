package com.medistore.app.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.medistore.app.data.entities.Medicine;
import com.medistore.app.data.entities.Purchase;
import com.medistore.app.data.entities.Supplier;
import com.medistore.app.data.repository.MedicineRepository;
import com.medistore.app.data.repository.PurchaseRepository;
import com.medistore.app.data.repository.SupplierRepository;

import java.util.List;

public class PurchaseViewModel extends AndroidViewModel {
    private final PurchaseRepository purchaseRepo;
    private final MedicineRepository medicineRepo;
    private final SupplierRepository supplierRepo;
    
    private final LiveData<List<Purchase>> allPurchases;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final LiveData<List<Purchase>> filteredPurchases;

    private final LiveData<List<Medicine>> allMedicines;
    private final LiveData<List<Supplier>> allSuppliers;

    public PurchaseViewModel(Application application) {
        super(application);
        purchaseRepo = new PurchaseRepository(application);
        medicineRepo = new MedicineRepository(application);
        supplierRepo = new SupplierRepository(application);

        allPurchases = purchaseRepo.getAllPurchases();
        allMedicines = medicineRepo.getAllMedicines();
        allSuppliers = supplierRepo.getAllSuppliers();

        filteredPurchases = Transformations.switchMap(searchQuery, query -> {
            if (query == null || query.trim().isEmpty()) {
                return allPurchases;
            } else {
                return purchaseRepo.searchPurchases(query.trim());
            }
        });
    }

    public void insert(Purchase purchase) {
        purchaseRepo.insertPurchase(purchase);
    }

    public void update(Purchase purchase) {
        purchaseRepo.updatePurchase(purchase);
    }

    public void delete(Purchase purchase) {
        purchaseRepo.deletePurchase(purchase);
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public LiveData<List<Purchase>> getFilteredPurchases() {
        return filteredPurchases;
    }

    public LiveData<List<Medicine>> getAllMedicines() {
        return allMedicines;
    }

    public LiveData<List<Supplier>> getAllSuppliers() {
        return allSuppliers;
    }
}
