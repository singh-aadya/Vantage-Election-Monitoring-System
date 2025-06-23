package com.election.monitoring;

import com.election.monitoring.core.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Main console application for the Election Monitoring System
 * Demonstrates various data structures and algorithms for election monitoring
 */
public class ElectionMonitoringSystem {
    private PollingStationGraph pollingGraph;
    private EventTreeManager eventTreeManager;
    private IncidentProcessor incidentProcessor;
    private Scanner scanner;
    private boolean running;
    
    public ElectionMonitoringSystem() {
        this.pollingGraph = new PollingStationGraph();
        this.eventTreeManager = new EventTreeManager();
        this.incidentProcessor = new IncidentProcessor();
        this.scanner = new Scanner(System.in);
        this.running = true;
    }
    
    public static void main(String[] args) {
        ElectionMonitoringSystem system = new ElectionMonitoringSystem();
        system.run();
    }
    
    public void run() {
        System.out.println("=== Election Monitoring System ===");
        System.out.println("Using Data Structures: Graphs, Trees, Queues, Hash Maps");
        System.out.println();
        
        // Initialize with sample data
        initializeSampleData();
        
        while (running) {
            displayMenu();
            processCommand();
        }
        
        scanner.close();
    }
    
    private void initializeSampleData() {
        // Add sample polling stations
        pollingGraph.addPollingStation("PS001", "Downtown Community Center", "123 Main St", 1500);
        pollingGraph.addPollingStation("PS002", "Eastside School", "456 Oak Ave", 1200);
        pollingGraph.addPollingStation("PS003", "Westside Library", "789 Pine St", 800);
        pollingGraph.addPollingStation("PS004", "Central High School", "321 Elm St", 2000);
        
        // Add connections between nearby stations
        pollingGraph.addConnection("PS001", "PS002", 2.5);
        pollingGraph.addConnection("PS001", "PS004", 1.8);
        pollingGraph.addConnection("PS002", "PS003", 3.2);
        pollingGraph.addConnection("PS003", "PS004", 2.1);
        
        System.out.println("Sample polling stations and connections initialized.");
        System.out.println();
    }
    
    private void displayMenu() {
        System.out.println("--- Election Monitoring Commands ---");
        System.out.println("1. Add Polling Station");
        System.out.println("2. Add Station Connection");
        System.out.println("3. Report Incident");
        System.out.println("4. Process Incident Queue");
        System.out.println("5. Query Station Information");
        System.out.println("6. Find Nearest Stations");
        System.out.println("7. View Event Tree");
        System.out.println("8. Generate Summary Report");
        System.out.println("9. View Suspicious Activity");
        System.out.println("10. Display Station Network");
        System.out.println("0. Exit");
        System.out.print("Enter command: ");
    }
    
