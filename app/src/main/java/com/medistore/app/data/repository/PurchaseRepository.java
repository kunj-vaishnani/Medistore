package com.medistore.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.medistore.app.data.dao.MedicineDao;
import com.medistore.app.data.dao.PurchaseDao;
import com.medistore.app.data.database.AppDatabase;
import com.medistore.app.data.entities.Medicine;
import com.medistore.app.data.entities.Purchase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PurchaseRepository {

    private final PurchaseDao purchaseDao;
    private final MedicineDao medicineDao;
    private final ExecutorService executorService;

    public PurchaseRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        purchaseDao = db.purchaseDao();
        medicineDao = db.medicineDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Purchase>> getAllPurchases() {
        return purchaseDao.getAllPurchases();
    }

    public LiveData<List<Purchase>> searchPurchases(String query) {
        return purchaseDao.searchPurchases(query);
    }

    public void insertPurchase(Purchase purchase) {
        executorService.execute(() -> {
            purchaseDao.insertPurchase(purchase);
            if (purchase.medicineId != null) {
                Medicine medicine = medicineDao.getMedicineByIdSync(purchase.medicineId);
                if (medicine != null) {
                    int newQuantity = medicine.quantity + purchase.quantity;
                    medicineDao.updateStock(purchase.medicineId, newQuantity);
                }
            }
        });
    }

    public void updatePurchase(Purchase purchase) {
        executorService.execute(() -> purchaseDao.updatePurchase(purchase));
    }

    public void deletePurchase(Purchase purchase) {
        executorService.execute(() -> purchaseDao.deletePurchase(purchase));
    }
}
