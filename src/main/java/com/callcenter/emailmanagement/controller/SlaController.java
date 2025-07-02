package com.callcenter.emailmanagement.controller;

import com.callcenter.emailmanagement.domain.model.Case;
import com.callcenter.emailmanagement.service.SlaTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sla")
@CrossOrigin(origins = "*")
public class SlaController {
    
    @Autowired
    private SlaTrackingService slaTrackingService;
    
    @GetMapping("/metrics")
    public ResponseEntity<SlaTrackingService.SlaMetrics> getSlaMetrics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        SlaTrackingService.SlaMetrics metrics = slaTrackingService.calculateSlaMetrics(startDate, endDate);
        return ResponseEntity.ok(metrics);
    }
    
    @GetMapping("/approaching-breach")
    public ResponseEntity<List<Case>> getCasesApproachingBreach() {
        List<Case> cases = slaTrackingService.getCasesApproachingSlaBreach();
        return ResponseEntity.ok(cases);
    }
    
    @GetMapping("/breached")
    public ResponseEntity<List<Case>> getBreachedCases() {
        List<Case> cases = slaTrackingService.getSlaBreachedCases();
        return ResponseEntity.ok(cases);
    }
    
    @PostMapping("/generate-report")
    public ResponseEntity<String> generateSlaReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        slaTrackingService.generateSlaReport(startDate, endDate);
        return ResponseEntity.ok("SLA report generation initiated");
    }
}