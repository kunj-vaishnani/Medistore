package com.medistore.app.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.medistore.app.data.dao.CustomerDao;
import com.medistore.app.data.dao.MedicineDao;
import com.medistore.app.data.dao.PurchaseDao;
import com.medistore.app.data.dao.SaleDao;
import com.medistore.app.data.dao.SaleItemDao;
import com.medistore.app.data.dao.SupplierDao;
import com.medistore.app.data.entities.Customer;
import com.medistore.app.data.entities.Medicine;
import com.medistore.app.data.entities.Purchase;
import com.medistore.app.data.entities.Sale;
import com.medistore.app.data.entities.SaleItem;
import com.medistore.app.data.entities.Supplier;

@Database(entities = {Medicine.class, Supplier.class, Customer.class, Sale.class, SaleItem.class, Purchase.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract MedicineDao medicineDao();
    public abstract SupplierDao supplierDao();
    public abstract CustomerDao customerDao();
    public abstract SaleDao saleDao();
    public abstract SaleItemDao saleItemDao();
    public abstract PurchaseDao purchaseDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "medistore_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
