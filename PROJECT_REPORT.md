# MediStore – Medical Store Management System
## Academic Project Report

**G H Patel College of Engineering & Technology**  
*(A Constituent College of CVM University)*  
**V. V. Nagar**  
**INFORMATION TECHNOLOGY DEPARTMENT**  

---

### **Submitted By:**
* **Name of Student:** Viren Prajapati *(Customizable)*
* **Enrollment Number:** 12302080501064 *(Customizable)*

### **Guided By:**
* **Dr. Premkumar Balani**

**Subject:** Mobile Application Development (202046712)  
**Academic Year:** A.Y. 2026-27 ODD TERM  

---

<div style="page-break-after: always;"></div>

## 1. Introduction
The **MediStore – Medical Store Management System** is a native Android application engineered to help pharmacy owners and retail medical store managers completely digitalize their day-to-day operations. The application provides an integrated, robust interface to manage pharmaceutical inventory, track batch numbers and manufacturing/expiry dates, process high-speed over-the-counter sales with automated GST and discounts, manage supplier purchase orders, and monitor customer profiles.

By transitioning from conventional manual paper registers to a digital offline-first architecture, medical store managers minimize dispensing errors, prevent the sale of expired medicines, prevent revenue leakage, and gain real-time visibility into overall store health. The application strictly adheres to modern **Material Design 3** principles, supporting both seamless **Dark Mode** and **Light Mode** with role-based accessibility for Store Administrators and Customers.

---

## 2. Problem Statement
Numerous retail pharmacies and medical establishments still depend heavily on physical paper ledgers and manual ledger books. This legacy workflow introduces critical operational hazards:
* **Risk of Dispensing Expired Drugs:** Manually monitoring expiry dates across hundreds of pharmaceutical batches often fails, risking customer health and regulatory penalties.
* **Time-Consuming Billing:** Calculating subtotal, itemized discounts, and 12% GST by hand during peak pharmacy hours causes significant queue delays.
* **Stock-Outs of Critical Medicines:** Without proactive low-stock threshold notifications, pharmacists run out of essential prescription medications unexpectedly.
* **Data Loss & Damage:** Paper registers are prone to physical wear, water damage, misplacement, and lack access security.
* **Absence of Business Analytics:** Store owners cannot instantly view today’s gross revenue, monthly margins, or identify top-selling medications.

---

## 3. Objectives
* To replace paper registers with a reliable, structured digital repository for medicines, purchases, sales, suppliers, and customers.
* To proactively safeguard patient health by classifying medicines into real-time visual statuses (*In Stock, Low Stock, Out of Stock, Expired*).
* To accelerate checkout using a streamlined Point of Sale (POS) cart system with automated GST and discount computation.
* To automate inventory replenishment so sales automatically decrement stock and supplier purchases increment stock.
* To ensure complete data confidentiality and instantaneous operation by functioning 100% offline via local SQLite Room Database.

---

## 4. Proposed Solution
To overcome these challenges, **MediStore** was architected as an offline-first Android application powered by Google’s Room Persistence Library and Simple MVVM architecture. The system features a dynamic dashboard with live summary cards, proactive notifications for low-stock and upcoming expiries, one-tap restocking, and strict role separation guaranteeing customer users access solely to medicine catalogs while shielding administrative financials.

---

<div style="page-break-after: always;"></div>

## 5. Technology Stack

| Component | Technology Used |
| :--- | :--- |
| **Platform** | Android Mobile Operating System |
| **Programming Language** | Java (100% Java & XML Architecture) |
| **UI Framework** | Android XML, Material Design 3 Components (MaterialCardView, RecyclerView, BottomNavigationView, Extended Toolbar) |
| **Local Database** | Room Persistence Library (SQLite Object Mapping) |
| **Architecture** | MVVM (Model-View-ViewModel) with View Binding, LiveData & Repository Pattern |
| **Minimum SDK** | Android 7.0 (API Level 24 – Nougat) |
| **Target / Compile SDK** | Android 14 / 15 (API Level 34 / 35) |

---

## 6. Core Modules & Code Implementation

### A. Database Layer (Room DAOs)

#### 1. Medicine DAO (`MedicineDao.java`)
```java
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

    @Insert
    void insertMedicine(Medicine medicine);

    @Update
    void updateMedicine(Medicine medicine);

    @Delete
    void deleteMedicine(Medicine medicine);

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

    @Query("SELECT * FROM medicines WHERE expiryDate < :today ORDER BY expiryDate ASC")
    LiveData<List<Medicine>> getExpiredMedicines(String today);

    @Query("SELECT * FROM medicines WHERE expiryDate <= :threshold AND expiryDate >= :today ORDER BY expiryDate ASC")
    LiveData<List<Medicine>> getExpiringSoonMedicines(String today, String threshold);

    @Query("SELECT * FROM medicines WHERE medicineId = :id")
    Medicine getMedicineByIdSync(int id);

    @Query("UPDATE medicines SET quantity = :newQuantity WHERE medicineId = :medicineId")
    void updateStock(int medicineId, int newQuantity);

    @Query("SELECT COUNT(*) FROM medicines")
    int getMedicineCount();

    @Query("SELECT COALESCE(SUM(quantity), 0) FROM medicines")
    int getTotalStock();
}
```

