package com.election.monitoring.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an election incident with various types and severity levels
 */
public class Incident implements Comparable<Incident> {
    public enum IncidentType {
        TECHNICAL("Technical Issue"),
        SECURITY("Security Concern"),
        PROCEDURAL("Procedural Violation"),
        OTHER("Other Issue");
        
        private final String description;
        
        IncidentType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    public enum Severity {
        LOW(1, "Low Priority"),
        MEDIUM(2, "Medium Priority"),
        HIGH(3, "High Priority"),
        CRITICAL(4, "Critical - Immediate Action Required");
        
        private final int priority;
        private final String description;
        
        Severity(int priority, String description) {
            this.priority = priority;
            this.description = description;
        }
        
        public int getPriority() {
            return priority;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    public enum Status {
        REPORTED("Reported"),
        IN_PROGRESS("In Progress"),
        RESOLVED("Resolved"),
        ESCALATED("Escalated");
        
        private final String description;
        
        Status(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    private String id;
    private String stationId;
    private IncidentType type;
    private String description;
    private Severity severity;
    private Status status;
    private LocalDateTime reportedAt;
    private LocalDateTime updatedAt;
    private String reportedBy;
    
    public Incident(String stationId, IncidentType type, String description, Severity severity) {
        this.id = generateIncidentId();
        this.stationId = stationId;
        this.type = type;
        this.description = description;
        this.severity = severity;
        this.status = Status.REPORTED;
        this.reportedAt = LocalDateTime.now();
        this.updatedAt = this.reportedAt;
        this.reportedBy = "System";
    }
    
    public Incident(String stationId, IncidentType type, String description, Severity severity, String reportedBy) {
        this(stationId, type, description, severity);
        this.reportedBy = reportedBy;
    }
    
    private String generateIncidentId() {
        return "INC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    // Getters
    public String getId() { return id; }
    public String getStationId() { return stationId; }
    public IncidentType getType() { return type; }
    public String getDescription() { return description; }
    public Severity getSeverity() { return severity; }
    public Status getStatus() { return status; }
    public LocalDateTime getReportedAt() { return reportedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getReportedBy() { return reportedBy; }
    
    // Setters
    public void setStatus(Status status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setSeverity(Severity severity) {
        this.severity = severity;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updateDescription(String newDescription) {
        this.description = newDescription;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Business methods
    public boolean isCritical() {
        return severity == Severity.CRITICAL;
    }
    
    public boolean isHighPriority() {
        return severity == Severity.HIGH || severity == Severity.CRITICAL;
    }
    
    public boolean isResolved() {
        return status == Status.RESOLVED;
    }
    
    public long getAgeInMinutes() {
        return java.time.Duration.between(reportedAt, LocalDateTime.now()).toMinutes();
    }
    
    @Override
    public int compareTo(Incident other) {
        // Compare by severity (higher priority first), then by time (older first)
        int severityComparison = Integer.compare(other.severity.getPriority(), this.severity.getPriority());
        if (severityComparison != 0) {
            return severityComparison;
        }
        return this.reportedAt.compareTo(other.reportedAt);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Incident incident = (Incident) obj;
        return Objects.equals(id, incident.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
        return String.format("[%s] %s at %s - %s (%s) - %s [%s] - Age: %d min",
            id,
            type.getDescription(),
            stationId,
            severity.getDescription(),
            status.getDescription(),
            description,
            reportedAt.format(formatter),
            getAgeInMinutes()
        );
    }
    
    public String toDetailedString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("""
            Incident Details:
            ID: %s
            Station: %s
            Type: %s
            Severity: %s
            Status: %s
            Description: %s
            Reported By: %s
            Reported At: %s
            Last Updated: %s
            Age: %d minutes
            """,
            id, stationId, type.getDescription(), severity.getDescription(),
            status.getDescription(), description, reportedBy,
            reportedAt.format(formatter), updatedAt.format(formatter),
            getAgeInMinutes()
        );
    }
}