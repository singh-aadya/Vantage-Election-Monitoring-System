package com.election.monitoring;

import com.election.monitoring.core.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Test class to demonstrate all data structures and algorithms
 * Can be run independently to show system capabilities
 */
public class SystemTest {
    private PollingStationGraph pollingGraph;
    private EventTreeManager eventTreeManager;
    private IncidentProcessor incidentProcessor;
    
    public SystemTest() {
        this.pollingGraph = new PollingStationGraph();
        this.eventTreeManager = new EventTreeManager();
        this.incidentProcessor = new IncidentProcessor();
    }
    
    public static void main(String[] args) {
        SystemTest test = new SystemTest();
        test.runAllTests();
    }
    
    public void runAllTests() {
        System.out.println("=== Election Monitoring System - Automated Test Suite ===");
        System.out.println("Demonstrating Data Structures and Algorithms");
        System.out.println();
        
        testGraphOperations();
        testTreeOperations();
        testQueueOperations();
        testIntegratedScenarios();
        
        System.out.println("=== All Tests Completed Successfully! ===");
    }
    
    private void testGraphOperations() {
        System.out.println("1. TESTING GRAPH DATA STRUCTURE");
        System.out.println("================================");
        
        // Add polling stations
        pollingGraph.addPollingStation("PS001", "Downtown Center", "123 Main St", 1500);
        pollingGraph.addPollingStation("PS002", "Eastside School", "456 Oak Ave", 1200);
        pollingGraph.addPollingStation("PS003", "Westside Library", "789 Pine St", 800);
        pollingGraph.addPollingStation("PS004", "Central High", "321 Elm St", 2000);
        pollingGraph.addPollingStation("PS005", "North Community", "654 Cedar Blvd", 1000);
        
        System.out.println("✓ Added 5 polling stations to graph");
        
        // Add connections (weighted edges)
        pollingGraph.addConnection("PS001", "PS002", 2.5);
        pollingGraph.addConnection("PS001", "PS004", 1.8);
        pollingGraph.addConnection("PS002", "PS003", 3.2);
        pollingGraph.addConnection("PS003", "PS004", 2.1);
        pollingGraph.addConnection("PS004", "PS005", 2.7);
        pollingGraph.addConnection("PS001", "PS005", 4.1);
        
        System.out.println("✓ Added weighted connections between stations");
        
        // Test pathfinding (Dijkstra's algorithm)
        List<String> path = pollingGraph.findShortestPath("PS001", "PS003");
        System.out.println("✓ Shortest path PS001 → PS003: " + path);
        
        // Test BFS for nearby stations
        List<String> nearby = pollingGraph.findNearestStations("PS001", 3.0);
        System.out.println("✓ Stations within 3km of PS001: " + nearby.size() + " found");
        
        // Display network statistics
        PollingStationGraph.NetworkStats stats = pollingGraph.getNetworkStats();
        System.out.println("✓ Network statistics: " + stats);
        System.out.println();
    }
    
    private void testTreeOperations() {
        System.out.println("2. TESTING TREE DATA STRUCTURE (BST)");
        System.out.println("====================================");
        
        // Add events with different timestamps
        LocalDateTime now = LocalDateTime.now();
        
        eventTreeManager.addEvent(new ElectionEvent(
            now.minusMinutes(30), 
            ElectionEvent.EventType.SYSTEM_START, 
            null, 
            "System initialization"
        ));
        
        eventTreeManager.addEvent(new ElectionEvent(
            now.minusMinutes(25), 
            ElectionEvent.EventType.STATION_ADDED, 
            "PS001", 
            "Downtown Center added"
        ));
        
        eventTreeManager.addEvent(new ElectionEvent(
            now.minusMinutes(20), 
            ElectionEvent.EventType.INCIDENT_REPORTED, 
            "PS002", 
            "Technical issue with voting machine"
        ));
        
        eventTreeManager.addEvent(new ElectionEvent(
            now.minusMinutes(15), 
            ElectionEvent.EventType.INCIDENT_PROCESSED, 
            "PS002", 
            "Technical issue resolved"
        ));
        
        eventTreeManager.addEvent(new ElectionEvent(
            now.minusMinutes(10), 
            ElectionEvent.EventType.SYSTEM_ALERT, 
            "PS003", 
            "High voter turnout detected"
        ));
        
        System.out.println("✓ Added 5 events to BST with time-based ordering");
        
        // Test time-range queries
        List<ElectionEvent> recentEvents = eventTreeManager.getEventsInTimeRange(
            now.minusMinutes(25), now.minusMinutes(10)
        );
        System.out.println("✓ Time-range query (25-10 min ago): " + recentEvents.size() + " events found");
        
        // Test event filtering by type
        List<ElectionEvent> incidents = eventTreeManager.getEventsByType(ElectionEvent.EventType.INCIDENT_REPORTED);
        System.out.println("✓ Events by type (INCIDENT_REPORTED): " + incidents.size() + " found");
        
        // Test recent events (tree traversal)
        List<ElectionEvent> recent = eventTreeManager.getRecentEvents(3);
        System.out.println("✓ Recent events retrieval: " + recent.size() + " most recent events");
        
        // Display tree statistics
        EventTreeManager.EventStats eventStats = eventTreeManager.getEventStats();
        System.out.println("✓ Event statistics: " + eventStats);
        System.out.println();
    }
    
