public class Rental {
    private final String id;
    private final Customer customer;
    private final Vehicle vehicle;
    private final int days;
    private final double totalCost;
    private boolean returned = false;

    public Rental(String id, Customer customer, Vehicle vehicle, int days) {
        this.id = id;
        this.customer = customer;
        this.vehicle = vehicle;
        this.days = days;
        this.totalCost = days * vehicle.getRatePerDay();
    }

    public String getId() { return id; }
    public Customer getCustomer() { return customer; }
    public Vehicle getVehicle() { return vehicle; }
    public int getDays() { return days; }
    public double getTotalCost() { return totalCost; }
    public boolean isReturned() { return returned; }
    public void markReturned() { this.returned = true; }

    @Override
    public String toString() {
        return String.format("%-6s %-20s %-6s %-22s %3d day(s)  Rs.%-10.2f %s",
                id, customer.getName(), vehicle.getId(),
                vehicle.getBrand() + " " + vehicle.getModel(),
                days, totalCost, returned ? "Returned" : "Active");
    }
}
