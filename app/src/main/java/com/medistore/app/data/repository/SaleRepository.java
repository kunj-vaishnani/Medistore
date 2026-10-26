package com.medistore.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.medistore.app.data.dao.MedicineDao;
import com.medistore.app.data.dao.SaleDao;
import com.medistore.app.data.dao.SaleItemDao;
import com.medistore.app.data.database.AppDatabase;
import com.medistore.app.data.entities.Medicine;
import com.medistore.app.data.entities.Sale;
import com.medistore.app.data.entities.SaleItem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SaleRepository {

    private final SaleDao saleDao;
    private final SaleItemDao saleItemDao;
    private final MedicineDao medicineDao;
    private final ExecutorService executorService;

    public SaleRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        saleDao = db.saleDao();
        saleItemDao = db.saleItemDao();
        medicineDao = db.medicineDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Sale>> getAllSales() {
        return saleDao.getAllSales();
    }

    public LiveData<List<Sale>> getRecentSales(int limit) {
        return saleDao.getRecentSales(limit);
    }

    public LiveData<List<Sale>> searchSales(String query) {
        return saleDao.searchSales(query);
    }

    public LiveData<List<Sale>> getSalesByDate(String date) {
        return saleDao.getSalesByDate(date);
    }

    public void insertSaleWithItems(Sale sale, List<SaleItem> items) {
        executorService.execute(() -> {
            long saleId = saleDao.insertSale(sale);
            for (SaleItem item : items) {
                item.saleId = (int) saleId;
                saleItemDao.insertSaleItem(item);
                
                if (item.medicineId != null) {
                    Medicine medicine = medicineDao.getMedicineByIdSync(item.medicineId);
                    if (medicine != null) {
                        int newQuantity = medicine.quantity - item.quantity;
                        medicineDao.updateStock(item.medicineId, Math.max(0, newQuantity));
                    }
                }
            }
        });
    }

    public void deleteSale(Sale sale) {
        executorService.execute(() -> {
            List<SaleItem> items = saleItemDao.getItemsBySaleIdSync(sale.saleId);
            for (SaleItem item : items) {
                if (item.medicineId != null) {
                    Medicine medicine = medicineDao.getMedicineByIdSync(item.medicineId);
                    if (medicine != null) {
                        int newQuantity = medicine.quantity + item.quantity;
                        medicineDao.updateStock(item.medicineId, newQuantity);
                    }
                }
            }
            saleItemDao.deleteSaleItemsBySaleId(sale.saleId);
            saleDao.deleteSale(sale);
        });
    }

    public void getSaleById(int id, MedicineRepository.Callback<Sale> callback) {
        executorService.execute(() -> {
            Sale sale = saleDao.getSaleById(id);
            if (callback != null) callback.onResult(sale);
        });
    }

    public void getTodaySalesTotal(String today, MedicineRepository.Callback<Double> callback) {
        executorService.execute(() -> {
            double total = saleDao.getTodaySalesTotal(today);
            if (callback != null) callback.onResult(total);
        });
    }

    public void getTotalRevenue(MedicineRepository.Callback<Double> callback) {
        executorService.execute(() -> {
            double total = saleDao.getTotalRevenue();
            if (callback != null) callback.onResult(total);
        });
    }

    public void getMonthlySalesTotal(String monthYear, MedicineRepository.Callback<Double> callback) {
        executorService.execute(() -> {
            double total = saleDao.getMonthlySalesTotal(monthYear);
            if (callback != null) callback.onResult(total);
        });
    }

    public void getSaleCount(MedicineRepository.Callback<Integer> callback) {
        executorService.execute(() -> {
            int count = saleDao.getSaleCount();
            if (callback != null) callback.onResult(count);
        });
    }

    public LiveData<List<SaleItem>> getItemsBySaleId(int saleId) {
        return saleItemDao.getItemsBySaleId(saleId);
    }
}
