package com.project.aura.Repository;

import com.project.aura.Entity.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SymptomRepo extends JpaRepository<Symptom, Integer> {

    Optional<Symptom> findByName(String name);

    boolean existsByName(String name);

    List<Symptom> findByNameContainingIgnoreCase(String keyword);
}
