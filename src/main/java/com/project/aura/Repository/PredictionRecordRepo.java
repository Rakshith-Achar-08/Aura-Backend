package com.project.aura.Repository;

import com.project.aura.Entity.PredictionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PredictionRecordRepo extends JpaRepository<PredictionRecord, Integer> {

    // Using Spring Data underscore notation: User field → User.userid field
    List<PredictionRecord> findByUser_UseridOrderByCreatedAtDesc(Integer userid);
}
