package org.Ucel;


import org.Ucel.Exceptions.KeyNotFoundException;

import java.util.*;

public class Graph implements IGraph {
    private Hashtable<String, ILocation> locations = new Hashtable<>();

    @Override
    public List<ILocation> GetLocations() {
        return Collections.list(locations.elements());
    }

    public void AddLocation(ILocation location) {
        locations.put(location.GetId(), location);
    }

    public ILocation GetLocation(String id) {
        return locations.get(id);
    }

    private Hashtable<String, IEdge> edges = new Hashtable<>();

    @Override
    public List<IEdge> GetEdges() {
        return Collections.list(edges.elements());
    }

    public IEdge GetEdge(String id) {
        return edges.get(id);
    }


    public void AddEdge(IEdge edge) throws KeyNotFoundException {
        String startId = edge.GetLocationIdStart();
        String endId = edge.GetLocationIdStart();

        if (!locations.containsKey(startId))
            throw new KeyNotFoundException(String.format("No start location found for ID: %s", startId));
        if (!locations.containsKey(endId))
            throw new KeyNotFoundException(String.format("No end location found for ID: %s", endId));

        edges.put(edge.GetId(), edge);
    }
}
