package com.medistore.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.medistore.app.data.dao.MedicineDao;
import com.medistore.app.data.database.AppDatabase;
import com.medistore.app.data.entities.Medicine;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MedicineRepository {

    private final MedicineDao medicineDao;
    private final ExecutorService executorService;

    public MedicineRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        medicineDao = db.medicineDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Medicine>> getAllMedicines() {
        return medicineDao.getAllMedicines();
    }

    public LiveData<List<Medicine>> searchMedicines(String query) {
        return medicineDao.searchMedicines(query);
    }

    public LiveData<List<Medicine>> getMedicinesByCategory(String category) {
        return medicineDao.getMedicinesByCategory(category);
    }

    public LiveData<List<Medicine>> getLowStockMedicines() {
        return medicineDao.getLowStockMedicines();
    }

    public LiveData<List<Medicine>> getOutOfStockMedicines() {
        return medicineDao.getOutOfStockMedicines();
    }

    public LiveData<List<Medicine>> getExpiringSoonMedicines(String today, String threshold) {
        return medicineDao.getExpiringSoonMedicines(today, threshold);
    }

    public LiveData<List<Medicine>> getExpiredMedicines(String today) {
        return medicineDao.getExpiredMedicines(today);
    }

    public List<Medicine> getAllMedicinesSync() {
        return medicineDao.getAllMedicinesSync();
    }

    public LiveData<Medicine> getMedicineById(int id) {
        return medicineDao.getMedicineById(id);
    }

    public void getMedicineById(int id, final Callback<Medicine> callback) {
        executorService.execute(() -> {
            Medicine medicine = medicineDao.getMedicineByIdSync(id);
            if (callback != null) callback.onResult(medicine);
        });
    }

    public void insert(Medicine medicine) {
        executorService.execute(() -> medicineDao.insertMedicine(medicine));
    }

    public void update(Medicine medicine) {
        executorService.execute(() -> medicineDao.updateMedicine(medicine));
    }

    public void delete(Medicine medicine) {
        executorService.execute(() -> medicineDao.deleteMedicine(medicine));
    }

    public void updateStock(int medicineId, int newQuantity) {
        executorService.execute(() -> medicineDao.updateStock(medicineId, newQuantity));
    }

    public void getMedicineCount(Callback<Integer> callback) {
        executorService.execute(() -> {
            int count = medicineDao.getMedicineCount();
            if (callback != null) callback.onResult(count);
        });
    }

    public void getTotalStock(Callback<Integer> callback) {
        executorService.execute(() -> {
            int total = medicineDao.getTotalStock();
            if (callback != null) callback.onResult(total);
        });
    }

    public void getLowStockCount(Callback<Integer> callback) {
        executorService.execute(() -> {
            int count = medicineDao.getLowStockCount();
            if (callback != null) callback.onResult(count);
        });
    }

    public void getExpiredCount(String today, Callback<Integer> callback) {
        executorService.execute(() -> {
            int count = medicineDao.getExpiredCount(today);
            if (callback != null) callback.onResult(count);
        });
    }

    public interface Callback<T> {
        void onResult(T result);
    }
}
