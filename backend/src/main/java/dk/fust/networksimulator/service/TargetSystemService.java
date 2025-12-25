package dk.fust.networksimulator.service;

import dk.fust.networksimulator.model.TargetSystem;
import dk.fust.networksimulator.repository.TargetSystemRepository;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TargetSystemService {

    private TargetSystemRepository targetSystemRepository;

    public List<TargetSystem> getAllTargetSystems() {
        return targetSystemRepository.findAll();
    }

    public Optional<TargetSystem> getTargetSystemById(Long id) {
        return targetSystemRepository.findById(id);
    }

    public Optional<TargetSystem> getTargetSystemByName(String systemName) {
        return targetSystemRepository.findBySystemName(systemName);
    }

    public TargetSystem createTargetSystem(TargetSystem targetSystem) {
        return targetSystemRepository.save(targetSystem);
    }

    public TargetSystem updateTargetSystem(Long id, TargetSystem targetSystemDetails) {
        TargetSystem targetSystem = targetSystemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TargetSystem not found with id: " + id));
        targetSystem.setSystemName(targetSystemDetails.getSystemName());
        targetSystem.setTargetBaseUrl(targetSystemDetails.getTargetBaseUrl());
        targetSystem.setTimeoutMs(targetSystemDetails.getTimeoutMs());
        targetSystem.setFollowRedirect(targetSystemDetails.isFollowRedirect());
        return targetSystemRepository.save(targetSystem);
    }

    public void deleteTargetSystem(Long id) {
        targetSystemRepository.deleteById(id);
    }
}
