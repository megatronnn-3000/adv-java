package com.rental.controller;

import com.rental.model.Customer;
import com.rental.model.Rental;
import com.rental.model.Vehicle;
import com.rental.service.RentalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RentalController {
    private final RentalService service;

    public RentalController(RentalService service) {
        this.service = service;
    }

    public record VehicleRequest(String type, String brand, String model, double ratePerDay) {}
    public record CustomerRequest(String name, String phone) {}
    public record RentalRequest(String customerId, String vehicleId, int days) {}

    @GetMapping("/vehicles")
    public List<Vehicle> getVehicles(@RequestParam(defaultValue = "false") boolean available) {
        return available ? service.getAvailableVehicles() : service.getVehicles();
    }

    @PostMapping("/vehicles")
    @ResponseStatus(HttpStatus.CREATED)
    public Vehicle addVehicle(@RequestBody VehicleRequest req) {
        return service.addVehicle(req.type(), req.brand(), req.model(), req.ratePerDay());
    }

    @GetMapping("/customers")
    public List<Customer> getCustomers() {
        return service.getCustomers();
    }

    @PostMapping("/customers")
    @ResponseStatus(HttpStatus.CREATED)
    public Customer addCustomer(@RequestBody CustomerRequest req) {
        return service.addCustomer(req.name(), req.phone());
    }

    @GetMapping("/rentals")
    public List<Rental> getRentals(@RequestParam(defaultValue = "false") boolean active) {
        return active ? service.getActiveRentals() : service.getRentals();
    }

    @PostMapping("/rentals")
    @ResponseStatus(HttpStatus.CREATED)
    public Rental rentVehicle(@RequestBody RentalRequest req) {
        return service.rentVehicle(req.customerId(), req.vehicleId(), req.days());
    }

    @PostMapping("/rentals/{id}/return")
    public Rental returnVehicle(@PathVariable String id) {
        return service.returnVehicle(id);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleError(RuntimeException e) {
        return Map.of("error", e.getMessage());
    }
}
