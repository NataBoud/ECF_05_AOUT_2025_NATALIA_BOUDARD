package org.example.environement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.environement.dto.travellogs.TravellogDtoResponse;
import org.example.environement.entity.enums.TravelMode;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Travellog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // plusieurs Travellogs peuvent être liés à une Observation
    @JoinColumn(name = "observation_id", nullable = false)
    private Observation observation;

    @Column(nullable = false)
    private Double distanceKm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TravelMode mode;

    @Column(nullable = false)
    private Double estimatedCo2Kg;

    public TravellogDtoResponse entityToDto() {
        return TravellogDtoResponse.builder()
                .id(this.getId())
                .distanceKm(this.getDistanceKm())
                .mode(this.getMode().name()) // TravelMode enum converti en String
                .estimatedCo2Kg(this.getEstimatedCo2Kg())
                .build();
    }

    public void calculateCO2() {
        double factor;

        switch (this.mode) {
            case WALKING, BIKE -> factor = 0.0;
            case CAR -> factor = 0.22;
            case BUS -> factor = 0.11;
            case TRAIN -> factor = 0.03;
            case PLANE -> factor = 0.259;
            default -> factor = 0.0;
        }

        if (this.distanceKm != null) {
            this.estimatedCo2Kg = this.distanceKm * factor;
        } else {
            this.estimatedCo2Kg = 0.0;
        }
    }
}
