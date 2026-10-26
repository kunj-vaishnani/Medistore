package com.medistore.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.medistore.app.data.dao.CustomerDao;
import com.medistore.app.data.database.AppDatabase;
import com.medistore.app.data.entities.Customer;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomerRepository {

    private final CustomerDao customerDao;
    private final ExecutorService executorService;

    public CustomerRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        customerDao = db.customerDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Customer>> getAllCustomers() {
        return customerDao.getAllCustomers();
    }

    public LiveData<List<Customer>> searchCustomers(String query) {
        return customerDao.searchCustomers(query);
    }

    public List<Customer> getAllCustomersSync() {
        return customerDao.getAllCustomersSync();
    }

    public void getCustomerById(int id, MedicineRepository.Callback<Customer> callback) {
        executorService.execute(() -> {
            Customer customer = customerDao.getCustomerById(id);
            if (callback != null) callback.onResult(customer);
        });
    }

    public void insert(Customer customer) {
        executorService.execute(() -> customerDao.insertCustomer(customer));
    }

    public void update(Customer customer) {
        executorService.execute(() -> customerDao.updateCustomer(customer));
    }

    public void delete(Customer customer) {
        executorService.execute(() -> customerDao.deleteCustomer(customer));
    }

    public void getCount(MedicineRepository.Callback<Integer> callback) {
        executorService.execute(() -> {
            int count = customerDao.getCustomerCount();
            if (callback != null) callback.onResult(count);
        });
    }
}
