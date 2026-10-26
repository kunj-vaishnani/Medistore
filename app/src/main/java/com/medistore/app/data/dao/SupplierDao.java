package com.medistore.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.medistore.app.data.entities.Supplier;

import java.util.List;

@Dao
public interface SupplierDao {

    @Query("SELECT * FROM suppliers ORDER BY name ASC")
    LiveData<List<Supplier>> getAllSuppliers();

    @Query("SELECT * FROM suppliers WHERE name LIKE '%' || :query || '%' OR company LIKE '%' || :query || '%' ORDER BY name ASC")
    LiveData<List<Supplier>> searchSuppliers(String query);

    @Query("SELECT * FROM suppliers WHERE supplierId = :id")
    Supplier getSupplierById(int id);

    @Query("SELECT * FROM suppliers ORDER BY name ASC")
    List<Supplier> getAllSuppliersSync();

    @Insert
    long insertSupplier(Supplier supplier);

    @Update
    void updateSupplier(Supplier supplier);

    @Delete
    void deleteSupplier(Supplier supplier);

    @Query("SELECT COUNT(*) FROM suppliers")
    int getSupplierCount();
}
