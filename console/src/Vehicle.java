public abstract class Vehicle {
    private final String id;
    private final String brand;
    private final String model;
    private final double ratePerDay;
    private boolean available = true;

    protected Vehicle(String id, String brand, String model, double ratePerDay) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.ratePerDay = ratePerDay;
    }

    public abstract String getType();

    public String getId() { return id; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public double getRatePerDay() { return ratePerDay; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return String.format("%-6s %-6s %-14s %-12s Rs.%-8.2f %s",
                id, getType(), brand, model, ratePerDay, available ? "Available" : "Rented");
    }
}