#### 2. Sales DAO (`SaleDao.java`)
```java
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

    @Insert
    long insertSale(Sale sale);

    @Delete
    void deleteSale(Sale sale);

    @Query("SELECT * FROM sales ORDER BY saleId DESC")
    LiveData<List<Sale>> getAllSales();

    @Query("SELECT * FROM sales ORDER BY saleId DESC LIMIT :limit")
    LiveData<List<Sale>> getRecentSales(int limit);

    @Query("SELECT * FROM sales WHERE invoiceNumber LIKE '%' || :query || '%' OR customerName LIKE '%' || :query || '%' ORDER BY saleId DESC")
    LiveData<List<Sale>> searchSales(String query);

    @Query("SELECT COALESCE(SUM(total), 0) FROM sales WHERE saleDate LIKE :today || '%'")
    double getTodaySalesTotal(String today);

    @Query("SELECT COUNT(*) FROM sales")
    int getSaleCount();

    @Query("SELECT COALESCE(SUM(total), 0) FROM sales")
    double getTotalRevenue();
}
```

#### 3. Purchase DAO (`PurchaseDao.java`)
```java
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

    @Insert
    long insertPurchase(Purchase purchase);

    @Update
    void updatePurchase(Purchase purchase);

    @Delete
    void deletePurchase(Purchase purchase);

    @Query("SELECT * FROM purchases ORDER BY purchaseId DESC")
    LiveData<List<Purchase>> getAllPurchases();

    @Query("SELECT * FROM purchases WHERE medicineName LIKE '%' || :query || '%' OR supplierName LIKE '%' || :query || '%' ORDER BY purchaseId DESC")
    LiveData<List<Purchase>> searchPurchases(String query);
}
```

---

<div style="page-break-after: always;"></div>

### B. Controller & Activity Layer

#### 1. Main Navigation Controller (`MainActivity.java`)
```java
package com.medistore.app.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.medistore.app.R;
import com.medistore.app.databinding.ActivityMainBinding;
import com.medistore.app.ui.login.LoginActivity;
import com.medistore.app.utils.Constants;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences sharedPreferences = getSharedPreferences(
                Constants.PREF_NAME, Context.MODE_PRIVATE);
        if (!sharedPreferences.getBoolean(Constants.KEY_IS_LOGGED_IN, false)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.bottomNav, navController);
        }

        // Role-Based Access Control: Hide Billing & Inventory tabs for Customers
        String role = sharedPreferences.getString("user_role", "Store Administrator");
        if ("Customer".equalsIgnoreCase(role)) {
            binding.bottomNav.getMenu().findItem(R.id.nav_sales).setVisible(false);
            binding.bottomNav.getMenu().findItem(R.id.nav_inventory).setVisible(false);
        }
    }

    @Override
    public void onBackPressed() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            if (navController.getCurrentDestination() != null &&
                    navController.getCurrentDestination().getId() == R.id.nav_dashboard) {
                finishAffinity();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }
}
```

---

<div style="page-break-after: always;"></div>

