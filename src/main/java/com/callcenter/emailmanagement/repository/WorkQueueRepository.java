package com.callcenter.emailmanagement.repository;

import com.callcenter.emailmanagement.domain.model.WorkQueue;
import com.callcenter.emailmanagement.domain.model.WorkQueueType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkQueueRepository extends JpaRepository<WorkQueue, Long> {
    
    List<WorkQueue> findByQueueTypeAndStatus(WorkQueueType queueType, WorkQueue.QueueStatus status);
    
    @Query("SELECT w FROM WorkQueue w WHERE w.queueType = :queueType AND w.status = 'PENDING' ORDER BY w.priorityScore DESC, w.addedToQueueTime ASC")
    List<WorkQueue> findPendingByQueueTypeOrderedByPriority(@Param("queueType") WorkQueueType queueType);
    
    @Query(value = "SELECT * FROM work_queue w WHERE w.queue_type = :queueType AND w.status = 'PENDING' ORDER BY w.priority_score DESC, w.added_to_queue_time ASC LIMIT 1", nativeQuery = true)
    Optional<WorkQueue> findNextPendingByQueueType(@Param("queueType") String queueType);
    
    long countByQueueTypeAndStatus(WorkQueueType queueType, WorkQueue.QueueStatus status);
}