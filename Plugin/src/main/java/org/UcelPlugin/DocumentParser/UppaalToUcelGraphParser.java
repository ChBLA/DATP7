package org.UcelPlugin.DocumentParser;

import com.uppaal.model.core2.*;
import org.UcelPlugin.Models.SharedInterface.Graph;

import java.util.Hashtable;

public class UppaalToUcelGraphParser {

    public UppaalToUcelGraphParser() {

    }

    public Graph parseGraph(AbstractTemplate uppaalTemplate) {
        Graph newGraph = new Graph();

        visitLocations(uppaalTemplate.first, newGraph);
        visitEdges(uppaalTemplate.first, newGraph);

        return newGraph;
    }

    private void visitLocations(Node node, Graph newGraph) {
        if(node == null)
            return;
        if(!(node instanceof Location)) {
            visitLocations(node.next, newGraph);
            return;
        }

        var location = (Location) node;
        var newLocation = new org.UcelPlugin.Models.SharedInterface.Location();

        newLocation.setPosX(location.getX());
        newLocation.setPosY(location.getY());
        newLocation.setName(location.getName());

        // Optional Properties
        if (location.getProperty(UppaalPropertyNames.Location.invariant) != null)
            newLocation.setInvariant( (String) location.getPropertyValue(UppaalPropertyNames.Location.invariant));

        if (location.getProperty(UppaalPropertyNames.Location.rateOfExponential) != null)
            newLocation.setRateOfExponential( (String) location.getPropertyValue(UppaalPropertyNames.Location.rateOfExponential));

        if (location.getProperty(UppaalPropertyNames.Location.init) != null)
            newLocation.setInitial( (boolean) location.getPropertyValue(UppaalPropertyNames.Location.init));

        if (location.getProperty(UppaalPropertyNames.Location.urgent) != null)
            newLocation.setUrgent( (boolean) location.getPropertyValue(UppaalPropertyNames.Location.urgent));

        if (location.getProperty(UppaalPropertyNames.Location.committed) != null)
            newLocation.setCommitted( (boolean) location.getPropertyValue(UppaalPropertyNames.Location.committed));

        if (location.getProperty(UppaalPropertyNames.Location.comments) != null)
            newLocation.setComments( (String) location.getPropertyValue(UppaalPropertyNames.Location.comments));

        if (location.getProperty(UppaalPropertyNames.Location.testCodeOnEnter) != null)
            newLocation.setTestCodeOnEnter( (String) location.getPropertyValue(UppaalPropertyNames.Location.testCodeOnEnter));

        if (location.getProperty(UppaalPropertyNames.Location.testCodeOnExit) != null)
            newLocation.setTestCodeOnExit( (String) location.getPropertyValue(UppaalPropertyNames.Location.testCodeOnExit));

        locationAssoc.put(location, newLocation);
        newGraph.addLocation(newLocation);

        visitLocations(location.next, newGraph);
    }

    private void visitEdges(Node node, Graph newGraph) {
        if(node == null)
            return;
        if(!(node instanceof Edge)) {
            visitEdges(node.next, newGraph);
            return;
        }

        var edge = (Edge)node;
        var newEdge = new org.UcelPlugin.Models.SharedInterface.Edge();

        newEdge.setLocationStart(locationAssoc.get(edge.getSource()));
        newEdge.setLocationEnd(locationAssoc.get(edge.getTarget()));

        if (edge.getProperty(UppaalPropertyNames.Edge.select) != null)
            newEdge.setSelect( (String) edge.getPropertyValue(UppaalPropertyNames.Edge.select));
        if (edge.getProperty(UppaalPropertyNames.Edge.guard) != null)
            newEdge.setGuard( (String) edge.getPropertyValue(UppaalPropertyNames.Edge.guard));
        if (edge.getProperty(UppaalPropertyNames.Edge.sync) != null)
            newEdge.setSync( (String) edge.getPropertyValue(UppaalPropertyNames.Edge.sync));
        if (edge.getProperty(UppaalPropertyNames.Edge.update) != null)
            newEdge.setUpdate( (String) edge.getPropertyValue(UppaalPropertyNames.Edge.update));
        if (edge.getProperty(UppaalPropertyNames.Edge.comment) != null)
            newEdge.setComment( (String) edge.getPropertyValue(UppaalPropertyNames.Edge.comment));
        if (edge.getProperty(UppaalPropertyNames.Edge.testCode) != null)
            newEdge.setTestCode( (String) edge.getPropertyValue(UppaalPropertyNames.Edge.testCode));

        edgeAssoc.put(edge, newEdge);
        newGraph.addEdge(newEdge);
        visitEdges(edge.next, newGraph);
    }

    private Hashtable<com.uppaal.model.core2.Location, org.UcelPlugin.Models.SharedInterface.Location> locationAssoc = new Hashtable<>();
    private Hashtable<com.uppaal.model.core2.Edge, org.UcelPlugin.Models.SharedInterface.Edge> edgeAssoc = new Hashtable<>();

}
