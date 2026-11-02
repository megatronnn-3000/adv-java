import java.util.ArrayList;
import java.util.List;

public class RentalService {
    private final List<Vehicle> vehicles = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Rental> rentals = new ArrayList<>();

    private int nextVehicleNo = 1;
    private int nextCustomerNo = 1;
    private int nextRentalNo = 1;

    public Vehicle addVehicle(String type, String brand, String model, double rate) {
        String id = String.format("V%03d", nextVehicleNo++);
        Vehicle v = type.equalsIgnoreCase("car")
                ? new Car(id, brand, model, rate)
                : new Bike(id, brand, model, rate);
        vehicles.add(v);
        return v;
    }

    public Customer addCustomer(String name, String phone) {
        String id = String.format("C%03d", nextCustomerNo++);
        Customer c = new Customer(id, name, phone);
        customers.add(c);
        return c;
    }

    public Rental rentVehicle(String customerId, String vehicleId, int days) {
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

    public Rental returnVehicle(String rentalId) {
        Rental r = findRental(rentalId);
        if (r == null) throw new IllegalArgumentException("Rental not found.");
        if (r.isReturned()) throw new IllegalStateException("Vehicle already returned.");
        r.markReturned();
        r.getVehicle().setAvailable(true);
        return r;
    }

    public List<Vehicle> getVehicles() { return vehicles; }
    public List<Customer> getCustomers() { return customers; }
    public List<Rental> getRentals() { return rentals; }

    public List<Vehicle> getAvailableVehicles() {
        return vehicles.stream().filter(Vehicle::isAvailable).toList();
    }

    public List<Rental> getActiveRentals() {
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
}
