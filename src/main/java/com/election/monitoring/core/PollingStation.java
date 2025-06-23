package com.election.monitoring.core;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a polling station in the election monitoring system
 */
public class PollingStation {
    private String id;
    private String name;
    private String address;
    private int capacity;
    private LocalDateTime createdAt;
    private int totalVoters;
    private boolean isActive;
    
    public PollingStation(String id, String name, String address, int capacity) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.createdAt = LocalDateTime.now();
        this.totalVoters = 0;
        this.isActive = true;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public int getCapacity() { return capacity; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public int getTotalVoters() { return totalVoters; }
    public boolean isActive() { return isActive; }
    
    // Setters
    public void setTotalVoters(int totalVoters) { this.totalVoters = totalVoters; }
    public void setActive(boolean active) { this.isActive = active; }
    
    // Business methods
    public double getUtilizationRate() {
        return capacity > 0 ? (double) totalVoters / capacity : 0.0;
    }
    
    public boolean isOverCapacity() {
        return totalVoters > capacity;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PollingStation that = (PollingStation) obj;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("PollingStation{id='%s', name='%s', address='%s', capacity=%d, voters=%d, active=%s, utilization=%.1f%%}",
            id, name, address, capacity, totalVoters, isActive, getUtilizationRate() * 100);
    }
}