    private void processCommand() {
        try {
            int command = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (command) {
                case 1: addPollingStation(); break;
                case 2: addStationConnection(); break;
                case 3: reportIncident(); break;
                case 4: processIncidentQueue(); break;
                case 5: queryStationInfo(); break;
                case 6: findNearestStations(); break;
                case 7: viewEventTree(); break;
                case 8: generateSummaryReport(); break;
                case 9: viewSuspiciousActivity(); break;
                case 10: displayStationNetwork(); break;
                case 0: running = false; System.out.println("System shutting down..."); break;
                default: System.out.println("Invalid command. Please try again.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // clear invalid input
        }
        System.out.println();
    }
    
    private void addPollingStation() {
        System.out.print("Enter Station ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Station Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Address: ");
        String address = scanner.nextLine();
        System.out.print("Enter Capacity: ");
        int capacity = scanner.nextInt();
        scanner.nextLine();
        
        pollingGraph.addPollingStation(id, name, address, capacity);
        eventTreeManager.addEvent(new ElectionEvent(
            LocalDateTime.now(),
            ElectionEvent.EventType.STATION_ADDED,
            id,
            "Station " + name + " added to network"
        ));
        System.out.println("Polling station added successfully!");
    }
    
    private void addStationConnection() {
        System.out.print("Enter Station 1 ID: ");
        String station1 = scanner.nextLine();
        System.out.print("Enter Station 2 ID: ");
        String station2 = scanner.nextLine();
        System.out.print("Enter Distance (km): ");
        double distance = scanner.nextDouble();
        scanner.nextLine();
        
        pollingGraph.addConnection(station1, station2, distance);
        System.out.println("Connection added successfully!");
    }
    
    private void reportIncident() {
        System.out.print("Enter Station ID: ");
        String stationId = scanner.nextLine();
        System.out.print("Enter Incident Type (TECHNICAL/SECURITY/PROCEDURAL/OTHER): ");
        String typeStr = scanner.nextLine().toUpperCase();
        System.out.print("Enter Description: ");
        String description = scanner.nextLine();
        System.out.print("Enter Severity (LOW/MEDIUM/HIGH/CRITICAL): ");
        String severityStr = scanner.nextLine().toUpperCase();
        
        try {
            Incident.IncidentType type = Incident.IncidentType.valueOf(typeStr);
            Incident.Severity severity = Incident.Severity.valueOf(severityStr);
            
            Incident incident = new Incident(stationId, type, description, severity);
            incidentProcessor.reportIncident(incident);
            
            // Add to event tree
            eventTreeManager.addEvent(new ElectionEvent(
                LocalDateTime.now(),
                ElectionEvent.EventType.INCIDENT_REPORTED,
                stationId,
                description
            ));
            
            System.out.println("Incident reported and added to processing queue!");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid incident type or severity. Please check your input.");
        }
    }
    
    private void processIncidentQueue() {
        List<Incident> processedIncidents = incidentProcessor.processIncidents();
        if (processedIncidents.isEmpty()) {
            System.out.println("No incidents in queue to process.");
        } else {
            System.out.println("Processed " + processedIncidents.size() + " incident(s):");
            for (Incident incident : processedIncidents) {
                System.out.println("- " + incident);
                
                // Add processing event to tree
                eventTreeManager.addEvent(new ElectionEvent(
                    LocalDateTime.now(),
                    ElectionEvent.EventType.INCIDENT_PROCESSED,
                    incident.getStationId(),
                    "Processed: " + incident.getDescription()
                ));
            }
        }
    }
    
    private void queryStationInfo() {
        System.out.print("Enter Station ID: ");
        String stationId = scanner.nextLine();
        
        PollingStation station = pollingGraph.getStation(stationId);
        if (station != null) {
            System.out.println("Station Information:");
            System.out.println(station);
            
            List<String> connections = pollingGraph.getConnectedStations(stationId);
            System.out.println("Connected Stations: " + connections);
            
            List<Incident> incidents = incidentProcessor.getIncidentsByStation(stationId);
            System.out.println("Total Incidents: " + incidents.size());
            
            if (!incidents.isEmpty()) {
                System.out.println("Recent Incidents:");
                incidents.stream().limit(3).forEach(System.out::println);
            }
        } else {
            System.out.println("Station not found.");
        }
    }
    
    private void findNearestStations() {
        System.out.print("Enter Station ID: ");
        String stationId = scanner.nextLine();
        System.out.print("Enter maximum distance (km): ");
        double maxDistance = scanner.nextDouble();
        scanner.nextLine();
        
        List<String> nearestStations = pollingGraph.findNearestStations(stationId, maxDistance);
        if (nearestStations.isEmpty()) {
            System.out.println("No stations found within " + maxDistance + " km.");
        } else {
            System.out.println("Nearest stations within " + maxDistance + " km:");
            nearestStations.forEach(System.out::println);
        }
    }
    
    private void viewEventTree() {
        System.out.println("=== Election Event Hierarchy ===");
        eventTreeManager.displayEventTree();
    }
    
    private void generateSummaryReport() {
        System.out.println("=== Election Monitoring Summary Report ===");
        System.out.println("Generated at: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        System.out.println();
        
        // Station statistics
        Map<String, PollingStation> stations = pollingGraph.getAllStations();
        System.out.println("Total Polling Stations: " + stations.size());
        
        // Incident statistics
        Map<Incident.IncidentType, Integer> incidentStats = incidentProcessor.getIncidentStatistics();
        System.out.println("Incident Statistics:");
        incidentStats.forEach((type, count) -> 
            System.out.println("  " + type + ": " + count));
        
        // Event statistics
        System.out.println("Total Events Logged: " + eventTreeManager.getTotalEvents());
        
        // High-priority incidents
        List<Incident> criticalIncidents = incidentProcessor.getCriticalIncidents();
        System.out.println("Critical Incidents: " + criticalIncidents.size());
        
        System.out.println();
        System.out.println("Report generation complete.");
    }
    
    private void viewSuspiciousActivity() {
        System.out.println("=== Suspicious Activity Analysis ===");
        
        // Find stations with multiple recent incidents
        Map<String, List<Incident>> suspiciousStations = incidentProcessor.findSuspiciousActivity();
        
        if (suspiciousStations.isEmpty()) {
            System.out.println("No suspicious activity detected.");
        } else {
            System.out.println("Stations with suspicious activity patterns:");
            suspiciousStations.forEach((stationId, incidents) -> {
                System.out.println("Station " + stationId + ": " + incidents.size() + " incidents");
                incidents.forEach(incident -> System.out.println("  - " + incident));
            });
        }
    }
    
    private void displayStationNetwork() {
        System.out.println("=== Polling Station Network ===");
        pollingGraph.displayNetwork();
    }
}