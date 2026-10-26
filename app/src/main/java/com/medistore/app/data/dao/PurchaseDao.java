package com.medistore.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.medistore.app.data.entities.Purchase;

import java.util.List;

@Dao
public interface PurchaseDao {

    @Query("SELECT * FROM purchases ORDER BY purchaseId DESC")
    LiveData<List<Purchase>> getAllPurchases();

    @Query("SELECT * FROM purchases WHERE medicineName LIKE '%' || :query || '%' OR supplierName LIKE '%' || :query || '%' ORDER BY purchaseId DESC")
    LiveData<List<Purchase>> searchPurchases(String query);

    @Query("SELECT * FROM purchases WHERE purchaseId = :id")
    Purchase getPurchaseById(int id);

    @Insert
    long insertPurchase(Purchase purchase);

    @Update
    void updatePurchase(Purchase purchase);

    @Delete
    void deletePurchase(Purchase purchase);
}
