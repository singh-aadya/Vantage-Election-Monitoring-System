package com.election.monitoring.core;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Queue-based incident processing system with priority handling
 * Demonstrates various queue operations and algorithms
 */
public class IncidentProcessor {
    private PriorityQueue<Incident> incidentQueue;
    private List<Incident> processedIncidents;
    private Map<String, List<Incident>> stationIncidents;
    private Queue<Incident> recentIncidents;
    private final int MAX_RECENT_INCIDENTS = 50;
    
    public IncidentProcessor() {
        // Priority queue orders incidents by severity and time
        this.incidentQueue = new PriorityQueue<>();
        this.processedIncidents = new ArrayList<>();
        this.stationIncidents = new HashMap<>();
        this.recentIncidents = new LinkedList<>();
    }
    
    /**
     * Report a new incident - adds to priority queue
     */
    public void reportIncident(Incident incident) {
        incidentQueue.offer(incident);
        
        // Add to station-specific tracking
        stationIncidents.computeIfAbsent(incident.getStationId(), k -> new ArrayList<>()).add(incident);
        
        // Add to recent incidents queue (FIFO)
        recentIncidents.offer(incident);
        if (recentIncidents.size() > MAX_RECENT_INCIDENTS) {
            recentIncidents.poll(); // Remove oldest
        }
        
        System.out.println("Incident reported: " + incident.getId() + " (Queue size: " + incidentQueue.size() + ")");
    }
    
    /**
     * Process all incidents in the queue
     */
    public List<Incident> processIncidents() {
        List<Incident> processed = new ArrayList<>();
        
        while (!incidentQueue.isEmpty()) {
            Incident incident = incidentQueue.poll();
            processIncident(incident);
            processed.add(incident);
            processedIncidents.add(incident);
        }
        
        return processed;
    }
    
    /**
     * Process a single incident
     */
    private void processIncident(Incident incident) {
        incident.setStatus(Incident.Status.IN_PROGRESS);
        
        // Simulate processing logic based on severity
        switch (incident.getSeverity()) {
            case CRITICAL:
                incident.setStatus(Incident.Status.ESCALATED);
                break;
            case HIGH:
                // High priority incidents get immediate attention
                incident.setStatus(Incident.Status.RESOLVED);
                break;
            case MEDIUM:
                // Medium priority incidents are processed normally
                incident.setStatus(Incident.Status.RESOLVED);
                break;
            case LOW:
                // Low priority incidents might be batched
                incident.setStatus(Incident.Status.RESOLVED);
                break;
        }
    }
    
    /**
     * Process next high-priority incident only
     */
    public Incident processNextHighPriorityIncident() {
        if (incidentQueue.isEmpty()) {
            return null;
        }
        
        // Check if the next incident is high priority
        Incident nextIncident = incidentQueue.peek();
        if (nextIncident != null && nextIncident.isHighPriority()) {
            Incident incident = incidentQueue.poll();
            processIncident(incident);
            processedIncidents.add(incident);
            return incident;
        }
        
        return null;
    }
    
    /**
     * Get incidents by station ID
     */
    public List<Incident> getIncidentsByStation(String stationId) {
        return stationIncidents.getOrDefault(stationId, new ArrayList<>());
    }
    
    /**
     * Get critical incidents that need immediate attention
     */
    public List<Incident> getCriticalIncidents() {
        return processedIncidents.stream()
            .filter(Incident::isCritical)
            .collect(Collectors.toList());
    }
    
    /**
     * Get unresolved incidents
     */
    public List<Incident> getUnresolvedIncidents() {
        return processedIncidents.stream()
            .filter(incident -> !incident.isResolved())
            .collect(Collectors.toList());
    }
    
