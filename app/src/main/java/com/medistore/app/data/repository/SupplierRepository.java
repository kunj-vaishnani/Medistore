package com.medistore.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.medistore.app.data.dao.SupplierDao;
import com.medistore.app.data.database.AppDatabase;
import com.medistore.app.data.entities.Supplier;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SupplierRepository {

    private final SupplierDao supplierDao;
    private final ExecutorService executorService;

    public SupplierRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        supplierDao = db.supplierDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Supplier>> getAllSuppliers() {
        return supplierDao.getAllSuppliers();
    }

    public LiveData<List<Supplier>> searchSuppliers(String query) {
        return supplierDao.searchSuppliers(query);
    }

    public List<Supplier> getAllSuppliersSync() {
        return supplierDao.getAllSuppliersSync();
    }

    public void getSupplierById(int id, MedicineRepository.Callback<Supplier> callback) {
        executorService.execute(() -> {
            Supplier supplier = supplierDao.getSupplierById(id);
            if (callback != null) callback.onResult(supplier);
        });
    }

    public void insert(Supplier supplier) {
        executorService.execute(() -> supplierDao.insertSupplier(supplier));
    }

    public void update(Supplier supplier) {
        executorService.execute(() -> supplierDao.updateSupplier(supplier));
    }

    public void delete(Supplier supplier) {
        executorService.execute(() -> supplierDao.deleteSupplier(supplier));
    }

    public void getCount(MedicineRepository.Callback<Integer> callback) {
        executorService.execute(() -> {
            int count = supplierDao.getSupplierCount();
            if (callback != null) callback.onResult(count);
        });
    }
}
