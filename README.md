# Election Monitoring System

A comprehensive console-based Election Monitoring System implemented in Java that demonstrates core data structures and algorithms for tracking and reporting election incidents.

## Features

### Data Structures Implementation

1. **Graphs** - Polling station network management
   - Adjacency list representation for station connections
   - Weighted edges representing distances between stations
   - Dijkstra's algorithm for shortest path finding
   - BFS for finding nearby stations

2. **Trees** - Hierarchical event logging
   - Binary Search Tree (BST) for time-ordered event management
   - In-order traversal for chronological event display
   - Efficient time-range queries
   - Event categorization and statistics

3. **Queues** - Real-time incident processing
   - Priority queue for incident severity handling
   - FIFO queue for recent incident tracking
   - Batch processing capabilities
   - Queue statistics and monitoring

4. **Hash Maps** - Fast data lookup and organization
   - Station-based incident tracking
   - Event type categorization
   - Statistical analysis and reporting

## System Architecture

```
Election Monitoring System
├── Core Components
│   ├── PollingStationGraph (Graph-based station network)
│   ├── EventTreeManager (BST-based event logging)
│   ├── IncidentProcessor (Queue-based incident handling)
│   └── Data Models (Station, Incident, Event)
└── Console Interface (Interactive command system)
```

## Key Classes

### PollingStation
- Represents individual polling stations with capacity, location, and status
- Supports utilization tracking and capacity management

### PollingStationGraph
- Graph implementation using adjacency lists
- Supports adding stations, connections, and pathfinding
- Implements Dijkstra's algorithm for shortest paths
- BFS for finding stations within distance ranges

### Incident
- Comprehensive incident model with types, severity levels, and status tracking
- Comparable implementation for priority queue sorting
- Rich incident lifecycle management

### IncidentProcessor
- Priority queue-based incident processing
- Suspicious activity pattern detection
- Statistical analysis and reporting
- Real-time queue monitoring

### ElectionEvent
- Event model for audit trail and logging
- Time-based categorization and prioritization
- Comprehensive event metadata

### EventTreeManager
- BST implementation for time-ordered event storage
- Efficient time-range queries
- Hierarchical event display and statistics
- Event lifecycle management

## Console Commands

1. **Add Polling Station** - Create new polling stations in the network
2. **Add Station Connection** - Connect stations with distance measurements
3. **Report Incident** - Log incidents with type, severity, and description
4. **Process Incident Queue** - Handle pending incidents by priority
5. **Query Station Information** - Detailed station data and incident history
6. **Find Nearest Stations** - Distance-based station discovery
7. **View Event Tree** - Hierarchical display of all logged events
8. **Generate Summary Report** - Comprehensive system statistics
9. **View Suspicious Activity** - Pattern analysis for unusual incident clusters
10. **Display Station Network** - Visual network topology

## Algorithms Demonstrated

### Graph Algorithms
- **Dijkstra's Algorithm**: Shortest path between stations
- **Breadth-First Search (BFS)**: Finding stations within distance radius
- **Graph Traversal**: Network topology analysis

### Tree Algorithms
- **Binary Search Tree Operations**: Insert, search, traversal
- **In-Order Traversal**: Chronological event listing
- **Range Queries**: Time-based event filtering

### Queue Algorithms
- **Priority Queue Processing**: Severity-based incident handling
- **FIFO Queue Management**: Recent incident tracking
- **Batch Processing**: Efficient incident resolution

### Search and Sort Algorithms
- **Binary Search**: Time-based event lookups
- **Comparator-based Sorting**: Multi-criteria incident prioritization
- **Stream Processing**: Statistical analysis and filtering

## Sample Usage

```bash
# Initialize system with sample data
java ElectionMonitoringSystem

# Example workflow:
1. Add Polling Station
2. Report Critical Incident
3. Process Incident Queue
4. View Event Tree
5. Generate Summary Report
```

## Data Structures Complexity

| Operation | Graph | Tree | Queue | HashMap |
|-----------|-------|------|-------|---------|
| Insert    | O(1)  | O(log n) | O(log n) | O(1) avg |
| Search    | O(V+E)| O(log n) | O(n)     | O(1) avg |
| Delete    | O(V)  | O(log n) | O(log n) | O(1) avg |
| Traversal | O(V+E)| O(n)     | O(n)     | O(n)     |

## Key Design Patterns

1. **Strategy Pattern**: Different incident processing strategies based on severity
2. **Observer Pattern**: Event logging for system actions
3. **Factory Pattern**: Event and incident creation
4. **Template Method**: Statistical analysis algorithms

## Build and Run

```bash
# Compile all Java files
javac -d out -sourcepath src src/main/java/com/election/monitoring/*.java src/main/java/com/election/monitoring/core/*.java

# Run the application
java -cp out com.election.monitoring.ElectionMonitoringSystem
```

## Sample Data

The system initializes with sample polling stations and connections to demonstrate functionality:
- 4 polling stations with different capacities
- Network connections with realistic distances
- Pre-configured for immediate testing

## Future Enhancements

1. **Database Integration**: Persistent storage for production use
2. **REST API**: Web service endpoints for remote access
3. **Advanced Analytics**: Machine learning for pattern detection
4. **Real-time Notifications**: Alert system for critical incidents
5. **Geographic Mapping**: GIS integration for station visualization

---

*This system demonstrates practical application of fundamental data structures and algorithms in a real-world election monitoring context, suitable for technical interviews and educational purposes.*#
