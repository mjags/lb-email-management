package com.callcenter.emailmanagement.repository;

import com.callcenter.emailmanagement.domain.model.Agent;
import com.callcenter.emailmanagement.domain.model.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaseRepository extends JpaRepository<Case, Long> {
    
    Optional<Case> findByCaseNumber(String caseNumber);
    
    List<Case> findByStatus(Case.CaseStatus status);
    
    List<Case> findByAssignedAgent(Agent agent);
    
    List<Case> findByCustomerEmail(String customerEmail);
    
    @Query("SELECT c FROM Case c WHERE c.status NOT IN ('RESOLVED', 'CLOSED')")
    List<Case> findActiveCases();
    
    @Query("SELECT COUNT(c) FROM Case c WHERE c.status = 'NEW'")
    long countNewCases();
}