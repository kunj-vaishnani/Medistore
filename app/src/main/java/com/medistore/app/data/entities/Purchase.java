package com.medistore.app.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "purchases",
    foreignKeys = {
        @ForeignKey(entity = Supplier.class,
            parentColumns = "supplierId", childColumns = "supplierId",
            onDelete = ForeignKey.SET_NULL),
        @ForeignKey(entity = Medicine.class,
            parentColumns = "medicineId", childColumns = "medicineId",
            onDelete = ForeignKey.SET_NULL)
    },
    indices = {@Index("supplierId"), @Index("medicineId")})
public class Purchase {
    @PrimaryKey(autoGenerate = true)
    public int purchaseId;
    public Integer supplierId; // nullable FK
    public String supplierName;  // denormalized
    public Integer medicineId; // nullable FK
    public String medicineName;  // denormalized
    public String batchNumber;
    public int quantity;
    public double purchasePrice;
    public String purchaseDate;  // "dd/MM/yyyy"
    public String expiryDate;    // "dd/MM/yyyy"
    public long createdAt;

    public Purchase() {
    }

    public int getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
