package com.rental.service;

import com.rental.model.Bike;
import com.rental.model.Car;
import com.rental.model.Customer;
import com.rental.model.Rental;
import com.rental.model.Vehicle;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RentalService {
    private final List<Vehicle> vehicles = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Rental> rentals = new ArrayList<>();

    private int nextVehicleNo = 1;
    private int nextCustomerNo = 1;
    private int nextRentalNo = 1;

    public RentalService() {
        addVehicle("car", "Toyota", "Innova", 2500);
        addVehicle("car", "Honda", "City", 1800);
        addVehicle("car", "Maruti", "Swift", 1200);
        addVehicle("bike", "Royal Enfield", "Classic350", 800);
        addVehicle("bike", "Honda", "Activa", 400);
        addCustomer("Rahul Sharma", "9876543210");
        addCustomer("Priya Patel", "9123456780");
    }

    public synchronized Vehicle addVehicle(String type, String brand, String model, double rate) {
        requireText(type, "Type");
        requireText(brand, "Brand");
        requireText(model, "Model");
        if (rate <= 0) throw new IllegalArgumentException("Rate per day must be greater than 0.");

        String id = String.format("V%03d", nextVehicleNo);
        Vehicle v = switch (type.trim().toLowerCase()) {
            case "car" -> new Car(id, brand.trim(), model.trim(), rate);
            case "bike" -> new Bike(id, brand.trim(), model.trim(), rate);
            default -> throw new IllegalArgumentException("Type must be 'car' or 'bike'.");
        };
        nextVehicleNo++;
        vehicles.add(v);
        return v;
    }

    public synchronized Customer addCustomer(String name, String phone) {
        requireText(name, "Name");
        requireText(phone, "Phone");
        String id = String.format("C%03d", nextCustomerNo++);
        Customer c = new Customer(id, name.trim(), phone.trim());
        customers.add(c);
        return c;
    }

    public synchronized Rental rentVehicle(String customerId, String vehicleId, int days) {
        Customer c = findCustomer(customerId);
        if (c == null) throw new IllegalArgumentException("Customer not found.");
        Vehicle v = findVehicle(vehicleId);
        if (v == null) throw new IllegalArgumentException("Vehicle not found.");
        if (!v.isAvailable()) throw new IllegalStateException("Vehicle is already rented.");
        if (days <= 0) throw new IllegalArgumentException("Days must be at least 1.");

        Rental r = new Rental(String.format("R%03d", nextRentalNo++), c, v, days);
        v.setAvailable(false);
        rentals.add(r);
        return r;
    }

    public synchronized Rental returnVehicle(String rentalId) {
        Rental r = findRental(rentalId);
        if (r == null) throw new IllegalArgumentException("Rental not found.");
        if (r.isReturned()) throw new IllegalStateException("Vehicle already returned.");
        r.markReturned();
        r.getVehicle().setAvailable(true);
        return r;
    }

    public synchronized List<Vehicle> getVehicles() { return List.copyOf(vehicles); }
    public synchronized List<Customer> getCustomers() { return List.copyOf(customers); }
    public synchronized List<Rental> getRentals() { return List.copyOf(rentals); }

    public synchronized List<Vehicle> getAvailableVehicles() {
        return vehicles.stream().filter(Vehicle::isAvailable).toList();
    }

    public synchronized List<Rental> getActiveRentals() {
        return rentals.stream().filter(r -> !r.isReturned()).toList();
    }

    private Vehicle findVehicle(String id) {
        return vehicles.stream().filter(v -> v.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    private Customer findCustomer(String id) {
        return customers.stream().filter(c -> c.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    private Rental findRental(String id) {
        return rentals.stream().filter(r -> r.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required.");
    }
}
