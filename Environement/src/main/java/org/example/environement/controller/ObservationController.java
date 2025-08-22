package org.example.environement.controller;

import org.example.environement.dto.observation.ObservationDtoReceive;
import org.example.environement.dto.observation.ObservationDtoResponse;
import org.example.environement.service.ObservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/observation")
public class ObservationController {

    private final ObservationService observationService;

    public ObservationController(ObservationService observationService) {
        this.observationService = observationService;
    }

    // GET /observation → Toutes les observations (avec pagination possible)
    @GetMapping
    public ResponseEntity<List<ObservationDtoResponse>> getAllObservations(
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "0") int pageNumber) {
        return ResponseEntity.ok(observationService.get(pageSize, pageNumber));
    }

    // POST /observation → Ajouter une observation
    @PostMapping
    public ResponseEntity<ObservationDtoResponse> createObservation(
            @RequestBody ObservationDtoReceive observation) {
        return ResponseEntity.ok(observationService.create(observation));
    }

    // GET /observation/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ObservationDtoResponse> getObservation(@PathVariable long id) {
        return ResponseEntity.ok(observationService.get(id));
    }

    // GET /observation/by-location?location=Paris → Filtrer par lieu
    @GetMapping("/by-location")
    public ResponseEntity<List<ObservationDtoResponse>> getByLocation(
            @RequestParam String location) {
        return ResponseEntity.ok(observationService.getByLocation(location));
    }

    // GET /observation/by-species/{speciesId} → Filtrer par espèce
    @GetMapping("/by-species/{speciesId}")
    public ResponseEntity<List<ObservationDtoResponse>> getBySpecie(
            @PathVariable long speciesId) {
        return ResponseEntity.ok(observationService.getBySpecie(speciesId));
    }
}