    /**
     * Get recent incidents (last 24 hours)
     */
    public List<Incident> getRecentIncidents() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        return processedIncidents.stream()
            .filter(incident -> incident.getReportedAt().isAfter(yesterday))
            .collect(Collectors.toList());
    }
    
    /**
     * Find suspicious activity patterns
     * Returns stations with multiple incidents in a short time frame
     */
    public Map<String, List<Incident>> findSuspiciousActivity() {
        Map<String, List<Incident>> suspiciousStations = new HashMap<>();
        LocalDateTime recentThreshold = LocalDateTime.now().minusHours(4);
        
        for (Map.Entry<String, List<Incident>> entry : stationIncidents.entrySet()) {
            String stationId = entry.getKey();
            List<Incident> incidents = entry.getValue();
            
            // Find incidents in the last 4 hours
            List<Incident> recentStationIncidents = incidents.stream()
                .filter(incident -> incident.getReportedAt().isAfter(recentThreshold))
                .collect(Collectors.toList());
            
            // Flag stations with 3 or more incidents in 4 hours as suspicious
            if (recentStationIncidents.size() >= 3) {
                suspiciousStations.put(stationId, recentStationIncidents);
            }
        }
        
        return suspiciousStations;
    }
    
    /**
     * Get incident statistics by type
     */
    public Map<Incident.IncidentType, Integer> getIncidentStatistics() {
        Map<Incident.IncidentType, Integer> stats = new HashMap<>();
        
        for (Incident.IncidentType type : Incident.IncidentType.values()) {
            stats.put(type, 0);
        }
        
        for (Incident incident : processedIncidents) {
            stats.put(incident.getType(), stats.get(incident.getType()) + 1);
        }
        
        // Also count pending incidents
        for (Incident incident : incidentQueue) {
            stats.put(incident.getType(), stats.get(incident.getType()) + 1);
        }
        
        return stats;
    }
    
    /**
     * Get processing queue statistics
     */
    public QueueStats getQueueStats() {
        int pending = incidentQueue.size();
        int processed = processedIncidents.size();
        int critical = (int) incidentQueue.stream().filter(Incident::isCritical).count();
        int high = (int) incidentQueue.stream().filter(Incident::isHighPriority).count();
        
        return new QueueStats(pending, processed, critical, high);
    }
    
    /**
     * Clear all processed incidents (maintenance operation)
     */
    public int clearProcessedIncidents() {
        int count = processedIncidents.size();
        processedIncidents.clear();
        return count;
    }
    
    /**
     * Get top stations by incident count
     */
    public List<Map.Entry<String, Integer>> getTopIncidentStations(int limit) {
        return stationIncidents.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().size()
            ))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    /**
     * Display current queue status
     */
    public void displayQueueStatus() {
        System.out.println("=== Incident Queue Status ===");
        System.out.println("Pending Incidents: " + incidentQueue.size());
        
        if (!incidentQueue.isEmpty()) {
            System.out.println("Next incident to process:");
            System.out.println("  " + incidentQueue.peek());
            
            // Show severity distribution in queue
            Map<Incident.Severity, Long> severityCount = incidentQueue.stream()
                .collect(Collectors.groupingBy(Incident::getSeverity, Collectors.counting()));
            
            System.out.println("Queue by severity:");
            for (Incident.Severity severity : Incident.Severity.values()) {
                long count = severityCount.getOrDefault(severity, 0L);
                System.out.println("  " + severity + ": " + count);
            }
        }
        
        System.out.println("Total Processed: " + processedIncidents.size());
        System.out.println();
    }
    
    /**
     * Queue statistics class
     */
    public static class QueueStats {
        public final int pendingIncidents;
        public final int processedIncidents;
        public final int criticalInQueue;
        public final int highPriorityInQueue;
        
        QueueStats(int pending, int processed, int critical, int high) {
            this.pendingIncidents = pending;
            this.processedIncidents = processed;
            this.criticalInQueue = critical;
            this.highPriorityInQueue = high;
        }
        
        @Override
        public String toString() {
            return String.format("QueueStats{pending=%d, processed=%d, critical=%d, high=%d}",
                pendingIncidents, processedIncidents, criticalInQueue, highPriorityInQueue);
        }
    }
}