#### 2. POS Billing Terminal (`AddSaleActivity.java`)
```java
package com.medistore.app.ui.sales;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.medistore.app.data.entities.Medicine;
import com.medistore.app.data.entities.Sale;
import com.medistore.app.data.entities.SaleItem;
import com.medistore.app.data.model.CartItem;
import com.medistore.app.databinding.ActivityAddSaleBinding;
import com.medistore.app.utils.CurrencyUtils;
import com.medistore.app.utils.DateUtils;
import com.medistore.app.viewmodel.SalesViewModel;
import java.util.ArrayList;
import java.util.List;

public class AddSaleActivity extends AppCompatActivity {
    private ActivityAddSaleBinding binding;
    private SalesViewModel viewModel;
    private CartAdapter cartAdapter;
    private Medicine selectedMedicine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddSaleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(SalesViewModel.class);

        setupToolbar();
        setupCartRecyclerView();
        setupDefaultCustomer();
        setupObservers();
        setupListeners();
    }

    private void setupDefaultCustomer() {
        binding.etCustomerName.setText("Walk-in Customer");
        binding.etCustomerPhone.setText("");
    }

    private void setupListeners() {
        binding.btnWalkinCustomer.setOnClickListener(v -> setupDefaultCustomer());

        binding.acMedicine.setOnItemClickListener((parent, view, position, id) -> {
            selectedMedicine = (Medicine) parent.getItemAtPosition(position);
            if (selectedMedicine != null) {
                binding.layoutSelectedMedicineInfo.setVisibility(View.VISIBLE);
                binding.tvSelectedMedName.setText(selectedMedicine.getName());
                binding.tvSelectedMedPrice.setText(CurrencyUtils.formatCurrency(
                        selectedMedicine.getSellingPrice()) + " / unit");
                binding.tvMedicineInfo.setText("Category: " + selectedMedicine.getCategory() 
                        + " • Stock: " + selectedMedicine.getQuantity() + " available");
            }
        });

        binding.btnQtyMinus.setOnClickListener(v -> {
            int current = getEnteredQty();
            if (current > 1) binding.etMedicineQty.setText(String.valueOf(current - 1));
        });

        binding.btnQtyPlus.setOnClickListener(v -> {
            int current = getEnteredQty();
            if (selectedMedicine != null && current >= selectedMedicine.getQuantity()) {
                Toast.makeText(this, "Maximum available stock reached", Toast.LENGTH_SHORT).show();
                return;
            }
            binding.etMedicineQty.setText(String.valueOf(current + 1));
        });

        binding.btnAddCart.setOnClickListener(v -> {
            if (selectedMedicine == null) {
                Toast.makeText(this, "Select a medicine first", Toast.LENGTH_SHORT).show();
                return;
            }
            int qty = getEnteredQty();
            if (qty > selectedMedicine.getQuantity()) {
                Toast.makeText(this, "Quantity exceeds available stock", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.addToCart(selectedMedicine, qty);
            binding.acMedicine.setText("");
            binding.layoutSelectedMedicineInfo.setVisibility(View.GONE);
            binding.etMedicineQty.setText("1");
            selectedMedicine = null;
        });

        binding.etDiscount.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSummary();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.btnGenerateBill.setOnClickListener(v -> generateBill());
    }

    private void updateSummary() {
        double subtotal = viewModel.calculateSubtotal();
        String discountStr = binding.etDiscount.getText().toString().trim();
        double discount = discountStr.isEmpty() ? 0 : Double.parseDouble(discountStr);
        double gst = 12.0;
        double total = viewModel.calculateTotal(discount, gst);

        binding.tvSubtotal.setText(CurrencyUtils.formatCurrency(subtotal));
        binding.tvGst.setText("12%");
        binding.tvTotalAmount.setText(CurrencyUtils.formatCurrency(total));
    }

    private void generateBill() {
        List<CartItem> cartItems = viewModel.getCartItems().getValue();
        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String invoiceNumber = "INV-" + (System.currentTimeMillis() % 1000000);
        String paymentMethod = binding.rbUpi.isChecked() ? "UPI" : 
                               (binding.rbCard.isChecked() ? "Card" : "Cash");

        Sale sale = new Sale();
        sale.setInvoiceNumber(invoiceNumber);
        sale.setCustomerName(binding.etCustomerName.getText().toString().trim());
        sale.setCustomerPhone(binding.etCustomerPhone.getText().toString().trim());
        sale.setSaleDate(DateUtils.getCurrentDateTime());
        sale.setSubtotal(viewModel.calculateSubtotal());
        sale.setTotal(viewModel.calculateTotal(getDiscount(), 12.0));
        sale.setPaymentMethod(paymentMethod);

        List<SaleItem> saleItems = new ArrayList<>();
        for (CartItem ci : cartItems) {
            SaleItem si = new SaleItem();
            si.setMedicineId(ci.getMedicineId());
            si.setMedicineName(ci.getMedicineName());
            si.setQuantity(ci.getQuantity());
            si.setPrice(ci.getPrice());
            si.setTotal(ci.getTotal());
            saleItems.add(si);
        }

        viewModel.insertSaleWithItems(sale, saleItems);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Bill Generated Successfully")
                .setMessage("Invoice #: " + invoiceNumber + "\nPayment: " + paymentMethod)
                .setPositiveButton("Done", (dialog, which) -> finish())
                .show();
    }
}
```

---

<div style="page-break-after: always;"></div>

