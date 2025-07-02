package com.callcenter.emailmanagement.repository;

import com.callcenter.emailmanagement.domain.model.SlaTracking;
import com.callcenter.emailmanagement.domain.model.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SlaTrackingRepository extends JpaRepository<SlaTracking, Long> {
    
    Optional<SlaTracking> findByCaseEntity(Case caseEntity);
    
    @Query("SELECT s FROM SlaTracking s WHERE s.slaStatus = 'APPROACHING_BREACH'")
    List<SlaTracking> findApproachingBreach();
    
    @Query("SELECT s FROM SlaTracking s WHERE s.slaStatus = 'BREACHED'")
    List<SlaTracking> findBreached();
    
    @Query("SELECT s FROM SlaTracking s WHERE s.firstResponseSlaMet = true")
    List<SlaTracking> findFirstResponseSlaCompliant();
    
    @Query("SELECT s FROM SlaTracking s WHERE s.resolutionSlaMet = true")
    List<SlaTracking> findResolutionSlaCompliant();
}