package com.election.monitoring.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an election event for hierarchical logging
 */
public class ElectionEvent implements Comparable<ElectionEvent> {
    public enum EventType {
        STATION_ADDED("Station Added"),
        STATION_UPDATED("Station Updated"),
        STATION_REMOVED("Station Removed"),
        INCIDENT_REPORTED("Incident Reported"),
        INCIDENT_PROCESSED("Incident Processed"),
        INCIDENT_RESOLVED("Incident Resolved"),
        SYSTEM_ALERT("System Alert"),
        AUDIT_LOG("Audit Log"),
        USER_ACTION("User Action"),
        SYSTEM_START("System Start"),
        SYSTEM_SHUTDOWN("System Shutdown");
        
        private final String description;
        
        EventType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    public enum Priority {
        LOW(1),
        NORMAL(2),  
        HIGH(3),
        CRITICAL(4);
        
        private final int level;
        
        Priority(int level) {
            this.level = level;
        }
        
        public int getLevel() {
            return level;
        }
    }
    
    private String id;
    private LocalDateTime timestamp;
    private EventType type;
    private String stationId;
    private String description;
    private Priority priority;
    private String userId;
    private String source;
    
    public ElectionEvent(LocalDateTime timestamp, EventType type, String stationId, String description) {
        this.id = generateEventId();
        this.timestamp = timestamp;
        this.type = type;
        this.stationId = stationId;
        this.description = description;
        this.priority = determinePriority(type);
        this.userId = "system";
        this.source = "ElectionMonitoringSystem";
    }
    
    public ElectionEvent(LocalDateTime timestamp, EventType type, String stationId, String description, String userId) {
        this(timestamp, type, stationId, description);
        this.userId = userId;
    }
    
    private String generateEventId() {
        return "EVT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private Priority determinePriority(EventType type) {
        switch (type) {
            case INCIDENT_REPORTED:
            case SYSTEM_ALERT:
                return Priority.HIGH;
            case INCIDENT_PROCESSED:
            case INCIDENT_RESOLVED:
                return Priority.NORMAL;
            case STATION_ADDED:
            case STATION_UPDATED:
            case USER_ACTION:
                return Priority.NORMAL;
            case SYSTEM_START:
            case SYSTEM_SHUTDOWN:
                return Priority.CRITICAL;
            default:
                return Priority.LOW;
        }
    }
    
    // Getters
    public String getId() { return id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public EventType getType() { return type; }
    public String getStationId() { return stationId; }
    public String getDescription() { return description; }
    public Priority getPriority() { return priority; }
    public String getUserId() { return userId; }
    public String getSource() { return source; }
    
    // Setters
    public void setPriority(Priority priority) { this.priority = priority; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setSource(String source) { this.source = source; }
    
    // Business methods
    public boolean isHighPriority() {
        return priority == Priority.HIGH || priority == Priority.CRITICAL;
    }
    
    public boolean isCritical() {
        return priority == Priority.CRITICAL;
    }
    
    public long getAgeInMinutes() {
        return java.time.Duration.between(timestamp, LocalDateTime.now()).toMinutes();
    }
    
    @Override
    public int compareTo(ElectionEvent other) {
        // Compare by timestamp (most recent first)
        return other.timestamp.compareTo(this.timestamp);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ElectionEvent that = (ElectionEvent) obj;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss");
        return String.format("[%s] %s - %s at %s: %s (%s)",
            timestamp.format(formatter),
            type.getDescription(),
            stationId != null ? stationId : "SYSTEM",
            priority,
            description,
            userId
        );
    }
    
    public String toDetailedString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("""
            Event Details:
            ID: %s
            Type: %s
            Timestamp: %s
            Station: %s
            Priority: %s
            Description: %s
            User: %s
            Source: %s
            Age: %d minutes
            """,
            id, type.getDescription(), timestamp.format(formatter),
            stationId != null ? stationId : "N/A", priority,
            description, userId, source, getAgeInMinutes()
        );
    }
}