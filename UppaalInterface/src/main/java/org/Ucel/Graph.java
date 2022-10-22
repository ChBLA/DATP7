package org.Ucel;


import org.Ucel.Exceptions.KeyNotFoundException;

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

    public void AddEdge(IEdge edge) throws IllegalArgumentException {
        if (!locations.contains(edge.getLocationStart()))
            throw new IllegalArgumentException("Start location of edge not found in graph");
        if (!locations.contains(edge.getLocationEnd()))
            throw new IllegalArgumentException("End location of edge not found in graph");

        edges.add(edge);
    }
}