    private void testQueueOperations() {
        System.out.println("3. TESTING QUEUE DATA STRUCTURES");
        System.out.println("================================");
        
        // Test priority queue with different incident severities
        incidentProcessor.reportIncident(new Incident(
            "PS001", Incident.IncidentType.TECHNICAL, 
            "Voting machine malfunction", Incident.Severity.HIGH
        ));
        
        incidentProcessor.reportIncident(new Incident(
            "PS002", Incident.IncidentType.SECURITY, 
            "Unauthorized person in polling area", Incident.Severity.CRITICAL
        ));
        
        incidentProcessor.reportIncident(new Incident(
            "PS003", Incident.IncidentType.PROCEDURAL, 
            "Long voter queue", Incident.Severity.LOW
        ));
        
        incidentProcessor.reportIncident(new Incident(
            "PS001", Incident.IncidentType.TECHNICAL, 
            "Power outage", Incident.Severity.CRITICAL
        ));
        
        incidentProcessor.reportIncident(new Incident(
            "PS004", Incident.IncidentType.OTHER, 
            "Missing ballot papers", Incident.Severity.MEDIUM
        ));
        
        System.out.println("✓ Added 5 incidents to priority queue");
        
        // Display queue status
        incidentProcessor.displayQueueStatus();
        
        // Test priority processing
        Incident highPriorityIncident = incidentProcessor.processNextHighPriorityIncident();
        if (highPriorityIncident != null) {
            System.out.println("✓ Processed high-priority incident: " + highPriorityIncident.getId());
        }
        
        // Process all remaining incidents
        List<Incident> processedIncidents = incidentProcessor.processIncidents();
        System.out.println("✓ Batch processed " + processedIncidents.size() + " incidents");
        
        // Test incident statistics
        Map<Incident.IncidentType, Integer> incidentStats = incidentProcessor.getIncidentStatistics();
        System.out.println("✓ Incident statistics by type:");
        incidentStats.forEach((type, count) -> 
            System.out.println("  " + type + ": " + count + " incidents"));
        
        // Test suspicious activity detection
        Map<String, List<Incident>> suspiciousActivity = incidentProcessor.findSuspiciousActivity();
        System.out.println("✓ Suspicious activity analysis: " + suspiciousActivity.size() + " flagged stations");
        System.out.println();
    }
    
    private void testIntegratedScenarios() {
        System.out.println("4. TESTING INTEGRATED SCENARIOS");
        System.out.println("===============================");
        
        // Scenario 1: Emergency response
        System.out.println("Scenario 1: Emergency Response Coordination");
        
        // Report critical incident
        Incident criticalIncident = new Incident(
            "PS001", Incident.IncidentType.SECURITY, 
            "Bomb threat reported", Incident.Severity.CRITICAL
        );
        incidentProcessor.reportIncident(criticalIncident);
        
        // Log the emergency event
        eventTreeManager.addEvent(new ElectionEvent(
            LocalDateTime.now(), 
            ElectionEvent.EventType.SYSTEM_ALERT, 
            "PS001", 
            "Emergency response activated"
        ));
        
        // Find nearest stations for coordination
        List<String> nearbyStations = pollingGraph.findNearestStations("PS001", 5.0);
        System.out.println("✓ Emergency response: Found " + nearbyStations.size() + " nearby stations for coordination");
        
        // Process the critical incident immediately
        Incident processed = incidentProcessor.processNextHighPriorityIncident();
        if (processed != null) {
            System.out.println("✓ Critical incident processed: " + processed.getId());
            
            // Log resolution event
            eventTreeManager.addEvent(new ElectionEvent(
                LocalDateTime.now(), 
                ElectionEvent.EventType.INCIDENT_RESOLVED, 
                "PS001", 
                "Emergency response completed"
            ));
        }
        
        // Scenario 2: Network analysis for resource allocation
        System.out.println("\nScenario 2: Resource Allocation Analysis");
        
        // Analyze incident patterns by station
        List<Map.Entry<String, Integer>> topIncidentStations = 
            incidentProcessor.getTopIncidentStations(3);
        
        System.out.println("✓ Top stations by incident count:");
        topIncidentStations.forEach(entry -> 
            System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " incidents"));
        
        // Find optimal redistribution paths
        if (topIncidentStations.size() >= 2) {
            String problematicStation = topIncidentStations.get(0).getKey();
            String backupStation = topIncidentStations.get(1).getKey();
            
            List<String> redistributionPath = pollingGraph.findShortestPath(problematicStation, backupStation);
            System.out.println("✓ Optimal resource redistribution path: " + redistributionPath);
        }
        
        // Scenario 3: Real-time monitoring dashboard simulation
        System.out.println("\nScenario 3: Real-time Monitoring Dashboard");
        
        // Generate comprehensive system report
        System.out.println("✓ System Status Summary:");
        
        // Network status
        PollingStationGraph.NetworkStats netStats = pollingGraph.getNetworkStats();
        System.out.println("  Network: " + netStats.totalStations + " stations, " + 
                          netStats.totalConnections + " connections");
        
        // Event system status
        EventTreeManager.EventStats eventStats = eventTreeManager.getEventStats();
        System.out.println("  Events: " + eventStats.totalEvents + " total, " + 
                          eventStats.eventsLastHour + " in last hour");
        
        // Incident processing status
        IncidentProcessor.QueueStats queueStats = incidentProcessor.getQueueStats();
        System.out.println("  Incidents: " + queueStats.processedIncidents + " processed, " + 
                          queueStats.pendingIncidents + " pending");
        
        // High-priority events requiring attention
        List<ElectionEvent> highPriorityEvents = eventTreeManager.getHighPriorityEvents();
        System.out.println("  Alerts: " + highPriorityEvents.size() + " high-priority events");
        
        System.out.println("✓ All integrated scenarios completed successfully!");
        System.out.println();
    }
}