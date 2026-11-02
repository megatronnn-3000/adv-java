import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final RentalService service = new RentalService();

    public static void main(String[] args) {
        loadSampleData();

        while (true) {
            System.out.println("\n===== VEHICLE RENTAL SYSTEM =====");
            System.out.println("1. View all vehicles");
            System.out.println("2. View available vehicles");
            System.out.println("3. Add vehicle");
            System.out.println("4. Register customer");
            System.out.println("5. View customers");
            System.out.println("6. Rent a vehicle");
            System.out.println("7. Return a vehicle");
            System.out.println("8. View active rentals");
            System.out.println("9. View rental history");
            System.out.println("0. Exit");

            int choice = readInt("Enter choice: ");
            try {
                switch (choice) {
                    case 1 -> printVehicles(service.getVehicles());
                    case 2 -> printVehicles(service.getAvailableVehicles());
                    case 3 -> addVehicle();
                    case 4 -> registerCustomer();
                    case 5 -> printCustomers();
                    case 6 -> rentVehicle();
                    case 7 -> returnVehicle();
                    case 8 -> printRentals(service.getActiveRentals());
                    case 9 -> printRentals(service.getRentals());
                    case 0 -> {
                        System.out.println("Thank you for using the Vehicle Rental System!");
                        return;
                    }
                    default -> System.out.println("Invalid choice, try again.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void loadSampleData() {
        service.addVehicle("car", "Toyota", "Innova", 2500);
        service.addVehicle("car", "Honda", "City", 1800);
        service.addVehicle("car", "Maruti", "Swift", 1200);
        service.addVehicle("bike", "Royal Enfield", "Classic350", 800);
        service.addVehicle("bike", "Honda", "Activa", 400);
        service.addCustomer("Rahul Sharma", "9876543210");
        service.addCustomer("Priya Patel", "9123456780");
    }

    private static void addVehicle() {
        String type;
        while (true) {
            type = readLine("Type (car/bike): ").toLowerCase();
            if (type.equals("car") || type.equals("bike")) break;
            System.out.println("Please enter 'car' or 'bike'.");
        }
        String brand = readLine("Brand: ");
        String model = readLine("Model: ");
        double rate = readDouble("Rate per day (Rs.): ");
        Vehicle v = service.addVehicle(type, brand, model, rate);
        System.out.println("Vehicle added with ID " + v.getId());
    }

    private static void registerCustomer() {
        String name = readLine("Name: ");
        String phone = readLine("Phone: ");
        Customer c = service.addCustomer(name, phone);
        System.out.println("Customer registered with ID " + c.getId());
    }

    private static void rentVehicle() {
        printVehicles(service.getAvailableVehicles());
        if (service.getAvailableVehicles().isEmpty()) return;
        printCustomers();
        String customerId = readLine("Customer ID: ");
        String vehicleId = readLine("Vehicle ID: ");
        int days = readInt("Number of days: ");
        Rental r = service.rentVehicle(customerId, vehicleId, days);
        System.out.println("\n--- Rental Receipt ---");
        System.out.println("Rental ID : " + r.getId());
        System.out.println("Customer  : " + r.getCustomer().getName());
        System.out.println("Vehicle   : " + r.getVehicle().getBrand() + " " + r.getVehicle().getModel());
        System.out.println("Days      : " + r.getDays());
        System.out.printf("Total     : Rs.%.2f%n", r.getTotalCost());
    }

    private static void returnVehicle() {
        printRentals(service.getActiveRentals());
        if (service.getActiveRentals().isEmpty()) return;
        String rentalId = readLine("Rental ID to return: ");
        Rental r = service.returnVehicle(rentalId);
        System.out.println(r.getVehicle().getBrand() + " " + r.getVehicle().getModel()
                + " returned successfully. Thank you, " + r.getCustomer().getName() + "!");
    }

    private static void printVehicles(List<Vehicle> list) {
        if (list.isEmpty()) {
            System.out.println("No vehicles to show.");
            return;
        }
        System.out.printf("%-6s %-6s %-14s %-12s %-12s %s%n", "ID", "Type", "Brand", "Model", "Rate/Day", "Status");
        list.forEach(System.out::println);
    }

    private static void printCustomers() {
        if (service.getCustomers().isEmpty()) {
            System.out.println("No customers registered.");
            return;
        }
        System.out.printf("%-6s %-20s %s%n", "ID", "Name", "Phone");
        service.getCustomers().forEach(System.out::println);
    }

    private static void printRentals(List<Rental> list) {
        if (list.isEmpty()) {
            System.out.println("No rentals to show.");
            return;
        }
        list.forEach(System.out::println);
    }

    private static String readLine(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("Input cannot be empty.");
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readLine(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            try {
                double d = Double.parseDouble(readLine(prompt));
                if (d > 0) return d;
                System.out.println("Value must be greater than 0.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
