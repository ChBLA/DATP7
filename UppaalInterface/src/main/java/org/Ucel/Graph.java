package org.Ucel;


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

    private Hashtable<String, IEdge> edges = new Hashtable<>();
    @Override
    public List<IEdge> GetEdges() {
        return Collections.list(edges.elements());
    }

    public void AddEdge(IEdge edge) {
//        if(!locations.contains(edge.GetLocationIdStart()))
//            throw new Exception("");
//        if(!locations.contains(edge.GetLocationIdEnd()))
//            throw new Exception("");

        edges.put(edge.GetId(), edge);

    }

}
