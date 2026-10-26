package com.medistore.app.utils;

import com.medistore.app.data.database.AppDatabase;
import com.medistore.app.data.entities.Customer;
import com.medistore.app.data.entities.Medicine;
import com.medistore.app.data.entities.Purchase;
import com.medistore.app.data.entities.Sale;
import com.medistore.app.data.entities.SaleItem;
import com.medistore.app.data.entities.Supplier;

public class SampleDataHelper {

    public static void insertSampleData(AppDatabase db) {
        // Suppliers
        Supplier s1 = new Supplier(); s1.name = "MedPharm Distributors"; s1.phone = "9876543210"; s1.email = "medpharm@gmail.com"; s1.company = "MedPharm";
        Supplier s2 = new Supplier(); s2.name = "HealthCare Suppliers"; s2.phone = "9123456789"; s2.email = "healthcare@gmail.com"; s2.company = "HealthCare";
        Supplier s3 = new Supplier(); s3.name = "Wellness Drug House"; s3.phone = "8765432109"; s3.email = "wellness@gmail.com"; s3.company = "Wellness";
        Supplier s4 = new Supplier(); s4.name = "LifeCare Pharmaceuticals"; s4.phone = "7654321098"; s4.email = "lifecare@gmail.com"; s4.company = "LifeCare";
        Supplier s5 = new Supplier(); s5.name = "CityMed Wholesale"; s5.phone = "6543210987"; s5.email = "citymed@gmail.com"; s5.company = "CityMed";

        int s1Id = (int) db.supplierDao().insertSupplier(s1);
        int s2Id = (int) db.supplierDao().insertSupplier(s2);
        int s3Id = (int) db.supplierDao().insertSupplier(s3);
        int s4Id = (int) db.supplierDao().insertSupplier(s4);
        int s5Id = (int) db.supplierDao().insertSupplier(s5);

        // Customers
        Customer c1 = new Customer(); c1.name = "Rajesh Kumar"; c1.phone = "9988776655"; c1.email = "rajesh@gmail.com";
        Customer c2 = new Customer(); c2.name = "Priya Sharma"; c2.phone = "8877665544"; c2.email = "priya@gmail.com";
        Customer c3 = new Customer(); c3.name = "Amit Patel"; c3.phone = "7766554433"; c3.email = "amit@gmail.com";
        Customer c4 = new Customer(); c4.name = "Sunita Verma"; c4.phone = "6655443322"; c4.email = "sunita@gmail.com";
        Customer c5 = new Customer(); c5.name = "Vikram Singh"; c5.phone = "5544332211"; c5.email = "vikram@gmail.com";

        int c1Id = (int) db.customerDao().insertCustomer(c1);
        int c2Id = (int) db.customerDao().insertCustomer(c2);
        int c3Id = (int) db.customerDao().insertCustomer(c3);
        int c4Id = (int) db.customerDao().insertCustomer(c4);
        int c5Id = (int) db.customerDao().insertCustomer(c5);

        // Medicines
        long time = System.currentTimeMillis();
        Medicine m1 = createMedicine("Paracetamol 500mg", "Tablets", "GSK", 150, 20, s1Id, DateUtils.getThresholdDate(365), 10.0, 15.0);
        Medicine m2 = createMedicine("Amoxicillin 250mg", "Capsule", "Cipla", 80, 15, s2Id, DateUtils.getThresholdDate(240), 20.0, 30.0);
        Medicine m3 = createMedicine("Metformin 500mg", "Tablets", "Sun Pharma", 10, 20, s3Id, DateUtils.getThresholdDate(180), 5.0, 10.0);
        Medicine m4 = createMedicine("Omeprazole 20mg", "Capsule", "Mankind", 45, 10, s4Id, DateUtils.getThresholdDate(120), 12.0, 20.0);
        Medicine m5 = createMedicine("Cough Syrup 100ml", "Syrup", "Dabur", 5, 10, s5Id, DateUtils.getThresholdDate(90), 45.0, 60.0);
        Medicine m6 = createMedicine("Vitamin C 500mg", "Tablets", "Abbott", 0, 15, s1Id, DateUtils.getThresholdDate(300), 25.0, 35.0);
        Medicine m7 = createMedicine("Aspirin 75mg", "Tablets", "Bayer", 200, 25, s2Id, DateUtils.getThresholdDate(540), 15.0, 25.0);
        Medicine m8 = createMedicine("Insulin 10ml", "Injection", "Novo Nordisk", 25, 10, s3Id, DateUtils.getThresholdDate(25), 250.0, 300.0);
        Medicine m9 = createMedicine("Eye Drops 5ml", "Drops", "Alcon", 30, 8, s4Id, DateUtils.getThresholdDate(10), 80.0, 120.0);
        Medicine m10 = createMedicine("Antifungal Cream 15g", "Cream", "Himalaya", 18, 5, s5Id, DateUtils.getThresholdDate(150), 65.0, 95.0);
        Medicine m11 = createMedicine("Cetirizine 10mg", "Tablets", "UCB", 60, 10, s1Id, DateUtils.getThresholdDate(-90), 8.0, 12.0);
        Medicine m12 = createMedicine("Azithromycin 500mg", "Tablets", "Cipla", 40, 10, s2Id, DateUtils.getThresholdDate(-30), 40.0, 55.0);
        Medicine m13 = createMedicine("Calcium Carbonate", "Tablets", "GlaxoSmithKline", 8, 15, s3Id, DateUtils.getThresholdDate(210), 30.0, 45.0);
        Medicine m14 = createMedicine("Dolo 650mg", "Tablets", "Micro Labs", 120, 20, s4Id, DateUtils.getThresholdDate(270), 20.0, 30.0);
        Medicine m15 = createMedicine("Ibuprofen 400mg", "Tablets", "Abbott", 0, 10, s5Id, DateUtils.getThresholdDate(330), 18.0, 25.0);

        db.medicineDao().insertMedicine(m1);
        db.medicineDao().insertMedicine(m2);
        db.medicineDao().insertMedicine(m3);
        db.medicineDao().insertMedicine(m4);
        db.medicineDao().insertMedicine(m5);
        db.medicineDao().insertMedicine(m6);
        db.medicineDao().insertMedicine(m7);
        db.medicineDao().insertMedicine(m8);
        db.medicineDao().insertMedicine(m9);
        db.medicineDao().insertMedicine(m10);
        db.medicineDao().insertMedicine(m11);
        db.medicineDao().insertMedicine(m12);
        db.medicineDao().insertMedicine(m13);
        db.medicineDao().insertMedicine(m14);
        db.medicineDao().insertMedicine(m15);

        // We could extract IDs by searching, but for simple mock data we assume IDs 1-15 sequentially
        // Sales
        insertSale(db, "INV-001", c1Id, c1.name, "Cash", 1, "Paracetamol 500mg", 2, 15.0);
        insertSale(db, "INV-002", c2Id, c2.name, "UPI", 2, "Amoxicillin 250mg", 1, 30.0);
        insertSale(db, "INV-003", c3Id, c3.name, "Card", 4, "Omeprazole 20mg", 5, 20.0);
        insertSale(db, "INV-004", c4Id, c4.name, "Cash", 8, "Insulin 10ml", 1, 300.0);
        insertSale(db, "INV-005", c5Id, c5.name, "UPI", 14, "Dolo 650mg", 3, 30.0);
        insertSale(db, "INV-006", c1Id, c1.name, "Card", 9, "Eye Drops 5ml", 2, 120.0);
        insertSale(db, "INV-007", c2Id, c2.name, "Cash", 10, "Antifungal Cream 15g", 1, 95.0);
        insertSale(db, "INV-008", c3Id, c3.name, "UPI", 7, "Aspirin 75mg", 10, 25.0);

        // Purchases
        insertPurchase(db, s1Id, s1.name, 1, "Paracetamol 500mg", 100, 10.0);
        insertPurchase(db, s2Id, s2.name, 2, "Amoxicillin 250mg", 50, 20.0);
        insertPurchase(db, s3Id, s3.name, 3, "Metformin 500mg", 20, 5.0);
        insertPurchase(db, s4Id, s4.name, 4, "Omeprazole 20mg", 40, 12.0);
        insertPurchase(db, s5Id, s5.name, 5, "Cough Syrup 100ml", 15, 45.0);
    }

