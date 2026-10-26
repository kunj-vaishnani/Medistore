package com.medistore.app.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "suppliers")
public class Supplier {
    @PrimaryKey(autoGenerate = true)
    public int supplierId;
    public String name;
    public String company;
    public String phone;
    public String email;
    public String address;

    public Supplier() {
    }

    @androidx.room.Ignore
    public Supplier(String name, String company, String phone, String email, String address) {
        this.name = name;
        this.company = company;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @androidx.annotation.NonNull
    @Override
    public String toString() {
        return name != null ? (company != null && !company.isEmpty() ? name + " (" + company + ")" : name) : "";
    }
}
