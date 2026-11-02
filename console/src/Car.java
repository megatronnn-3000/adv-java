public class Car extends Vehicle {
    public Car(String id, String brand, String model, double ratePerDay) {
        super(id, brand, model, ratePerDay);
    }

    @Override
    public String getType() { return "Car"; }
}
