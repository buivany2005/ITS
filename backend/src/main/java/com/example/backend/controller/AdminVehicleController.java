package com.example.backend.controller;

import com.example.backend.entity.Vehicle;
import com.example.backend.entity.Vehicle.VehicleStatus;
import com.example.backend.entity.Vehicle.VehicleType;
import com.example.backend.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/vehicles")
@CrossOrigin(origins = "*")
public class AdminVehicleController {

    @Autowired
    private VehicleRepository vehicleRepository;

    @PostMapping
    public ResponseEntity<?> createVehicle(@RequestBody Map<String, Object> body) {
        try {
            Vehicle v = mapToVehicle(new Vehicle(), body);
            Vehicle saved = vehicleRepository.save(v);
            Map<String, Object> res = new HashMap<>();
            res.put("id", saved.getId());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVehicle(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Optional<Vehicle> opt = vehicleRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        try {
            Vehicle v = mapToVehicle(opt.get(), body);
            Vehicle saved = vehicleRepository.save(v);
            return ResponseEntity.ok(Map.of("id", saved.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Optional<Vehicle> opt = vehicleRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Object s = body.get("status");
        if (s == null) return ResponseEntity.badRequest().body(Map.of("error", "Missing status"));
        try {
            Vehicle v = opt.get();
            v.setStatus(Vehicle.VehicleStatus.valueOf(s.toString().toUpperCase()));
            vehicleRepository.save(v);
            return ResponseEntity.ok(Map.of("status", v.getStatus().name()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid status"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long id) {
        if (!vehicleRepository.existsById(id)) return ResponseEntity.notFound().build();
        vehicleRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) String vehicleType,
                                    @RequestParam(required = false) String status) {
        try {
            // reuse repository queries
            if (vehicleType != null && !vehicleType.isEmpty()) {
                VehicleType vt = VehicleType.valueOf(vehicleType.toUpperCase());
                var list = vehicleRepository.findByVehicleType(vt);
                return ResponseEntity.ok(list);
            }
            return ResponseEntity.ok(vehicleRepository.findAll());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid vehicleType"));
        }
    }

    private Vehicle mapToVehicle(Vehicle v, Map<String, Object> body) {
        if (body.containsKey("name")) v.setName((String) body.get("name"));
        if (body.containsKey("type")) {
            String t = String.valueOf(body.get("type"));
            v.setVehicleType(VehicleType.valueOf(t.toUpperCase()));
        } else if (body.containsKey("vehicleType")) {
            String t = String.valueOf(body.get("vehicleType"));
            v.setVehicleType(VehicleType.valueOf(t.toUpperCase()));
        }
        if (body.containsKey("brand")) v.setBrand((String) body.get("brand"));
        if (body.containsKey("model")) v.setModel((String) body.get("model"));
        if (body.containsKey("year")) v.setYear((body.get("year") instanceof Number) ? ((Number) body.get("year")).intValue() : Integer.parseInt(body.get("year").toString()));
        if (body.containsKey("licensePlate")) v.setLicensePlate((String) body.get("licensePlate"));
        if (body.containsKey("color")) v.setColor((String) body.get("color"));
        if (body.containsKey("seats")) v.setSeats((body.get("seats") instanceof Number) ? ((Number) body.get("seats")).intValue() : Integer.parseInt(body.get("seats").toString()));
        if (body.containsKey("transmission")) v.setTransmission((String) body.get("transmission"));
        if (body.containsKey("fuelType")) v.setFuelType((String) body.get("fuelType"));
        if (body.containsKey("pricePerDay")) {
            Object p = body.get("pricePerDay");
            BigDecimal bd = p instanceof Number ? BigDecimal.valueOf(((Number) p).doubleValue()) : new BigDecimal(p.toString());
            v.setPricePerDay(bd);
        }
        if (body.containsKey("description")) v.setDescription((String) body.get("description"));
        if (body.containsKey("imageUrl")) v.setImageUrl((String) body.get("imageUrl"));
        return v;
    }
}
