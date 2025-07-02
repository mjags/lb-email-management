package com.callcenter.emailmanagement.controller;

import com.callcenter.emailmanagement.domain.model.WorkQueueType;
import com.callcenter.emailmanagement.service.WorkQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/queues")
@CrossOrigin(origins = "*")
public class QueueController {
    
    @Autowired
    private WorkQueueService workQueueService;
    
    @GetMapping("/{queueType}/depth")
    public ResponseEntity<Integer> getQueueDepth(@PathVariable WorkQueueType queueType) {
        int depth = workQueueService.getQueueDepth(queueType);
        return ResponseEntity.ok(depth);
    }
    
    @GetMapping("/{queueType}/metrics")
    public ResponseEntity<WorkQueueService.WorkQueueMetrics> getQueueMetrics(
            @PathVariable WorkQueueType queueType) {
        WorkQueueService.WorkQueueMetrics metrics = workQueueService.getQueueMetrics(queueType);
        return ResponseEntity.ok(metrics);
    }
    
    @GetMapping("/{queueType}/wait-time")
    public ResponseEntity<Double> getAverageWaitTime(@PathVariable WorkQueueType queueType) {
        double waitTime = workQueueService.getAverageWaitTime(queueType);
        return ResponseEntity.ok(waitTime);
    }
    
    @PostMapping("/redistribute")
    public ResponseEntity<String> redistributeCases() {
        workQueueService.redistributeUnassignedCases();
        return ResponseEntity.ok("Cases redistributed successfully");
    }
}