package com.medistore.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.medistore.app.data.entities.SaleItem;

import java.util.List;

@Dao
public interface SaleItemDao {

    @Query("SELECT * FROM sale_items WHERE saleId = :saleId")
    LiveData<List<SaleItem>> getItemsBySaleId(int saleId);

    @Query("SELECT * FROM sale_items WHERE saleId = :saleId")
    List<SaleItem> getItemsBySaleIdSync(int saleId);

    @Insert
    void insertSaleItem(SaleItem saleItem);

    @Query("DELETE FROM sale_items WHERE saleId = :saleId")
    void deleteSaleItemsBySaleId(int saleId);

    @Query("SELECT COALESCE(SUM(quantity), 0) FROM sale_items WHERE medicineId = :medicineId")
    int getTotalSoldQuantity(int medicineId);
}
