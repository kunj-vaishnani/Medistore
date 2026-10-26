package com.medistore.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.medistore.app.data.entities.Sale;

import java.util.List;

@Dao
public interface SaleDao {

    @Query("SELECT * FROM sales ORDER BY saleId DESC")
    LiveData<List<Sale>> getAllSales();

    @Query("SELECT * FROM sales ORDER BY saleId DESC LIMIT :limit")
    LiveData<List<Sale>> getRecentSales(int limit);

    @Query("SELECT * FROM sales WHERE invoiceNumber LIKE '%' || :query || '%' OR customerName LIKE '%' || :query || '%' ORDER BY saleId DESC")
    LiveData<List<Sale>> searchSales(String query);

    @Query("SELECT * FROM sales WHERE saleDate LIKE :date || '%' ORDER BY saleId DESC")
    LiveData<List<Sale>> getSalesByDate(String date);

    @Query("SELECT * FROM sales WHERE saleId = :id")
    Sale getSaleById(int id);

    @Insert
    long insertSale(Sale sale);

    @Delete
    void deleteSale(Sale sale);

    @Query("SELECT COALESCE(SUM(total), 0) FROM sales WHERE saleDate LIKE :today || '%'")
    double getTodaySalesTotal(String today);

    @Query("SELECT COUNT(*) FROM sales")
    int getSaleCount();

    @Query("SELECT COALESCE(SUM(total), 0) FROM sales")
    double getTotalRevenue();

    @Query("SELECT COALESCE(SUM(total), 0) FROM sales WHERE saleDate LIKE :monthYear || '%'")
    double getMonthlySalesTotal(String monthYear);
}
