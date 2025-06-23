package com.election.monitoring.core;

import java.util.*;

/**
 * Graph-based implementation for managing polling stations and their relationships
 * Uses adjacency list representation with weighted edges (distances)
 */
public class PollingStationGraph {
    private Map<String, PollingStation> stations;
    private Map<String, Map<String, Double>> adjacencyList;
    
    public PollingStationGraph() {
        this.stations = new HashMap<>();
        this.adjacencyList = new HashMap<>();
    }
    
    /**
     * Add a polling station to the graph
     */
    public void addPollingStation(String id, String name, String address, int capacity) {
        if (stations.containsKey(id)) {
            System.out.println("Station " + id + " already exists!");
            return;
        }
        
        PollingStation station = new PollingStation(id, name, address, capacity);
        stations.put(id, station);
        adjacencyList.put(id, new HashMap<>());
    }
    
    /**
     * Add a bidirectional connection between two stations with distance
     */
    public void addConnection(String station1Id, String station2Id, double distance) {
        if (!stations.containsKey(station1Id) || !stations.containsKey(station2Id)) {
            System.out.println("One or both stations do not exist!");
            return;
        }
        
        if (station1Id.equals(station2Id)) {
            System.out.println("Cannot connect a station to itself!");
            return;
        }
        
        // Add bidirectional connection
        adjacencyList.get(station1Id).put(station2Id, distance);
        adjacencyList.get(station2Id).put(station1Id, distance);
    }
    
    /**
     * Get a station by ID
     */
    public PollingStation getStation(String id) {
        return stations.get(id);
    }
    
    /**
     * Get all stations
     */
    public Map<String, PollingStation> getAllStations() {
        return new HashMap<>(stations);
    }
    
    /**
     * Get connected stations for a given station
     */
    public List<String> getConnectedStations(String stationId) {
        if (!adjacencyList.containsKey(stationId)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(adjacencyList.get(stationId).keySet());
    }
    
    /**
     * Find nearest stations within a given distance using BFS
     */
    public List<String> findNearestStations(String startStationId, double maxDistance) {
        if (!stations.containsKey(startStationId)) {
            return new ArrayList<>();
        }
        
        List<String> nearbyStations = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Queue<StationDistance> queue = new LinkedList<>();
        
        queue.offer(new StationDistance(startStationId, 0.0));
        visited.add(startStationId);
        
        while (!queue.isEmpty()) {
            StationDistance current = queue.poll();
            
            // Skip the starting station itself
            if (!current.stationId.equals(startStationId) && current.distance <= maxDistance) {
                nearbyStations.add(current.stationId + " (" + String.format("%.1f", current.distance) + " km)");
            }
            
            // Explore neighbors
            Map<String, Double> neighbors = adjacencyList.get(current.stationId);
            if (neighbors != null) {
                for (Map.Entry<String, Double> neighbor : neighbors.entrySet()) {
                    String neighborId = neighbor.getKey();
                    double newDistance = current.distance + neighbor.getValue();
                    
                    if (!visited.contains(neighborId) && newDistance <= maxDistance) {
                        visited.add(neighborId);
                        queue.offer(new StationDistance(neighborId, newDistance));
                    }
                }
            }
        }
        
        return nearbyStations;
    }
    
    /**
     * Find shortest path between two stations using Dijkstra's algorithm
     */
    public List<String> findShortestPath(String startId, String endId) {
        if (!stations.containsKey(startId) || !stations.containsKey(endId)) {
            return new ArrayList<>();
        }
        
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<StationDistance> pq = new PriorityQueue<>(Comparator.comparing(sd -> sd.distance));
        Set<String> visited = new HashSet<>();
        
        // Initialize distances
        for (String stationId : stations.keySet()) {
            distances.put(stationId, Double.MAX_VALUE);
        }
        distances.put(startId, 0.0);
        pq.offer(new StationDistance(startId, 0.0));
        
        while (!pq.isEmpty()) {
            StationDistance current = pq.poll();
            
            if (visited.contains(current.stationId)) {
                continue;
            }
            
            visited.add(current.stationId);
            
            if (current.stationId.equals(endId)) {
                break;
            }
            
            Map<String, Double> neighbors = adjacencyList.get(current.stationId);
            if (neighbors != null) {
                for (Map.Entry<String, Double> neighbor : neighbors.entrySet()) {
                    String neighborId = neighbor.getKey();
                    double newDistance = current.distance + neighbor.getValue();
                    
                    if (newDistance < distances.get(neighborId)) {
                        distances.put(neighborId, newDistance);
                        previous.put(neighborId, current.stationId);
                        pq.offer(new StationDistance(neighborId, newDistance));
                    }
                }
            }
        }
        
        // Reconstruct path
        List<String> path = new ArrayList<>();
        String currentStation = endId;
        
        while (currentStation != null) {
            path.add(0, currentStation);
            currentStation = previous.get(currentStation);
        }
        
        // Return empty list if no path found
        return path.size() > 1 && path.get(0).equals(startId) ? path : new ArrayList<>();
    }
    
    /**
     * Display the entire network structure
     */
    public void displayNetwork() {
        System.out.println("Polling Station Network:");
        for (Map.Entry<String, PollingStation> entry : stations.entrySet()) {
            String stationId = entry.getKey();
            PollingStation station = entry.getValue();
            
            System.out.println(station);
            
            Map<String, Double> connections = adjacencyList.get(stationId);
            if (!connections.isEmpty()) {
                System.out.println("  Connected to:");
                for (Map.Entry<String, Double> connection : connections.entrySet()) {
                    System.out.println("    " + connection.getKey() + " (" + 
                        String.format("%.1f", connection.getValue()) + " km)");
                }
            } else {
                System.out.println("  No connections");
            }
            System.out.println();
        }
    }
    
    /**
     * Get network statistics
     */
    public NetworkStats getNetworkStats() {
        int totalStations = stations.size();
        int totalConnections = adjacencyList.values().stream()
            .mapToInt(Map::size)
            .sum() / 2; // Divide by 2 since connections are bidirectional
        
        double averageConnections = totalStations > 0 ? (double) totalConnections * 2 / totalStations : 0;
        
        return new NetworkStats(totalStations, totalConnections, averageConnections);
    }
    
    /**
     * Helper class for distance calculations
     */
    private static class StationDistance {
        String stationId;
        double distance;
        
        StationDistance(String stationId, double distance) {
            this.stationId = stationId;
            this.distance = distance;
        }
    }
    
    /**
     * Network statistics class
     */
    public static class NetworkStats {
        public final int totalStations;
        public final int totalConnections;
        public final double averageConnections;
        
        NetworkStats(int totalStations, int totalConnections, double averageConnections) {
            this.totalStations = totalStations;
            this.totalConnections = totalConnections;
            this.averageConnections = averageConnections;
        }
        
        @Override
        public String toString() {
            return String.format("NetworkStats{stations=%d, connections=%d, avgConnections=%.1f}",
                totalStations, totalConnections, averageConnections);
        }
    }
}