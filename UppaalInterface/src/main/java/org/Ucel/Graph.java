package org.Ucel;


import org.Ucel.Exceptions.KeyNotFoundException;

import java.util.*;

public class Graph implements IGraph {
    private Hashtable<String, ILocation> locations = new Hashtable<>();

    @Override
    public List<ILocation> getLocations() {
        return Collections.list(locations.elements());
    }

    public void putLocation(ILocation location) {
        locations.put(location.getId(), location);
    }

    public ILocation getLocation(String id) {
        return locations.get(id);
    }

    private Hashtable<String, IEdge> edges = new Hashtable<>();

    @Override
    public List<IEdge> getEdges() {
        return Collections.list(edges.elements());
    }

    public IEdge getEdge(String id) {
        return edges.get(id);
    }


    public void putEdge(IEdge edge) throws KeyNotFoundException {
        String startId = edge.getLocationIdStart();
        String endId = edge.getLocationIdStart();

        if (!locations.containsKey(startId))
            throw new KeyNotFoundException(String.format("No start location found for ID: %s", startId));
        if (!locations.containsKey(endId))
            throw new KeyNotFoundException(String.format("No end location found for ID: %s", endId));

        edges.put(edge.getId(), edge);
    }
}
