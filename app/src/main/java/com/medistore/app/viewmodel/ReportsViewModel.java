package com.medistore.app.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.medistore.app.data.repository.CustomerRepository;
import com.medistore.app.data.repository.MedicineRepository;
import com.medistore.app.data.repository.SaleRepository;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReportsViewModel extends AndroidViewModel {
    private final MedicineRepository medicineRepository;
    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;
    
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public final MutableLiveData<Integer> totalMedicines = new MutableLiveData<>(0);
    public final MutableLiveData<Integer> totalStock = new MutableLiveData<>(0);
    public final MutableLiveData<Integer> lowStockCount = new MutableLiveData<>(0);
    public final MutableLiveData<Integer> expiredCount = new MutableLiveData<>(0);

    public final MutableLiveData<Double> todaySales = new MutableLiveData<>(0.0);
    public final MutableLiveData<Double> weeklySales = new MutableLiveData<>(0.0);
    public final MutableLiveData<Double> monthlySales = new MutableLiveData<>(0.0);
    public final MutableLiveData<Double> totalRevenue = new MutableLiveData<>(0.0);

    public final MutableLiveData<Integer> totalCustomers = new MutableLiveData<>(0);
    public final MutableLiveData<Integer> totalSales = new MutableLiveData<>(0);

    public ReportsViewModel(@NonNull Application application) {
        super(application);
        medicineRepository = new MedicineRepository(application);
        saleRepository = new SaleRepository(application);
        customerRepository = new CustomerRepository(application);
    }

    public void refreshAll() {
        executor.execute(() -> {
            // Mock fetching data from repositories
            int mockTotalMedicines = 100; // medicineRepository.getTotalMedicinesCount();
            int mockTotalStock = 500; // medicineRepository.getTotalStockCount();
            int mockLowStock = 10; // medicineRepository.getLowStockCount();
            int mockExpired = 5; // medicineRepository.getExpiredCount();
            
            double mockToday = 1500.0; // saleRepository.getTodaySales();
            double mockWeekly = 10500.0; // saleRepository.getWeeklySales();
            double mockMonthly = 45000.0; // saleRepository.getMonthlySalesTotal("05-2024");
            double mockTotalRev = 150000.0; // saleRepository.getTotalRevenue();
            
            int mockCustomers = 50; // customerRepository.getTotalCustomersCount();
            int mockSales = 200; // saleRepository.getTotalSalesCount();

            totalMedicines.postValue(mockTotalMedicines);
            totalStock.postValue(mockTotalStock);
            lowStockCount.postValue(mockLowStock);
            expiredCount.postValue(mockExpired);
            
            todaySales.postValue(mockToday);
            weeklySales.postValue(mockWeekly);
            monthlySales.postValue(mockMonthly);
            totalRevenue.postValue(mockTotalRev);
            
            totalCustomers.postValue(mockCustomers);
            totalSales.postValue(mockSales);
        });
    }
}
