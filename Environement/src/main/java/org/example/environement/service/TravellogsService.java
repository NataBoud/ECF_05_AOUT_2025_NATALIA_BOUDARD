package org.example.environement.service;

import org.example.environement.dto.travellogs.TravellogDtoResponse;
import org.example.environement.dto.travellogs.TravellogDtoStat;
import org.example.environement.entity.Observation;
import org.example.environement.entity.Travellog;
import org.example.environement.repository.ObservationRepository;
import org.example.environement.repository.TravellogRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TravellogsService {

    private final TravellogRepository travellogRepository;
    private final ObservationRepository observationRepository;

    public TravellogsService(TravellogRepository travellogRepository,
                             ObservationRepository observationRepository) {
        this.travellogRepository = travellogRepository;
        this.observationRepository = observationRepository;
    }

    private TravellogDtoStat calculateStats(List<Travellog> travellogs) {
        TravellogDtoStat stat = new TravellogDtoStat();

        for (Travellog t : travellogs) {
            t.calculateCO2();
            stat.addTotalDistanceKm(t.getDistanceKm());
            stat.addTotalEmissionsKg(t.getEstimatedCo2Kg());
            stat.addMode(t.getMode().name(), t.getDistanceKm());
        }

        stat.setNumberOfTravellogs(travellogs.size());
        return stat;
    }


    // Récupérer les 10 derniers trajets
    public List<TravellogDtoResponse> get(int limit) {
        List<Travellog> travellogs = (List<Travellog>) travellogRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        return travellogs.stream()
                .limit(limit)
                .map(travellog -> {
                    travellog.calculateCO2();
                    return travellog.entityToDto();
                })
                .collect(Collectors.toList());
    }

    // Statistiques pour une observation
    public TravellogDtoStat getStat(long observationId) {
        Observation observation = observationRepository.findById(observationId)
                .orElseThrow(() -> new RuntimeException("Observation not found"));

        return calculateStats(observation.getTravellogs());
    }

    // Statistiques pour un utilisateur sur le dernier mois
    public Map<String, TravellogDtoStat> getStatForUserLastMonth(String observerName) {
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);

        List<Observation> observations = observationRepository.findAll()
                .stream()
                .filter(o -> o.getObserverName().equalsIgnoreCase(observerName)
                        && !o.getObservationDate().isBefore(oneMonthAgo))
                .toList();

        Map<String, TravellogDtoStat> statsMap = new HashMap<>();
        for (Observation obs : observations) {
            statsMap.put(obs.getLocation(), calculateStats(obs.getTravellogs()));
        }

        return statsMap;
    }
}
