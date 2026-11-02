public class Bike extends Vehicle {
    public Bike(String id, String brand, String model, double ratePerDay) {
        super(id, brand, model, ratePerDay);
    }

    @Override
    public String getType() { return "Bike"; }
}
