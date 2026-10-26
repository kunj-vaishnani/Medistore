package com.medistore.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.medistore.app.data.entities.Customer;

import java.util.List;

@Dao
public interface CustomerDao {

    @Query("SELECT * FROM customers ORDER BY name ASC")
    LiveData<List<Customer>> getAllCustomers();

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%' ORDER BY name ASC")
    LiveData<List<Customer>> searchCustomers(String query);

    @Query("SELECT * FROM customers WHERE customerId = :id")
    Customer getCustomerById(int id);

    @Query("SELECT * FROM customers ORDER BY name ASC")
    List<Customer> getAllCustomersSync();

    @Insert
    long insertCustomer(Customer customer);

    @Update
    void updateCustomer(Customer customer);

    @Delete
    void deleteCustomer(Customer customer);

    @Query("SELECT COUNT(*) FROM customers")
    int getCustomerCount();
}
