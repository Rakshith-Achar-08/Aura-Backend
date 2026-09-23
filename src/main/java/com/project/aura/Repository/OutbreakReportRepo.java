package com.project.aura.Repository;

import com.project.aura.Entity.OutbreakReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutbreakReportRepo extends JpaRepository<OutbreakReport, Integer> {

    List<OutbreakReport> findByDisease_DiseaseIdOrderByReportDateDesc(Integer diseaseId);

    List<OutbreakReport> findByHospital_HospitalIdOrderByReportDateDesc(Integer hospitalId);

    List<OutbreakReport> findByReportedCasesGreaterThanEqualOrderByReportDateDesc(Integer threshold);

    List<OutbreakReport> findAllByOrderByReportDateDesc();
}
