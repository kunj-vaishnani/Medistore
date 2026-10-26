package com.medistore.app.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "sale_items",
    foreignKeys = {
        @ForeignKey(entity = Sale.class,
            parentColumns = "saleId", childColumns = "saleId",
            onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Medicine.class,
            parentColumns = "medicineId", childColumns = "medicineId",
            onDelete = ForeignKey.SET_NULL)
    },
    indices = {@Index("saleId"), @Index("medicineId")})
public class SaleItem {
    @PrimaryKey(autoGenerate = true)
    public int saleItemId;
    public int saleId;
    public Integer medicineId; // nullable FK
    public String medicineName;  // denormalized
    public int quantity;
    public double price;
    public double total;

    public SaleItem() {
    }

    @androidx.room.Ignore
    public SaleItem(int saleId, Integer medicineId, String medicineName, int quantity, double price, double total) {
        this.saleId = saleId;
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
    }

    public int getSaleItemId() {
        return saleItemId;
    }

    public void setSaleItemId(int saleItemId) {
        this.saleItemId = saleItemId;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public Integer getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Integer medicineId) {
        this.medicineId = medicineId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
