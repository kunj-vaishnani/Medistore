package com.medistore.app.ui.dashboard;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.medistore.app.data.entities.Sale;
import com.medistore.app.data.repository.CustomerRepository;
import com.medistore.app.data.repository.MedicineRepository;
import com.medistore.app.data.repository.SaleRepository;
import com.medistore.app.utils.CurrencyUtils;
import com.medistore.app.utils.DateUtils;

import java.util.List;

public class DashboardViewModel extends AndroidViewModel {

    private final MedicineRepository medicineRepository;
    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;

    private final MutableLiveData<Integer> totalMedicines = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> totalStock = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> lowStockCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> expiredCount = new MutableLiveData<>(0);
    private final MutableLiveData<String> todaySales = new MutableLiveData<>("₹0");
    private final MutableLiveData<Integer> totalCustomers = new MutableLiveData<>(0);

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        medicineRepository = new MedicineRepository(application);
        saleRepository = new SaleRepository(application);
        customerRepository = new CustomerRepository(application);
        refreshStats();
    }

    public LiveData<Integer> getTotalMedicines() { return totalMedicines; }
    public LiveData<Integer> getTotalStock() { return totalStock; }
    public LiveData<Integer> getLowStockCount() { return lowStockCount; }
    public LiveData<Integer> getExpiredCount() { return expiredCount; }
    public LiveData<String> getTodaySales() { return todaySales; }
    public LiveData<Integer> getTotalCustomers() { return totalCustomers; }
    public LiveData<List<Sale>> getRecentSales() { return saleRepository.getRecentSales(5); }

    public void refreshStats() {
        medicineRepository.getMedicineCount(totalMedicines::postValue);
        medicineRepository.getTotalStock(totalStock::postValue);
        medicineRepository.getLowStockCount(lowStockCount::postValue);
        medicineRepository.getExpiredCount(DateUtils.getTodayDate(), expiredCount::postValue);
        saleRepository.getTodaySalesTotal(DateUtils.getTodayDate(), amount -> {
            todaySales.postValue(CurrencyUtils.formatCurrency(amount != null ? amount : 0.0));
        });
        customerRepository.getCount(totalCustomers::postValue);
    }
}
