package com.medistore.app.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "medicines",
    foreignKeys = @ForeignKey(entity = Supplier.class,
        parentColumns = "supplierId", childColumns = "supplierId",
        onDelete = ForeignKey.SET_NULL),
    indices = {@Index("supplierId")})
public class Medicine {
    @PrimaryKey(autoGenerate = true)
    public int medicineId;
    public String name;
    public String category;        // Tablets, Syrup, Injection, Capsule, Cream, Drops
    public String manufacturer;
    public String batchNumber;
    public double purchasePrice;
    public double sellingPrice;
    public int quantity;
    public int minimumStock;
    public String manufacturingDate;  // stored as "dd/MM/yyyy"
    public String expiryDate;         // stored as "dd/MM/yyyy"
    public Integer supplierId;         // nullable FK
    public String description;
    public long createdAt;             // System.currentTimeMillis()

    public Medicine() {
    }

    public int getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(int medicineId) {
        this.medicineId = medicineId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(int minimumStock) {
        this.minimumStock = minimumStock;
    }

    public String getManufacturingDate() {
        return manufacturingDate;
    }

    public void setManufacturingDate(String manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @androidx.annotation.NonNull
    @Override
    public String toString() {
        return name != null ? name + " (Stock: " + quantity + " | ₹" + sellingPrice + ")" : "";
    }
}