    private static Medicine createMedicine(String name, String cat, String mfg, int qty, int minStock, int supplierId, String exp, double pPrice, double sPrice) {
        Medicine m = new Medicine();
        m.name = name;
        m.category = cat;
        m.manufacturer = mfg;
        m.quantity = qty;
        m.minimumStock = minStock;
        m.supplierId = supplierId;
        m.expiryDate = exp;
        m.purchasePrice = pPrice;
        m.sellingPrice = sPrice;
        m.createdAt = System.currentTimeMillis();
        return m;
    }

    private static void insertSale(AppDatabase db, String inv, int custId, String custName, String pMethod, int medId, String medName, int qty, double price) {
        Sale s = new Sale();
        s.invoiceNumber = inv;
        s.customerId = custId;
        s.customerName = custName;
        s.saleDate = DateUtils.getCurrentDateTime();
        s.subtotal = qty * price;
        s.discount = 0;
        s.gst = s.subtotal * (Constants.GST_RATE / 100.0);
        s.total = s.subtotal + s.gst;
        s.paymentMethod = pMethod;
        int sId = (int) db.saleDao().insertSale(s);

        SaleItem si = new SaleItem();
        si.saleId = sId;
        si.medicineId = medId;
        si.medicineName = medName;
        si.quantity = qty;
        si.price = price;
        si.total = qty * price;
        db.saleItemDao().insertSaleItem(si);
    }

    private static void insertPurchase(AppDatabase db, int suppId, String suppName, int medId, String medName, int qty, double pPrice) {
        Purchase p = new Purchase();
        p.supplierId = suppId;
        p.supplierName = suppName;
        p.medicineId = medId;
        p.medicineName = medName;
        p.quantity = qty;
        p.purchasePrice = pPrice;
        p.purchaseDate = DateUtils.getTodayDate();
        p.createdAt = System.currentTimeMillis();
        db.purchaseDao().insertPurchase(p);
    }
}
