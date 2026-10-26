package com.medistore.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.medistore.app.data.entities.Medicine;

import java.util.List;

@Dao
public interface MedicineDao {

    @Query("SELECT * FROM medicines ORDER BY name ASC")
    LiveData<List<Medicine>> getAllMedicines();

    @Query("SELECT * FROM medicines WHERE name LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR manufacturer LIKE '%' || :query || '%' ORDER BY name ASC")
    LiveData<List<Medicine>> searchMedicines(String query);

    @Query("SELECT * FROM medicines WHERE category = :category ORDER BY name ASC")
    LiveData<List<Medicine>> getMedicinesByCategory(String category);

    @Query("SELECT * FROM medicines WHERE quantity <= minimumStock AND quantity > 0 ORDER BY quantity ASC")
    LiveData<List<Medicine>> getLowStockMedicines();

    @Query("SELECT * FROM medicines WHERE quantity = 0 ORDER BY name ASC")
    LiveData<List<Medicine>> getOutOfStockMedicines();

    @Query("SELECT * FROM medicines WHERE expiryDate <= :threshold AND expiryDate >= :today ORDER BY expiryDate ASC")
    LiveData<List<Medicine>> getExpiringSoonMedicines(String today, String threshold);

    @Query("SELECT * FROM medicines WHERE expiryDate < :today ORDER BY expiryDate ASC")
    LiveData<List<Medicine>> getExpiredMedicines(String today);

    @Query("SELECT * FROM medicines ORDER BY expiryDate ASC")
    LiveData<List<Medicine>> getAllMedicinesByExpiry();

    @Query("SELECT * FROM medicines WHERE medicineId = :id")
    LiveData<Medicine> getMedicineById(int id);

    @Query("SELECT * FROM medicines WHERE medicineId = :id")
    Medicine getMedicineByIdSync(int id);

    @Query("SELECT * FROM medicines ORDER BY name ASC")
    List<Medicine> getAllMedicinesSync();

    @Insert
    void insertMedicine(Medicine medicine);

    @Update
    void updateMedicine(Medicine medicine);

    @Delete
    void deleteMedicine(Medicine medicine);

    @Query("UPDATE medicines SET quantity = :newQuantity WHERE medicineId = :medicineId")
    void updateStock(int medicineId, int newQuantity);

    @Query("SELECT COUNT(*) FROM medicines")
    int getMedicineCount();

    @Query("SELECT COALESCE(SUM(quantity), 0) FROM medicines")
    int getTotalStock();

    @Query("SELECT COUNT(*) FROM medicines WHERE quantity <= minimumStock AND quantity > 0")
    int getLowStockCount();

    @Query("SELECT COUNT(*) FROM medicines WHERE expiryDate < :today")
    int getExpiredCount(String today);

    @Query("SELECT COUNT(*) FROM medicines WHERE quantity = 0")
    int getOutOfStockCount();
}
