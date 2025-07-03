package com.example.demoSQL.repository;

import com.example.demoSQL.entity.PeriodicalReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodicalReportRepository extends JpaRepository<PeriodicalReport, Long> {
}
