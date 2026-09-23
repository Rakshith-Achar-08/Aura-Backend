package com.project.aura.Repository;

import com.project.aura.Entity.Disease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiseaseRepo extends JpaRepository<Disease, Integer> {

    Optional<Disease> findByName(String name);

    boolean existsByName(String name);
}
