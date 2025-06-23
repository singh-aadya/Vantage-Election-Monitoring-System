package com.election.monitoring.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Tree-based hierarchical event management system
 * Uses a combination of BST for time-ordered events and hierarchical organization
 */
public class EventTreeManager {
    private EventTreeNode root;
    private Map<String, List<ElectionEvent>> stationEvents;
    private Map<ElectionEvent.EventType, List<ElectionEvent>> eventsByType;
    private int totalEvents;
    
    public EventTreeManager() {
        this.root = null;
        this.stationEvents = new HashMap<>();
        this.eventsByType = new HashMap<>();
        this.totalEvents = 0;
        
        // Initialize event type lists
        for (ElectionEvent.EventType type : ElectionEvent.EventType.values()) {
            eventsByType.put(type, new ArrayList<>());
        }
    }
    
    /**
     * Add an event to the tree structure
     */
    public void addEvent(ElectionEvent event) {
        root = insertEvent(root, event);
        
        // Add to station-specific tracking
        if (event.getStationId() != null) {
            stationEvents.computeIfAbsent(event.getStationId(), k -> new ArrayList<>()).add(event);
        }
        
        // Add to type-specific tracking
        eventsByType.get(event.getType()).add(event);
        
        totalEvents++;
    }
    
    /**
     * Insert event into BST based on timestamp
     */
    private EventTreeNode insertEvent(EventTreeNode node, ElectionEvent event) {
        if (node == null) {
            return new EventTreeNode(event);
        }
        
        if (event.getTimestamp().isBefore(node.event.getTimestamp())) {
            node.left = insertEvent(node.left, event);
        } else {
            node.right = insertEvent(node.right, event);
        }
        
        return node;
    }
    
    /**
     * Get events by station ID
     */
    public List<ElectionEvent> getEventsByStation(String stationId) {
        return stationEvents.getOrDefault(stationId, new ArrayList<>());
    }
    
    /**
     * Get events by type
     */
    public List<ElectionEvent> getEventsByType(ElectionEvent.EventType type) {
        return new ArrayList<>(eventsByType.get(type));
    }
    
    /**
     * Get events within a time range
     */
    public List<ElectionEvent> getEventsInTimeRange(LocalDateTime start, LocalDateTime end) {
        List<ElectionEvent> result = new ArrayList<>();
        searchTimeRange(root, start, end, result);
        return result;
    }
    
    /**
     * Search for events within time range using BST
     */
    private void searchTimeRange(EventTreeNode node, LocalDateTime start, LocalDateTime end, List<ElectionEvent> result) {
        if (node == null) {
            return;
        }
        
        LocalDateTime nodeTime = node.event.getTimestamp();
        
        // If current node is within range, add it
        if (!nodeTime.isBefore(start) && !nodeTime.isAfter(end)) {
            result.add(node.event);
        }
        
        // Search left subtree if start time is before current node
        if (start.isBefore(nodeTime)) {
            searchTimeRange(node.left, start, end, result);
        }
        
        // Search right subtree if end time is after current node
        if (end.isAfter(nodeTime)) {
            searchTimeRange(node.right, start, end, result);
        }
    }
    
    /**
     * Get recent events (last N events)
     */
    public List<ElectionEvent> getRecentEvents(int count) {
        List<ElectionEvent> allEvents = new ArrayList<>();
        inOrderTraversal(root, allEvents);
        
        // Sort by timestamp descending and take the first N
        allEvents.sort((e1, e2) -> e2.getTimestamp().compareTo(e1.getTimestamp()));
        
        return allEvents.stream()
            .limit(count)
            .toList();
    }
    
    /**
     * Get high priority events
     */
    public List<ElectionEvent> getHighPriorityEvents() {
        List<ElectionEvent> allEvents = new ArrayList<>();
        inOrderTraversal(root, allEvents);
        
        return allEvents.stream()
            .filter(ElectionEvent::isHighPriority)
            .sorted((e1, e2) -> e2.getTimestamp().compareTo(e1.getTimestamp()))
            .toList();
    }
    
    /**
     * In-order traversal to collect all events
     */
    private void inOrderTraversal(EventTreeNode node, List<ElectionEvent> events) {
        if (node != null) {
            inOrderTraversal(node.left, events);
            events.add(node.event);
            inOrderTraversal(node.right, events);
        }
    }
    
