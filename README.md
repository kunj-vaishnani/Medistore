# MediStore – Medical Store Management System

A clean, modern, and interactive Android application designed for pharmacy and medical store inventory, sales billing, purchase tracking, and stock management. Built using pure **Java**, **XML layouts**, **Material Design 3**, and **Android Jetpack (MVVM + Room SQLite Database)**.

---

## 📱 Project Overview

MediStore operates completely **offline** using an embedded SQLite database through Android's Room Persistence Library. It automatically seeds rich demonstration data on first launch, ensuring seamless evaluation during practical exams and college vivas.

### 🔑 Default Login Credentials
* **Username:** `admin`
* **Password:** `admin123`
*(Includes a "Remember Me" session toggle stored in SharedPreferences)*

---

## 🏛️ Architecture & Tech Stack

* **Language:** Java (100% Java & XML — no Kotlin, no Python)
* **Architecture:** Simple MVVM (Model-View-ViewModel)
  * **View:** XML Layouts with Material 3 Components, ViewBinding, Activities & Fragments
  * **ViewModel:** `AndroidViewModel` + `LiveData` for reactive UI updates
  * **Repository:** Threaded repository pattern using background `ExecutorService`
  * **Model / Database:** Room Database with 6 Relational Entities & DAOs
* **Navigation:** Android Jetpack Navigation Component with Bottom Navigation + Sub-menus
* **Target / Compile SDK:** 34
* **Minimum SDK:** 24 (Android 7.0 Nougat and above)

---

## 📦 Main Modules (12 Complete Modules)

1. **Dashboard**
   * Real-time metric cards: Total Medicines, Available Stock, Low Stock, Expired Medicines, Today's Sales, Customer Count.
   * Quick action buttons for rapid navigation.
   * Recent sales list automatically populated from database.
2. **Medicine Management**
   * Complete inventory list with category filter chips and instant search.
   * Add, edit, delete, and view details of medicines (Batch number, Mfg/Exp dates, rack info, prices, stock levels).
3. **Inventory / Stock Management**
   * Real-time stock status badges: *In Stock*, *Low Stock*, *Out of Stock*, *Expired*.
   * Filter inventory by stock health.
4. **Sales & Billing Module (Point of Sale)**
   * Medicine search with instant stock validation (cannot sell more than available quantity).
   * Interactive cart with quantity increment/decrement controls.
   * Automatic subtotal, discount %, 12% GST, and grand total calculations.
   * Multiple payment options: Cash, UPI, Card.
   * Automatic stock reduction upon bill generation.
5. **Purchase Management**
   * Record new stock purchases from registered suppliers.
   * Automatically increases medicine stock quantity in the inventory.
6. **Supplier Management**
   * Manage distributor and pharmaceutical supplier details (Company, contact person, phone, email, address).
7. **Customer Management**
   * Maintain patient/customer records with contact details and purchase histories.
8. **Expiry Management**
   * Monitors medicine expiration dates with color-coded alerts (Expired in red, Expiring within 7/30 days in orange).
9. **Low Stock Alerts**
   * Identifies medicines where `Current Quantity <= Minimum Stock Level`.
   * Direct "Restock" action linking into the purchase workflow.
10. **Sales History**
    * Full ledger of past invoices with payment methods, item counts, and totals.
    * Detailed invoice view with printable receipt summary.
11. **Reports Module**
    * Business intelligence overview: Today's sales, monthly sales, revenue totals, inventory health breakdown, and customer counts.
12. **Settings & Profile**
    * Pharmacy store information configuration (store name, phone, address).
    * Dark mode toggle (`AppCompatDelegate`).
    * Session logout and data reset controls.

---

## 🗄️ Database Design (Room SQLite)

The Room Database (`medistore_database`) includes 6 interconnected entities:

* **`Medicine`**: `medicineId` (PK), `name`, `category`, `manufacturer`, `batchNumber`, `purchasePrice`, `sellingPrice`, `quantity`, `minimumStock`, `manufacturingDate`, `expiryDate`, `supplierId` (FK), `description`, `createdAt`
* **`Supplier`**: `supplierId` (PK), `name`, `company`, `phone`, `email`, `address`
* **`Customer`**: `customerId` (PK), `name`, `phone`, `email`, `address`
* **`Sale`**: `saleId` (PK), `invoiceNumber`, `customerId` (FK), `customerName`, `customerPhone`, `saleDate`, `subtotal`, `discount`, `gst`, `total`, `paymentMethod`
* **`SaleItem`**: `saleItemId` (PK), `saleId` (FK CASCADE), `medicineId` (FK SET_NULL), `medicineName`, `quantity`, `price`, `total`
* **`Purchase`**: `purchaseId` (PK), `supplierId` (FK SET_NULL), `supplierName`, `medicineId` (FK SET_NULL), `medicineName`, `batchNumber`, `quantity`, `purchasePrice`, `purchaseDate`, `expiryDate`, `createdAt`

---

## 🚀 How to Run in Android Studio

1. Open **Android Studio**.
2. Select **Open** and choose the directory:  
   `C:\Users\tirth\.gemini\antigravity\scratch\MediStore`
3. Allow Gradle to sync dependencies.
4. Select an Android Emulator or physical device (API 24+).
5. Click **Run** (`Shift + F10`).
6. Sign in with `admin` / `admin123`.

---

## 🎓 Viva & Presentation Explanations

* **How offline data persistence works:** The application uses Android Jetpack Room, which acts as an abstraction layer over SQLite. `SampleDataHelper.java` checks `KEY_SAMPLE_DATA_INSERTED` in `SharedPreferences` and pre-populates initial records on the background thread on first run.
* **How stock synchronization works:** When a sale is confirmed in `SaleRepository.insertSaleWithItems()`, the transaction writes the sale items and subtracts sold quantities from the `Medicine` table. Conversely, `PurchaseRepository.insertPurchase()` increases stock levels automatically.
* **Why MVVM was chosen:** UI components (Activities/Fragments) only observe `LiveData` from ViewModels, ensuring separation of business logic from the UI and handling device rotations without state loss.
