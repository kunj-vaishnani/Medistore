package com.medistore.app.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.medistore.app.data.model.CartItem;
import com.medistore.app.data.entities.Customer;
import com.medistore.app.data.entities.Medicine;
import com.medistore.app.data.entities.Sale;
import com.medistore.app.data.entities.SaleItem;
import com.medistore.app.data.repository.CustomerRepository;
import com.medistore.app.data.repository.MedicineRepository;
import com.medistore.app.data.repository.SaleRepository;

import java.util.ArrayList;
import java.util.List;

public class SalesViewModel extends AndroidViewModel {
    private final SaleRepository saleRepo;
    private final MedicineRepository medicineRepo;
    private final CustomerRepository customerRepo;
    private final LiveData<List<Sale>> allSales;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final LiveData<List<Sale>> filteredSales;

    private final LiveData<List<Medicine>> allMedicines;
    private final LiveData<List<Customer>> allCustomers;

    private final MutableLiveData<Double> todaySales = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> totalRevenue = new MutableLiveData<>(0.0);
    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>(new ArrayList<>());

    public SalesViewModel(Application application) {
        super(application);
        saleRepo = new SaleRepository(application);
        medicineRepo = new MedicineRepository(application);
        customerRepo = new CustomerRepository(application);

        allSales = saleRepo.getAllSales();
        allMedicines = medicineRepo.getAllMedicines();
        allCustomers = customerRepo.getAllCustomers();

        filteredSales = Transformations.switchMap(searchQuery, query -> {
            if (query == null || query.trim().isEmpty()) {
                return allSales;
            } else {
                return saleRepo.searchSales(query.trim());
            }
        });
    }

    public void insertSaleWithItems(Sale sale, List<SaleItem> items) {
        saleRepo.insertSaleWithItems(sale, items);
    }

    public void deleteSale(Sale sale) {
        saleRepo.deleteSale(sale);
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public LiveData<List<Sale>> getFilteredSales() {
        return filteredSales;
    }

    public LiveData<List<Medicine>> getAllMedicines() {
        return allMedicines;
    }

    public LiveData<List<Customer>> getAllCustomers() {
        return allCustomers;
    }

    public MutableLiveData<Double> getTodaySales() {
        return todaySales;
    }

    public MutableLiveData<Double> getTotalRevenue() {
        return totalRevenue;
    }

    public void refreshStats() {
        // Implementation for stats
    }

    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    public void addToCart(Medicine m, int qty) {
        List<CartItem> currentCart = cartItems.getValue();
        if (currentCart != null) {
            boolean exists = false;
            for (CartItem item : currentCart) {
                if (item.getMedicineId() == m.getMedicineId()) {
                    item.setQuantity(item.getQuantity() + qty);
                    item.setTotal(item.getQuantity() * item.getPrice());
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                currentCart.add(new CartItem(m.getMedicineId(), m.getName(), qty, m.getSellingPrice(), qty * m.getSellingPrice()));
            }
            cartItems.setValue(currentCart);
        }
    }

    public void removeFromCart(int position) {
        List<CartItem> currentCart = cartItems.getValue();
        if (currentCart != null && position >= 0 && position < currentCart.size()) {
            currentCart.remove(position);
            cartItems.setValue(currentCart);
        }
    }

    public void updateCartQty(int position, int qty) {
        List<CartItem> currentCart = cartItems.getValue();
        if (currentCart != null && position >= 0 && position < currentCart.size()) {
            CartItem item = currentCart.get(position);
            item.setQuantity(qty);
            item.setTotal(qty * item.getPrice());
            cartItems.setValue(currentCart);
        }
    }

    public void clearCart() {
        cartItems.setValue(new ArrayList<>());
    }

    public double calculateSubtotal() {
        double subtotal = 0;
        List<CartItem> currentCart = cartItems.getValue();
        if (currentCart != null) {
            for (CartItem item : currentCart) {
                subtotal += item.getTotal();
            }
        }
        return subtotal;
    }

    public double calculateTotal(double discount, double gst) {
        double subtotal = calculateSubtotal();
        double afterDiscount = subtotal - (subtotal * discount / 100.0);
        return afterDiscount + (afterDiscount * gst / 100.0);
    }
}