    /**
     * Display the event tree structure hierarchically
     */
    public void displayEventTree() {
        if (root == null) {
            System.out.println("No events logged yet.");
            return;
        }
        
        System.out.println("Election Event Hierarchy (organized by time):");
        displayTree(root, "", true);
        
        System.out.println("\n=== Event Summary by Type ===");
        for (ElectionEvent.EventType type : ElectionEvent.EventType.values()) {
            List<ElectionEvent> events = eventsByType.get(type);
            if (!events.isEmpty()) {
                System.out.println(type.getDescription() + ": " + events.size() + " events");
                // Show most recent event of this type
                events.stream()
                    .max(Comparator.comparing(ElectionEvent::getTimestamp))
                    .ifPresent(event -> System.out.println("  Latest: " + event));
            }
        }
        
        System.out.println("\n=== Event Summary by Station ===");
        stationEvents.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                String stationId = entry.getKey();
                List<ElectionEvent> events = entry.getValue();
                System.out.println(stationId + ": " + events.size() + " events");
                
                // Show event type distribution for this station
                Map<ElectionEvent.EventType, Long> typeCount = events.stream()
                    .collect(Collectors.groupingBy(ElectionEvent::getType, Collectors.counting()));
                
                typeCount.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEach(typeEntry -> 
                        System.out.println("  " + typeEntry.getKey() + ": " + typeEntry.getValue()));
            });
    }
    
    /**
     * Display tree structure visually
     */
    private void displayTree(EventTreeNode node, String prefix, boolean isLast) {
        if (node != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
            System.out.println(prefix + (isLast ? "└── " : "├── ") + 
                node.event.getTimestamp().format(formatter) + " - " + 
                node.event.getType().getDescription() + " (" + 
                (node.event.getStationId() != null ? node.event.getStationId() : "SYSTEM") + ")");
            
            if (node.left != null || node.right != null) {
                if (node.right != null) {
                    displayTree(node.right, prefix + (isLast ? "    " : "│   "), node.left == null);
                }
                if (node.left != null) {
                    displayTree(node.left, prefix + (isLast ? "    " : "│   "), true);
                }
            }
        }
    }
    
    /**
     * Get event statistics
     */
    public EventStats getEventStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastHour = now.minusHours(1);
        LocalDateTime lastDay = now.minusDays(1);
        
        List<ElectionEvent> lastHourEvents = getEventsInTimeRange(lastHour, now);
        List<ElectionEvent> lastDayEvents = getEventsInTimeRange(lastDay, now);
        
        int highPriorityCount = (int) lastDayEvents.stream()
            .filter(ElectionEvent::isHighPriority)
            .count();
        
        return new EventStats(totalEvents, lastHourEvents.size(), lastDayEvents.size(), 
                             highPriorityCount, stationEvents.size());
    }
    
    /**
     * Clear old events (older than specified days)
     */
    public int clearOldEvents(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        List<ElectionEvent> eventsToRemove = new ArrayList<>();
        
        // Collect events to remove
        collectOldEvents(root, cutoffDate, eventsToRemove);
        
        // Remove from tracking maps
        for (ElectionEvent event : eventsToRemove) {
            if (event.getStationId() != null) {
                List<ElectionEvent> stationEventList = stationEvents.get(event.getStationId());
                if (stationEventList != null) {
                    stationEventList.remove(event);
                    if (stationEventList.isEmpty()) {
                        stationEvents.remove(event.getStationId());
                    }
                }
            }
            eventsByType.get(event.getType()).remove(event);
        }
        
        // Rebuild tree without old events
        if (!eventsToRemove.isEmpty()) {
            List<ElectionEvent> remainingEvents = new ArrayList<>();
            inOrderTraversal(root, remainingEvents);
            remainingEvents.removeAll(eventsToRemove);
            
            root = null;
            totalEvents = 0;
            
            for (ElectionEvent event : remainingEvents) {
                root = insertEvent(root, event);
                totalEvents++;
            }
        }
        
        return eventsToRemove.size();
    }
    
    /**
     * Collect events older than cutoff date
     */
    private void collectOldEvents(EventTreeNode node, LocalDateTime cutoffDate, List<ElectionEvent> oldEvents) {
        if (node != null) {
            if (node.event.getTimestamp().isBefore(cutoffDate)) {
                oldEvents.add(node.event);
            }
            collectOldEvents(node.left, cutoffDate, oldEvents);
            collectOldEvents(node.right, cutoffDate, oldEvents);
        }
    }
    
    /**
     * Get total number of events
     */
    public int getTotalEvents() {
        return totalEvents;
    }
    
    /**
     * Tree node class for BST
     */
    private static class EventTreeNode {
        ElectionEvent event;
        EventTreeNode left;
        EventTreeNode right;
        
        EventTreeNode(ElectionEvent event) {
            this.event = event;
            this.left = null;
            this.right = null;
        }
    }
    
    /**
     * Event statistics class
     */
    public static class EventStats {
        public final int totalEvents;
        public final int eventsLastHour;
        public final int eventsLastDay;
        public final int highPriorityEvents;
        public final int stationsWithEvents;
        
        EventStats(int total, int lastHour, int lastDay, int highPriority, int stations) {
            this.totalEvents = total;
            this.eventsLastHour = lastHour;
            this.eventsLastDay = lastDay;
            this.highPriorityEvents = highPriority;
            this.stationsWithEvents = stations;
        }
        
        @Override
        public String toString() {
            return String.format("EventStats{total=%d, lastHour=%d, lastDay=%d, highPriority=%d, stations=%d}",
                totalEvents, eventsLastHour, eventsLastDay, highPriorityEvents, stationsWithEvents);
        }
    }
}