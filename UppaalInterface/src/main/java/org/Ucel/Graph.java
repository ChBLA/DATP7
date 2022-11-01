package org.Ucel;


import java.util.*;

public class Graph implements IGraph {
    private ArrayList<ILocation> locations = new ArrayList<>();

    @Override
    public List<ILocation> getLocations() {
        return locations;
    }

    public void addLocation(ILocation location) {
        locations.add(location);
    }

    private ArrayList<IEdge> edges = new ArrayList<>();

    @Override
    public List<IEdge> getEdges() {
        return edges;
    }

    public void addEdge(IEdge edge) throws IllegalArgumentException {
        if (!locations.contains(edge.getLocationStart()))
            throw new IllegalArgumentException("Start location of edge not found in graph");
        if (!locations.contains(edge.getLocationEnd()))
            throw new IllegalArgumentException("End location of edge not found in graph");

        edges.add(edge);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Graph graph = (Graph) o;
        return Objects.equals(locations, graph.locations) && Objects.equals(edges, graph.edges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locations, edges);
    }
}