#### 3. Restocking & Purchases (`AddPurchaseActivity.java`)
```java
package com.medistore.app.ui.purchase;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.medistore.app.data.entities.Medicine;
import com.medistore.app.data.entities.Purchase;
import com.medistore.app.data.entities.Supplier;
import com.medistore.app.databinding.ActivityAddPurchaseBinding;
import com.medistore.app.utils.CurrencyUtils;
import com.medistore.app.utils.DateUtils;
import com.medistore.app.viewmodel.PurchaseViewModel;
import java.util.Calendar;
import java.util.Random;

public class AddPurchaseActivity extends AppCompatActivity {
    private ActivityAddPurchaseBinding binding;
    private PurchaseViewModel viewModel;
    private Supplier selectedSupplier;
    private Medicine selectedMedicine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPurchaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(PurchaseViewModel.class);

        binding.etDate.setText(DateUtils.getTodayDate());
        setExpiryYearsAhead(1);

        binding.acMedicine.setOnItemClickListener((parent, view, position, id) -> {
            selectedMedicine = (Medicine) parent.getItemAtPosition(position);
            if (selectedMedicine != null) {
                binding.etPrice.setText(String.valueOf(selectedMedicine.getPurchasePrice()));
                if (selectedMedicine.getBatchNumber() != null) {
                    binding.etBatch.setText(selectedMedicine.getBatchNumber());
                }
                if (selectedMedicine.getExpiryDate() != null) {
                    binding.etExpiry.setText(selectedMedicine.getExpiryDate());
                }
                updateCostPreview();
            }
        });

        binding.btnAutoBatch.setOnClickListener(v -> {
            binding.etBatch.setText("BAT-" + (1000 + new Random().nextInt(9000)));
        });

        binding.btnExpiry1yr.setOnClickListener(v -> setExpiryYearsAhead(1));
        binding.btnExpiry2yr.setOnClickListener(v -> setExpiryYearsAhead(2));

        binding.etDate.setOnClickListener(v -> showDatePicker(binding.etDate));
        binding.etExpiry.setOnClickListener(v -> showDatePicker(binding.etExpiry));

        binding.btnSave.setOnClickListener(v -> savePurchase());
    }

    private void savePurchase() {
        if (selectedMedicine == null || selectedSupplier == null) {
            Toast.makeText(this, "Select both medicine and supplier", Toast.LENGTH_SHORT).show();
            return;
        }

        int qty = Integer.parseInt(binding.etQty.getText().toString());
        double price = Double.parseDouble(binding.etPrice.getText().toString());

        Purchase purchase = new Purchase();
        purchase.setSupplierId(selectedSupplier.getSupplierId());
        purchase.setSupplierName(selectedSupplier.getName());
        purchase.setMedicineId(selectedMedicine.getMedicineId());
        purchase.setMedicineName(selectedMedicine.getName());
        purchase.setBatchNumber(binding.etBatch.getText().toString().trim());
        purchase.setQuantity(qty);
        purchase.setPurchasePrice(price);
        purchase.setPurchaseDate(binding.etDate.getText().toString().trim());
        purchase.setExpiryDate(binding.etExpiry.getText().toString().trim());
        purchase.setCreatedAt(System.currentTimeMillis());

        // Automatically increments medicine quantity in Room inventory
        viewModel.insert(purchase);

        Toast.makeText(this, "Stock added to inventory successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
```

---

<div style="page-break-after: always;"></div>

## 7. Application Screenshots & User Interface

| Screen Representation | Description |
| :--- | :--- |
| **Figure 1: Login Screen** | Local authentication screen with role-based routing (`admin` / `admin123`), remember me toggle, and direct link to customer registration. |
| **Figure 2: Registration Screen with Role Selection** | Registration dialog allowing new customers to register with Name, Phone, Email, and Password. |
| **Figure 3: Dashboard Overview** | Interactive dashboard presenting metric cards: Total Medicines, Available Stock, Low Stock, Expired Medicines, Today's Sales, and Customer count. |
| **Figure 4: Medicine Management & Stock Badges** | Filterable medicine directory with high-contrast status badges (*In Stock* in green, *Low Stock* in orange, *Expired* in purple). |
| **Figure 5: Point of Sale (POS) Billing** | Rapid checkout screen with pre-filled Walk-in Customer option, medicine stepper, interactive cart, automated 12% GST and discount calculation. |
| **Figure 6: Add Medicine & Restock Form** | Comprehensive medicine entry form with batch number, purchase/selling price, stock thresholds, and expiry picker. |
| **Figure 7: Expiry & Low Stock Logbook** | Dedicated safety screen showing medicines requiring immediate quarantine or supplier reordering. |
| **Figure 8: Supplier Management Directory** | Supplier contact ledger with agency names, phone numbers, and direct restock linkages. |

---

## 8. Conclusion
The **MediStore – Medical Store Management System** successfully accomplishes all stated objectives. It replaces traditional paper-based pharmaceutical tracking with an ultra-fast, offline-first native Android solution. By utilizing Google's recommended Jetpack architecture components (Room Database, LiveData, ViewModel, ViewBinding) and Material Design 3, MediStore eliminates calculation mistakes, automates inventory adjustments, and prevents the dispensing of expired medications while remaining intuitive for pharmacy managers.
